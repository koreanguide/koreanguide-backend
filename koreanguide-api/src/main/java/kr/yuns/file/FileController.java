package kr.yuns.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import kr.yuns.JwtTokenProvider;
import kr.yuns.auth.data.dao.UserDao;
import kr.yuns.file.data.dto.FileResponseDto;
import kr.yuns.file.data.enums.FileDivision;
import kr.yuns.file.service.FileService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/file")
@Slf4j
public class FileController {
    private final FileService fileService;
    private final JwtTokenProvider tokenProvider;
    private final UserDao userDao;

    @Autowired
    public FileController(FileService fileService, JwtTokenProvider tokenProvider, UserDao userDao) {
        this.fileService = fileService;
        this.tokenProvider = tokenProvider;
        this.userDao = userDao;
    }

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return userDao.getUserId(tokenProvider.getUserEmail(request));
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDto> uploadFile(
                            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) MultipartFile file, 
                            @RequestParam FileDivision division, HttpServletRequest request) throws IOException {
        return fileService.saveFile(file, division, GET_USER_ID_BY_TOKEN(request));
    }
}