package io.github.fantasmadux.usermicro.config;

import io.github.fantasmadux.usermicro.component.ActivityTrackingInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final ActivityTrackingInterceptor activityTrackingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(activityTrackingInterceptor)
                .addPathPatterns("/user/v1/**");
    }
}
