package kr.yuns.seoul.data.entity;

import kr.yuns.seoul.data.enums.DustInfo;
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