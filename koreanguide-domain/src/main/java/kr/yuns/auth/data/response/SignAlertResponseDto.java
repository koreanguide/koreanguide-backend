package kr.yuns.auth.data.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Builder
public class SignAlertResponseDto {
    private String en;
    private String ko;
}