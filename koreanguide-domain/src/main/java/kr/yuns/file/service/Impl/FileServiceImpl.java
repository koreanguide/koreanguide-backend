package kr.yuns.file.service.Impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
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

    public String uploadFiles(MultipartFile file) throws AmazonServiceException, SdkClientException, IOException {
        String originFileName = file.getOriginalFilename();

        if(originFileName == null) {
            throw new IllegalArgumentException("File name is empty");
        }

        String extension = originFileName.substring(originFileName.lastIndexOf("."));
        String uuidFilename = UUID.randomUUID() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3Client.putObject(bucket, uuidFilename, file.getInputStream(), metadata);

        return uuidFilename;
    }

    void saveFileLog(String uuid, FileDivision division, Long userId) {
        User user = userDao.getUserEntity(userId);

        filesRepository.save(Files.builder()
                .uploadedUser(user)
                .uuid(uuid)
                .division(division)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public String getUrl(String uuid) {
        return amazonS3Client.getUrl(bucket, uuid).toString();
    }

    @Override
    public ResponseEntity<FileResponseDto> saveFile(MultipartFile file, FileDivision division, Long userId) throws IOException {
        String uuid = uploadFiles(file);
        saveFileLog(uuid, division, userId);

        return ResponseEntity.status(HttpStatus.OK).body(
            FileResponseDto.builder()
                .uuid(uuid)
                .url(getUrl(uuid))
            .build());
    }
}