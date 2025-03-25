package kr.yuns.seoul.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BicycleResponseDto {
    private String code;
    private String count;
    private String name;
    private String address;
    private String kakaoMapUrl;
}