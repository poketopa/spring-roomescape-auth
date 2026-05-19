package roomescape.service;

import roomescape.auth.JwtTokenProvider;
import roomescape.domain.User;
import roomescape.dto.auth.LoginRequest;
import roomescape.dto.auth.LoginResponse;
import roomescape.dto.error.ErrorCode;
import roomescape.exception.AuthenticationException;
import roomescape.repository.UserDao;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserDao userDao;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserDao userDao, JwtTokenProvider jwtTokenProvider) {
        this.userDao = userDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userDao.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationException(ErrorCode.LOGIN_FAILED, "로그인에 실패했습니다."));

        if (!user.getPassword().equals(request.password())) {
            throw new AuthenticationException(ErrorCode.LOGIN_FAILED, "로그인에 실패했습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        return new LoginResponse(accessToken);
    }
}
