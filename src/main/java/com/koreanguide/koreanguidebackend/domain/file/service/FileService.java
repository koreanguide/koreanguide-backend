package com.koreanguide.koreanguidebackend.domain.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String saveFile(MultipartFile multipartFile, Long userId) throws IOException;
}
