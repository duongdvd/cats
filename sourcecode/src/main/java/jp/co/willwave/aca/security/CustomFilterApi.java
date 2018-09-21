package jp.co.willwave.aca.security;

import jp.co.willwave.aca.constants.PathConstants;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.AccessEntity;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.model.enums.HttpMethod;
import jp.co.willwave.aca.service.AccessService;
import jp.co.willwave.aca.service.DevicesService;
import jp.co.willwave.aca.service.ExpandUserDetails;
import jp.co.willwave.aca.utilities.ApplicationContextUtil;
import jp.co.willwave.aca.utilities.LoginUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebFilter(filterName = "validate-token", value = "/*")
public class CustomFilterApi extends GenericFilterBean implements Filter {

    private Logger logger = Logger.getLogger(CustomFilterApi.class);

    @Autowired
    private DevicesService devicesService;
    @Autowired
    private AccessService accessService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        String path = ((HttpServletRequest) servletRequest).getServletPath();
        String method = ((HttpServletRequest) servletRequest).getMethod().toUpperCase();
        // ignore resource, favicon
        if (path.matches(PathConstants.REGEX_RESOURCES) || path.equalsIgnoreCase(PathConstants.FAVICON)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        logger.info("PATH ----- " + path + " | " + method);
        // Api
        if (path.matches(PathConstants.REGEX_API)) {
            if ((path.equals(PathConstants.API_LOGIN) && "POST".equals(method)) || path.equals(PathConstants.API_ABOUT)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                String token = ((HttpServletRequest) servletRequest).getHeader("token");
                if (StringUtils.isEmpty(token)) {
                    logger.debug("CustomFilterApi require login");
                    ((HttpServletResponse) servletResponse).setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }

                DevicesEntity devicesEntity = devicesService.findByToken(token);
                if (devicesEntity != null && !StringUtils.isEmpty(devicesEntity.getLoginToken())) {
                    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(grantedAuthority);
                    ExpandUserDetails expandUserDetails = new ExpandUserDetails(devicesEntity.getLoginId(), "", true,
                            false, false, false, authorities);
                    expandUserDetails.setDevicesEntity(devicesEntity);
                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(expandUserDetails, token, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }

                logger.debug("CustomFilterApi require login");
                ((HttpServletResponse) servletResponse).setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        }
        // Server
        else {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            HttpSession session = request.getSession();
            String userLoginUri = request.getContextPath() + PathConstants.LOGIN_VIEW;

            boolean hasAccess = this.hasAccess(session, path, HttpMethod.getByName(method));
            if (hasAccess) {
                filterChain.doFilter(request, response);
            } else if (LoginUtil.isLogin(session)) {
                ((HttpServletResponse) servletResponse).setStatus(HttpStatus.FORBIDDEN.value()); //TODO message
            } else {
                response.sendRedirect(userLoginUri);
            }
        }
    }

    private boolean hasAccess(HttpSession session, String path, HttpMethod method) {
        return accessService.hasAccess(ApplicationContextUtil.getAccessPaths(), path, method) //check access (role NONE)
            ||
            accessService.hasAccess((List<AccessEntity>) session.getAttribute(Constant.Session.ACCESS_PATHS), path, method);
    }
}
