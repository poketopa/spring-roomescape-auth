package roomescape.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.User;

@Schema(description = "회원가입 응답")
public record RegisterResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long id,

        @Schema(description = "이름", example = "홍길동")
        String name,

        @Schema(description = "이메일", example = "hong@test.com")
        String email
) {
    public static RegisterResponse from(User user) {
        return new RegisterResponse(user.getId(), user.getName(), user.getEmail());
    }
}
