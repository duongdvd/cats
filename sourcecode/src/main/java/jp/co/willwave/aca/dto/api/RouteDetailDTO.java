package jp.co.willwave.aca.dto.api;

import jp.co.willwave.aca.model.entity.RouteDetailEntity;
import jp.co.willwave.aca.model.enums.CustomerType;
import jp.co.willwave.aca.utilities.WebUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RouteDetailDTO {
    private Long id;
    private Long routesId;
    private Long visitOrder;
    private String iconMarker;
    private Date arrivalTime;
    private String arrivalNote;
    private Date reDepartTime;
    private Long customersId;
    private String name;
    private String address;
    private String buildingName;
    private String longtitude;
    private String latitude;
    private String description;
    private Boolean finished = Boolean.FALSE;
    private CustomerType customerType;

    public void setIconMarker(String serverBaseUrl, String iconMarker) {
        if (!StringUtils.isEmpty(iconMarker)) {
            this.iconMarker = WebUtil.combineUrl(serverBaseUrl, iconMarker);
        }
    }

    public static RouteDetailDTO fromEntity(RouteDetailEntity entity) {
        RouteDetailDTO dto = new RouteDetailDTO();
        dto.setRoutesId(entity.getRoutesId());
        dto.setVisitOrder(entity.getVisitOrder());
        dto.setArrivalTime(entity.getArrivalTime());
        dto.setArrivalNote(entity.getArrivalNote());
        dto.setReDepartTime(entity.getReDepartTime());
        dto.setCustomersId(entity.getCustomers().getId());
        dto.setName(entity.getCustomers().getName());
        dto.setAddress(entity.getCustomers().getAddress());
        dto.setBuildingName(entity.getCustomers().getBuildingName());
        dto.setLongtitude(entity.getCustomers().getLongitude());
        dto.setLatitude(entity.getCustomers().getLatitude());
        dto.setDescription(entity.getCustomers().getDescription());
        dto.setCustomerType(entity.getCustomers().getCustomerType());

        return dto;
    }

    public static List<RouteDetailDTO> fromEntity(List<RouteDetailEntity> entities) {
        return entities.stream().map(entity -> RouteDetailDTO.fromEntity(entity)).collect(Collectors.toList());
    }
}
