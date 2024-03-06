package com.koreanguide.koreanguidebackend.domain.assistant.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssistantRequestDto {
    private String msg;
}
