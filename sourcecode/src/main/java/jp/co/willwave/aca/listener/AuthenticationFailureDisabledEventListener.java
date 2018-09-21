package jp.co.willwave.aca.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureDisabledEventListener
        implements ApplicationListener<AuthenticationFailureDisabledEvent> {
    private static Logger logger = Logger.getLogger(AuthenticationFailureDisabledEventListener.class);

    @Override
    public void onApplicationEvent(AuthenticationFailureDisabledEvent event) {
        logger.error("login failure disabled : " + event.getAuthentication().getPrincipal());
    }
}
