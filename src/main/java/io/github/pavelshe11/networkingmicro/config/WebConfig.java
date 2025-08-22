package io.github.pavelshe11.networkingmicro.config;

import io.github.pavelshe11.networkingmicro.component.ActivityTrackingInterceptor;
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
                .addPathPatterns("/networking/v1/**");
    }
}
