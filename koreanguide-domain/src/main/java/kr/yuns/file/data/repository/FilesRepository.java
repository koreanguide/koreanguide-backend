package kr.yuns.file.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.file.data.entity.Files;

public interface FilesRepository extends JpaRepository<Files, Long> {
    
}
