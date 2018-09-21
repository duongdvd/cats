package jp.co.willwave.aca.dto.api;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class RoutesDTO {
    private Long id;
    private String name;
    private String description;
    private Long devicesId;
    private Long planedRoutesId;
    private Date startDate;
    private Date endDate;
    private Date actualDate;
    private Date updateDate;
    private Long callUserId;
    private Boolean finished = Boolean.TRUE;
    private List<RouteDetailDTO> routeDetails = new ArrayList<>();
}
