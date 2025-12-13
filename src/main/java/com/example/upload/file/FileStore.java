package com.example.upload.file;

import com.example.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String fileName) {

        return fileDir + fileName;
    };

    /**
     * 파일이 여러개 들어 왔을때 처리
     */
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {

        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()) { //multipartFiles 이 비어있지 않다면
                storeFileResult.add(storeFile(multipartFile)); // 추가
            }
        }
        return storeFileResult;
    }

    public UploadFile  storeFile(MultipartFile multipartFile) throws IOException {

        if (multipartFile.isEmpty()){
           return null;
        }
        String originalFilename = multipartFile.getOriginalFilename(); //파일 이름 가지고 오기
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return new UploadFile(originalFilename, storeFileName);
    }

    /**
     * 서버 내부에서 관리하는 파일명은 유일한 이름을 생성하는 UUID 를 사용해서 충돌하지 않도록 하는 메서드.
     */
    private static String createStoreFileName(String originalFilename) {

        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    /**
     * 확장자를 붙여주는 메서드
     * 확장자를 별도로 추출해서 서버 내부에서 관리하는 파일 명에도 붙여준다
     * ex) werk1234jkf  + .png     와 같이 추출
     */
    private static String extractExt(String originalFilename) {

        int pos = originalFilename.lastIndexOf('.'); // 인덱스 가지고 오기
        return originalFilename.substring(pos + 1); // 확장자 뽑기
    }

}
