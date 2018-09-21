package jp.co.willwave.aca.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private static Logger logger = Logger.getLogger(AuthenticationSuccessEventListener.class);

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        logger.error("login succeeded : " + event.getAuthentication().getPrincipal());
    }
}
