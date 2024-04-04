package com.koreanguide.koreanguidebackend.domain.seoul.data.entity;

import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.DustInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DustData {
    private DustInfo fineDust;
    private DustInfo ultraFineDust;
}
