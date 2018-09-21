package jp.co.willwave.aca.web.form;

import lombok.Data;

@Data
public class RouteCustomerDetailForm {

    private String customerName;
    private String customerAddress;
    private String customerBuildingName;
    private Long visitOrder;
}
