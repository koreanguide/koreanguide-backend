package com.koreanguide.koreanguidebackend.domain.profile.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MainProfileAlertResponseDto {
    private boolean profileComplete;
    private boolean couponUsed;
    private int level;
    private boolean firstLevel;
    private boolean secondLevel;
    private boolean thirdLevel;
    private boolean fourthLevel;
    private boolean fifthLevel;
}
