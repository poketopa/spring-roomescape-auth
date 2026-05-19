package roomescape.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.exception.InvalidInputException;

public record LoginRequest(
        @Schema(description = "이메일", example = "user@test.com")
        String email,
        @Schema(description = "비밀번호", example = "password1234")
        String password
) {
    public LoginRequest {
        if (email == null || email.isBlank()) {
            throw new InvalidInputException("이메일은 필수입니다.");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidInputException("비밀번호는 필수입니다.");
        }
    }
}
