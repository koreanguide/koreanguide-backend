package kr.yuns.auth.data.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TokenRequestDto {
    private String refreshToken;
}