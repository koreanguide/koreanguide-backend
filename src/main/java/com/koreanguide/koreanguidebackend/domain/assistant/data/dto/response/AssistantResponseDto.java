package com.koreanguide.koreanguidebackend.domain.assistant.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssistantResponseDto {
    private String msg;
}
