package jp.co.willwave.aca.dto.api;

import lombok.Data;

@Data
public class RouteCustomerDetailDTO {

    private String customerName;
    private String customerAddress;
    private String customerBuildingName;
    private Long visitOrder;
}
