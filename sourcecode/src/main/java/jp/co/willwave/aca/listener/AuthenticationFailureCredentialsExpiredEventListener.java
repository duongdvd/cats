package jp.co.willwave.aca.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureCredentialsExpiredEventListener
        implements ApplicationListener<AuthenticationFailureCredentialsExpiredEvent> {
    private static Logger logger = Logger.getLogger(AuthenticationFailureCredentialsExpiredEventListener.class);

    @Override
    public void onApplicationEvent(AuthenticationFailureCredentialsExpiredEvent event) {
        logger.error("login credential expired : " + event.getAuthentication().getPrincipal());
    }
}
