package com.koreanguide.koreanguidebackend.domain.seoul.data.dto;

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
