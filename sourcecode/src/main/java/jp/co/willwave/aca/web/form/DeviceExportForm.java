package jp.co.willwave.aca.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class DeviceExportForm {
    @NotEmpty(message = "E0001,loginId")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "E0015, loginId")
    private String loginId;

    @NotNull(message = "E0001,status")
    private Integer status;

    private String iconPath;

    @SafeHtml(message = "E0015, carMaker")
    private String carMaker;

    @SafeHtml(message = "E0015, carType")
    private String carType;

    @NotEmpty(message = "E0001,driverName")
    @SafeHtml(message = "E0015, driverName")
    private String driverName;

    @SafeHtml(message = "E0015, plateNumber")
    private String plateNumber;
}
