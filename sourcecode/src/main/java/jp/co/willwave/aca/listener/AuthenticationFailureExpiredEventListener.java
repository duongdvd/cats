package jp.co.willwave.aca.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureExpiredEventListener
        implements ApplicationListener<AuthenticationFailureExpiredEvent> {
    private static Logger logger = Logger.getLogger(AuthenticationFailureExpiredEventListener.class);

    @Override
    public void onApplicationEvent(AuthenticationFailureExpiredEvent event) {
        logger.error("login failure expired : " + event.getAuthentication().getPrincipal());
    }
}
