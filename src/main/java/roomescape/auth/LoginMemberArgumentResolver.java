package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.exception.AuthenticationException;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginMemberAnnotation = parameter.hasParameterAnnotation(LoginMember.class);
        boolean isAuthenticatedMemberType = AuthenticatedMember.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginMemberAnnotation && isAuthenticatedMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new AuthenticationException("인증이 필요합니다.");
        }

        Object authenticatedMember = request.getAttribute(JwtAuthInterceptor.AUTHENTICATED_MEMBER_ATTRIBUTE);
        if (!(authenticatedMember instanceof AuthenticatedMember)) {
            throw new AuthenticationException("인증이 필요합니다.");
        }
        return authenticatedMember;
    }
}
