package jp.co.willwave.aca.dto.api;

import jp.co.willwave.aca.constants.CarStatus;
import jp.co.willwave.aca.utilities.WebUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class DeviceInfoDTO {
    private CarStatus carStatus;
    private String callUserName;
    private String callPassword;
    private Long callApplicationId;
    private String callAuthKey;
    private String callAuthSecret;
    private String callAccountKey;
    private String callTags;
    private String iconPath;

    public void setIconPath(String serverBaseUrl, String iconPath) {
        if (!StringUtils.isEmpty(iconPath)) {
            this.iconPath = WebUtil.combineUrl(serverBaseUrl, iconPath);
        }
    }
}
