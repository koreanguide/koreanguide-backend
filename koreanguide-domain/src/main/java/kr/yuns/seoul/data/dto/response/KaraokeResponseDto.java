package kr.yuns.seoul.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KaraokeResponseDto {
    private String phoneNum;
    private String name;
    private String address;
}