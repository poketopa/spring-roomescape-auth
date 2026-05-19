package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.dto.error.ErrorCode;
import roomescape.exception.AuthenticationException;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    public static final String AUTHENTICATED_MEMBER_ATTRIBUTE = "authenticatedMember";

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        if (!requiresAuthentication(handlerMethod)) {
            return true;
        }

        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = jwtTokenProvider.resolveToken(authorizationHeader);

        if (accessToken == null) {
            throw new AuthenticationException(ErrorCode.AUTHENTICATION_REQUIRED, "인증이 필요합니다.");
        }
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN, "유효하지 않은 토큰입니다.");
        }

        AuthenticatedMember authenticatedMember = new AuthenticatedMember(
                jwtTokenProvider.extractUserId(accessToken),
                jwtTokenProvider.extractEmail(accessToken)
        );
        request.setAttribute(AUTHENTICATED_MEMBER_ATTRIBUTE, authenticatedMember);
        return true;
    }

    private boolean requiresAuthentication(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(LoginRequired.class)
                || handlerMethod.getBeanType().isAnnotationPresent(LoginRequired.class);
    }
}
