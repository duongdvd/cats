package jp.co.willwave.aca.config;

import jp.co.willwave.aca.model.constant.Constant;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
@Component
public class MessageConfig {
    private static final int CACHE_SECONDS = 5;
    private static final String MESSAGE_SOURCE = "classpath:message";

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename(MESSAGE_SOURCE);
        messageSource.setCacheSeconds(CACHE_SECONDS);
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding(Constant.UTF_8);
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.JAPAN); // change this
        return localeResolver;
    }

}
