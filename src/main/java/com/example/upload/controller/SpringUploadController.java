package com.example.upload.controller;

import com.example.upload.domain.Item;
import com.example.upload.domain.ItemRepository;
import com.example.upload.file.FileStore;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequestMapping("/spring")
@RequiredArgsConstructor
public class SpringUploadController {

    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    /**
     * 브라우저나 클라이언트가 파일 업로드를 보낼때 , 바이너리 형식으로 오는데,
     * Content-Type: multipart/form-data; boundary=----xxxx 형식으로 요청이 들어온다. 이것을 MultipartFormDate
     *
     * 이 요청을 받으면 MultipartResolver 가 요청을 가로채서 멀티파트 바디를 파싱한후
     * 파일을 MultipartFile 객체로 바꾼후 받는다
     *
     * 만약 Multipart + json 이 같이 오는 경우를 생각해보면
     * @RequestPart 를 사용해야 한다. @RequestBody 는 multipart 에서 작동 안 함
     * @PostMapping("/upload")
     * public void upload(@RequestPart("file") MultipartFile file,
     *                    @RequestPart("data") MyDto dto) 이런 형식으로
     */

    @PostMapping("/upload")
    public String saveFile(@RequestParam String itemName,
                           @RequestParam MultipartFile file, HttpServletRequest request) throws ServletException, IOException
    {
        log.info("request={}", request);
        log.info("itemName={}", itemName);
        log.info("multipartFile={}", file);

        if (!file.isEmpty()) {
            String fullPath = fileDir + file.getOriginalFilename(); //업로드 파일 명
            log.info("파일 저장 fullPath={}", fullPath);
            file.transferTo(new File(fullPath)); // 파일객체로 파일 저장
        }

        return "upload-form";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId)
            throws MalformedURLException {
        Item item = itemRepository.findById(itemId);
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();
        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));
        log.info("uploadFileName={}", uploadFileName);
        String encodedUploadFileName = UriUtils.encode(uploadFileName,
                StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" +
                encodedUploadFileName + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
}
