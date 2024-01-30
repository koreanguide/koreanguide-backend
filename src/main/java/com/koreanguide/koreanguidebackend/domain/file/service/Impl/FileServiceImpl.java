package com.koreanguide.koreanguidebackend.domain.file.service.Impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.file.entity.FileLog;
import com.koreanguide.koreanguidebackend.domain.file.repository.FileLogRepository;
import com.koreanguide.koreanguidebackend.domain.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    private final AmazonS3 amazonS3;
    private final UserRepository userRepository;
    private final FileLogRepository fileLogRepository;

    @Autowired
    public FileServiceImpl(AmazonS3 amazonS3, UserRepository userRepository, FileLogRepository fileLogRepository) {
        this.amazonS3 = amazonS3;
        this.userRepository = userRepository;
        this.fileLogRepository = fileLogRepository;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String saveFile(MultipartFile multipartFile, Long userId) throws IOException {
        // 사용자 확인
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new RuntimeException("미등록 사용자");
        }

        // 파일 저장
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 확장자 추출
        String uuidFilename = UUID.randomUUID().toString() + extension; // UUID 생성 및 확장자 붙이기

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, uuidFilename, multipartFile.getInputStream(), metadata);

        String FILE_URL = amazonS3.getUrl(bucket, uuidFilename).toString();

        // 로그 저장
        fileLogRepository.save(FileLog.builder()
                .user(user.get())
                .url(FILE_URL)
                .createdAt(LocalDateTime.now())
                .build());

        return FILE_URL;
    }
}
