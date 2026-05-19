package roomescape.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginResponse(
        @Schema(description = "Access Token")
        String accessToken
) {
}
