package com.koreanguide.koreanguidebackend.domain.gpt.data;

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
