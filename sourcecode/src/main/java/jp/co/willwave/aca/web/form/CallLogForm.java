package jp.co.willwave.aca.web.form;

import jp.co.willwave.aca.model.enums.CallType;
import lombok.Data;

import java.util.Date;

@Data
public class CallLogForm {
    private Long fromCallId;
    private String fromCallName;
    private String fromCallPassword;
    private Long toCallId;
    private String toCallName;
    private String userTags;

    private Boolean isFromDevice;
    private CallType type;
    private String latitude;
    private String longitude;
    private Long routeDetailId;
    private Date startTime;
    private Date endTime;
    private Long deviceId;
    private Long operatorId;
}
