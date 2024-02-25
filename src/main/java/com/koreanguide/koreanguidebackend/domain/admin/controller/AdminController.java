package com.koreanguide.koreanguidebackend.domain.admin.controller;

import com.koreanguide.koreanguidebackend.domain.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<?> getAllUser() {
        return adminService.getAllUser();
    }

    @GetMapping("/track")
    public ResponseEntity<?> getAllTrack() {
        return adminService.getAllTrack();
    }
}
