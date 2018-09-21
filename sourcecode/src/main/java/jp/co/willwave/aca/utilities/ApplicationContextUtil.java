package jp.co.willwave.aca.utilities;

import jp.co.willwave.aca.model.entity.AccessEntity;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext ctx;
    private static String workspaceId;
    /**
     * access paths list {@link jp.co.willwave.aca.model.enums.UserRole NONE}
     */
    private static List<AccessEntity> accessPaths;

    @Override
    public void setApplicationContext(ApplicationContext appContext)
            throws BeansException {
        ctx = appContext;

    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    /**
     * @return the workspaceId
     */
    public static String getWorkspaceId() {
        return workspaceId;
    }

    /**
     * @param workspaceId the workspaceId to set
     */
    public void setWorkspaceId(String workspaceId) {
        ApplicationContextUtil.workspaceId = workspaceId;
    }

    /**
     * get access paths list {@link jp.co.willwave.aca.model.enums.UserRole NONE}
     */
    public static List<AccessEntity> getAccessPaths() {
        return accessPaths;
    }

    /**
     * set access paths list {@link jp.co.willwave.aca.model.enums.UserRole NONE}
     */
    public static void setAccessPaths(List<AccessEntity> accessPaths) {
        ApplicationContextUtil.accessPaths = accessPaths;
    }
}