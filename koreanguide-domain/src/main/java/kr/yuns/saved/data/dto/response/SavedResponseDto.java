package kr.yuns.saved.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedResponseDto {
    private Long id;
    private String category;
    private String value;
    private String address;
}