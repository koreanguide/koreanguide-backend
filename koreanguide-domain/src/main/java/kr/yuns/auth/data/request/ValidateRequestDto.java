package kr.yuns.auth.data.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ValidateRequestDto {
    private String email;
    private String key;
}