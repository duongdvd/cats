package jp.co.willwave.aca.web.form;

import jp.co.willwave.aca.model.enums.CustomerType;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;


@Data
public class CustomerForm {
    private Long id;

    @NotEmpty(message = "E0001,customer.name")
    private String name;

    private String buildingName;

    @NotEmpty(message = "E0001,customer.address")
    private String address;

    private String description;

    @NotEmpty(message = "E0001,customer.longitude")
    private String longitude;

    @NotEmpty(message = "E0001,customer.latitude")
    private String latitude;

    @NotNull(message = "E0001,customer.division")
    private Long divisionsId;

    private String divisionName;

    private String iconMarker;

    private MultipartFile iconMarkerFile;

    private Boolean status = Boolean.TRUE;

    private Boolean editPermission;

    private Boolean deleteAndChangeStatusPermission;

    private CustomerType customerType;
}
