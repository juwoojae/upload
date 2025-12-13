package com.example.upload.controller;

import com.example.upload.domain.Item;
import com.example.upload.domain.ItemRepository;
import com.example.upload.domain.UploadFile;
import com.example.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final FileStore fileStores;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form) {
        return "item-form";
    }

    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {

        UploadFile attachFile = fileStores.storeFile(form.getAttachFile());
        List<UploadFile> storeImageFiles = fileStores.storeFiles(form.getImageFiles());

        /**
         * 사실 데이터 베이스에는 파일을 저장하지 않고, 경로만 저장한다. 실제 파일 저장은 storage 나 AWS 의 s3 에 저장을 한다
         * 그리고 DB 에서 fullPath 를 다 저장하는것이 아니라, 상대적인 경로만을 저장함
         */
        // 데이터베이스에 저장
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);



        redirectAttributes.addAttribute("itemId", item.getId());

        return  "redirect:/items/{itemId}";
    }

    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id);
        model.addAttribute("item", item);
        return "item-view";
    }

}