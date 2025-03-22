package kr.yuns.file.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponseDto {
    private String uuid;
    private String url;
}
