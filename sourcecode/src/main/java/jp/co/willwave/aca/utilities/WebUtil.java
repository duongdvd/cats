package jp.co.willwave.aca.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class WebUtil {

    @Value("${cat.server.base.url}")
    private String serverBaseUrl;

    private final HttpServletRequest request;

    @Autowired
    public WebUtil(HttpServletRequest request) {
        this.request = request;
    }

    public String getBaseUrl()  {
        String scheme = request.getScheme();
        String host = request.getHeader("Host");
        String contextPath = request.getContextPath();

        String baseUrl = scheme + "://" + host + contextPath;
        return baseUrl;
    }

    public String getServerBaseUrl() {
        return StringUtils.isEmpty(this.serverBaseUrl)? this.getBaseUrl(): this.serverBaseUrl;
    }

    public static String combineUrl(String... urls) {
        if (urls == null || urls.length == 0) return null;

        String slash = "/";
        String firstUrl = urls[0];
        String lastUrl = urls[urls.length - 1];
        boolean startWithSlash = firstUrl.startsWith(slash);
        boolean endWithSlash = lastUrl.endsWith(slash);

        for (int i = 0; i < urls.length; i++) {
            urls[i] = StringUtils.trimTrailingCharacter(StringUtils.trimLeadingCharacter(urls[i], '/'), '/');
        }
        String combineUrl = String.join(slash, urls);
        if (startWithSlash) combineUrl = slash + combineUrl;
        if (endWithSlash) combineUrl = combineUrl + slash;
        return combineUrl;
    }
}
