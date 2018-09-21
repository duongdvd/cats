package jp.co.willwave.aca.service;

import jp.co.willwave.aca.model.entity.UsersEntity;
import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ExpandUserDetailsService extends JdbcDaoImpl {
    /**
     * ロガーの定義
     */
    private static Logger logger = Logger.getLogger(ExpandUserDetailsService.class);

    @Override
    protected void checkDaoConfig() {
        DataSource dataSource = getDataSource();
        if (dataSource != null) {
            this.createJdbcTemplate(dataSource);
        }
    }

    @Override
    protected List<UserDetails> loadUsersByUsername(String username) {
        List<UserDetails> ret = new ArrayList<UserDetails>();
        try {
            Connection conn = getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(getUsersByUsernameQuery());
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                String user = result.getString("username");
                String pass = result.getString("password");
//                boolean enabled = result.getShort("enabled") == 1;
                ExpandUserDetails details = new ExpandUserDetails(user, pass, true, true, true, true,
                        AuthorityUtils.NO_AUTHORITIES);
                details.setUsersEntity(new UsersEntity());
                ret.add(details);
            }
            result.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            logger.error(e.getSQLState());
        }
        return ret;
    }

    @Override
    protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery,
                                            List<GrantedAuthority> combinedAuthorities) {
        UserDetails user = super.createUserDetails(username, userFromUserQuery, combinedAuthorities);

        if (userFromUserQuery instanceof ExpandUserDetails) {
            ExpandUserDetails expUser = (ExpandUserDetails) userFromUserQuery;
            ExpandUserDetails newUser = new ExpandUserDetails(user.getUsername(), user.getPassword(), user.isEnabled(),
                    user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(),
                    user.getAuthorities());
            newUser.setUsersEntity(expUser.getUsersEntity());
            return newUser;
        } else {
            return user;
        }
    }

}
