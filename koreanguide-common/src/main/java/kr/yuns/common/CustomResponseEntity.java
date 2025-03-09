package kr.yuns.common;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class CustomResponseEntity<T> {
    private int code;
    private String message;

    public static <T> CustomResponseEntity<T> success() {
        return CustomResponseEntity.<T>builder()
                .code(Result.OK.getCode())
                .message(Result.OK.getMessage())
                .build();
    }

    public static <T> CustomResponseEntity<T> fail() {
        return CustomResponseEntity.<T>builder()
                .code(Result.FAIL.getCode())
                .message(Result.FAIL.getMessage())
                .build();
    }

    public static <T> CustomResponseEntity<T> fail(Result result) {
        return CustomResponseEntity.<T>builder()
                .code(result.getCode())
                .message(result.getMessage())
                .build();
    }

    public CustomResponseEntity(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
