package kr.yuns.file.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import kr.yuns.file.data.dto.FileResponseDto;
import kr.yuns.file.data.enums.FileDivision;

public interface FileService {
    ResponseEntity<FileResponseDto> saveFile(MultipartFile multipartFile, FileDivision division, Long userId) throws IOException;
}