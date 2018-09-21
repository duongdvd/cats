package jp.co.willwave.aca.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureProviderNotFoundEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureProviderNotFoundEventListener
        implements ApplicationListener<AuthenticationFailureProviderNotFoundEvent> {
    private static Logger logger = Logger.getLogger(AuthenticationFailureProviderNotFoundEventListener.class);

    @Override
    public void onApplicationEvent(AuthenticationFailureProviderNotFoundEvent event) {
        logger.error("login failure provider not found : " + event.getAuthentication().getPrincipal());
    }
}
