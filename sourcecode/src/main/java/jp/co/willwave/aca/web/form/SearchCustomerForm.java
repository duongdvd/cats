package jp.co.willwave.aca.web.form;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchCustomerForm {
    private Long loginUserId;
    private Long id;
    private String name;
    private String address;
    private String buildingName;
    private String description;
    private String longitude;
    private String latitude;
    private Boolean status;
    private Long divisionsId;
    private List<Long> divisionsIds = new ArrayList<>();
    private List<Long> divisionIdsManagedByLoginUser = new ArrayList<>();
    private Boolean isCustomer;
}
