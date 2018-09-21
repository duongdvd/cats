package jp.co.willwave.aca.web.form;

import jp.co.willwave.aca.model.enums.CustomerType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class CustomerImportForm {
    private String id;
    private String name;
    private String buildingName;
    private String address;
    private String description;
    private String longitude;
    private String latitude;
    private String status;
    private String divisionsId;
    private String divisionName;
    private String iconMarker;
    private CustomerType customerType;
}
