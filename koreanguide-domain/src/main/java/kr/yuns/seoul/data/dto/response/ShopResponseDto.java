package kr.yuns.seoul.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShopResponseDto {
    private String cate2Name;
    private String cate3Name;
    private String nameKor;
    private String address;
}