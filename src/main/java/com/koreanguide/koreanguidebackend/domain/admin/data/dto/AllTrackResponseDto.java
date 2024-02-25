package com.koreanguide.koreanguidebackend.domain.admin.data.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AllTrackResponseDto {
    private Long id;
    private Long user;
    private String title;
    private String preview;
    private String content;
    private String tag;
    private String primaryImage;
    private String addedImage;
    private boolean autoTranslate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
