package jp.co.willwave.aca.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureBadCredentialsEventListener
        implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private static Logger logger = Logger.getLogger(AuthenticationFailureBadCredentialsEventListener.class);

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        logger.error("login failure : " + event.getAuthentication().getPrincipal());
    }
}
