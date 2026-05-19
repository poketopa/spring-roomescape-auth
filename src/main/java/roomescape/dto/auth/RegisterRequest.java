package roomescape.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.exception.InvalidInputException;

@Schema(description = "회원가입 요청")
public record RegisterRequest(
        @Schema(description = "이름", example = "홍길동")
        String name,

        @Schema(description = "이메일", example = "hong@test.com")
        String email,

        @Schema(description = "비밀번호", example = "password1234")
        String password
) {
    public RegisterRequest {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("이름은 필수입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new InvalidInputException("이메일은 필수입니다.");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidInputException("비밀번호는 필수입니다.");
        }
    }
}
