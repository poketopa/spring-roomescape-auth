package roomescape.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.JwtAuthInterceptor;
import roomescape.auth.LoginMemberArgumentResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;

    public WebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor,
                        LoginMemberArgumentResolver loginMemberArgumentResolver) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }
}
