package com.example.upload.controller;

import com.example.upload.domain.UploadFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ItemForm {

    private Long itemId;
    private String itemName;
    private MultipartFile attachFile; // 멀티 파트는 @ModelAttribute 에서 사용할수 있다.
    private List<MultipartFile> ImageFiles; // 이미지 다중 업로드 용.
}
