package jp.co.willwave.aca.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureLockedEventListener implements ApplicationListener<AuthenticationFailureLockedEvent> {
    private static Logger logger = Logger.getLogger(AuthenticationFailureLockedEventListener.class);

    @Override
    public void onApplicationEvent(AuthenticationFailureLockedEvent event) {
        logger.error("login failure locked : " + event.getAuthentication().getPrincipal());
    }
}
