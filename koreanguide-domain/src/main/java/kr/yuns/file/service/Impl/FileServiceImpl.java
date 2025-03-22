package kr.yuns.file.service.Impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import kr.yuns.auth.data.dao.UserDao;
import kr.yuns.auth.data.entity.User;
import kr.yuns.file.data.dto.FileResponseDto;
import kr.yuns.file.data.entity.Files;
import kr.yuns.file.data.enums.FileDivision;
import kr.yuns.file.data.repository.FilesRepository;
import kr.yuns.file.service.FileService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private final AmazonS3Client amazonS3Client;
    private final UserDao userDao;
    private final FilesRepository filesRepository;

    @Autowired
    public FileServiceImpl(AmazonS3Client amazonS3Client, UserDao userDao, FilesRepository filesRepository) {
        this.amazonS3Client = amazonS3Client;
        this.userDao = userDao;
        this.filesRepository = filesRepository;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public ResponseEntity<FileResponseDto> saveFile(MultipartFile multipartFile, FileDivision division, Long userId) throws IOException {
        User user = userDao.getUserEntity(userId);

        String originalFilename = multipartFile.getOriginalFilename();
        assert originalFilename != null;
        @SuppressWarnings("null")
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuidFilename = UUID.randomUUID() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3Client.putObject(bucket, uuidFilename, multipartFile.getInputStream(), metadata);

        String FILE_URL = amazonS3Client.getUrl(bucket, uuidFilename).toString();

        filesRepository.save(Files.builder()
                .user(user)
                .url(FILE_URL)
                .division(division)
                .createdAt(LocalDateTime.now())
                .build());

        return ResponseEntity.status(HttpStatus.OK).body(
            FileResponseDto.builder()
                .url(FILE_URL)
            .build());
    }
}