package jp.co.willwave.aca.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureServiceExceptionEventListener
        implements ApplicationListener<AuthenticationFailureServiceExceptionEvent> {
    private static Logger logger = Logger.getLogger(AuthenticationFailureServiceExceptionEventListener.class);

    @Override
    public void onApplicationEvent(AuthenticationFailureServiceExceptionEvent event) {
        logger.error("login failure exception : " + event.getAuthentication().getPrincipal());
    }
}
