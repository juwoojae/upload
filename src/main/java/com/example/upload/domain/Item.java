package com.example.upload.domain;

import lombok.Data;

import java.util.List;

@Data
public class Item {

    private Long id;
    private String itemName;
    private UploadFile attachFile;
    private List<UploadFile> imageFiles; // 이미지는 여러개의 파일을 업로드 할수 있어야 한다.
}
