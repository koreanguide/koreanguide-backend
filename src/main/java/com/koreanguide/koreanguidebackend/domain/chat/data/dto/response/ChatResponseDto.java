package com.koreanguide.koreanguidebackend.domain.chat.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponseDto {
    private String profileUrl;
    private String name;
    private String msg;
    private LocalDateTime date;
}
