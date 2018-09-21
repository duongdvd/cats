package jp.co.willwave.aca.dto.api;

import jp.co.willwave.aca.constants.RunningStatus;
import lombok.Data;

import java.util.Date;

@Data
public class RouteDTO {

    private Long routeId;
    private String routeName;
    private String deviceName;
    private String plateNumber;
    private Date startDate;
    private Date endDate;
    private Integer status;
    private String routeMemo;
    private RunningStatus runningStatus;

    private boolean deletePermission = Boolean.TRUE;
    private boolean changeStatusPermission = Boolean.TRUE;
}
