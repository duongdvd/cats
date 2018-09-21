package jp.co.willwave.aca.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:quickblox.properties")
public class QuickBlox {

    @Value("${quickblox.session.get}")
    private String sessionGetUrl;
    @Value("${quickblox.user.register}")
    private String userRegisterUrl;
    @Value("${quickblox.app.id}")
    private String appId;
    @Value("${quickblox.auth.key}")
    private String authKey;
    @Value("${quickblox.auth.secret}")
    private String authSecret;
    @Value("${quickblox.header.token}")
    private String headerToken;
    @Value("${quickblox.admin.id}")
    private String adminId;
    @Value("${quickblox.account.key}")
    private String accountKey;

    public String getSessionGetUrl() {
        return sessionGetUrl;
    }

    public void setSessionGetUrl(String sessionGetUrl) {
        this.sessionGetUrl = sessionGetUrl;
    }

    public String getUserRegisterUrl() {
        return userRegisterUrl;
    }

    public void setUserRegisterUrl(String userRegisterUrl) {
        this.userRegisterUrl = userRegisterUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAuthSecret() {
        return authSecret;
    }

    public void setAuthSecret(String authSecret) {
        this.authSecret = authSecret;
    }

    public String getHeaderToken() {
        return headerToken;
    }

    public void setHeaderToken(String headerToken) {
        this.headerToken = headerToken;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }
}
