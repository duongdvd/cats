package jp.co.willwave.aca.service;

import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.model.entity.UsersEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Data
@EqualsAndHashCode(
        callSuper = false)
public class ExpandUserDetails extends User {
    private static final long serialVersionUID = 1L;
    private String domain;
    private boolean isAdmin;
    private boolean isInit;
    private boolean isSetup;
    private boolean isCanUseSSL;
    private boolean isDNS;
    private UsersEntity usersEntity;
    private DevicesEntity devicesEntity;

    /**
     * コンストラクタ
     *
     * @param username
     * @param password
     * @param enabled
     * @param accountNonExpired
     * @param credentialsNonExpired
     * @param accountNonLocked
     * @param authorities
     */
    public ExpandUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
                             boolean credentialsNonExpired, boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }
}