package kr.yuns.auth.data.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
}