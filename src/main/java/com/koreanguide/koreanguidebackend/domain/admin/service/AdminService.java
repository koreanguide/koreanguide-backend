package com.koreanguide.koreanguidebackend.domain.admin.service;

import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<?> getAllUser();

    ResponseEntity<?> getAllTrack();
}
