package io.github.pavelshe11.networkingmicro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.annotation.PostConstruct;
import java.util.Locale;

@Configuration
public class LocalisationConfig {

    @PostConstruct
    public void init() {
        Locale.setDefault(new Locale("ru"));
        LocaleContextHolder.setDefaultLocale(new Locale("ru"));
    }

    @Bean
    LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale("ru"));
        return resolver;
    }
}
