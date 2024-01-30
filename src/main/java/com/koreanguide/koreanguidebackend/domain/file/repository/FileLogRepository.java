package com.koreanguide.koreanguidebackend.domain.file.repository;

import com.koreanguide.koreanguidebackend.domain.file.entity.FileLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileLogRepository extends JpaRepository<FileLog, Long> {
}
