package com.koreanguide.koreanguidebackend.domain.admin.controller;

import com.koreanguide.koreanguidebackend.domain.admin.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"Admin API"})
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @ApiOperation(value = "모든 사용자 조회")
    @GetMapping
    public ResponseEntity<?> getAllUser() {
        return adminService.getAllUser();
    }

    @ApiOperation(value = "모든 트랙 조회")
    @GetMapping("/track")
    public ResponseEntity<?> getAllTrack() {
        return adminService.getAllTrack();
    }
}
