package kr.yuns.credit.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import kr.yuns.credit.data.enums.ReturningStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditReturningRequestResponseDto {
    private Long amount;
    private ReturningStatus returningStatus;
    private LocalDateTime requestDate;
    private LocalDateTime updateDate;
}