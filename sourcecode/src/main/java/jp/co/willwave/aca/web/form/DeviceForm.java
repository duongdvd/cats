package jp.co.willwave.aca.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class DeviceForm {

    private Long id;

    @NotEmpty(message = "E0001,devices.loginIdedit")
    @Length(max = 200, message = "E0074,devices.loginIdedit,200")
    @Pattern(regexp = "^[a-zA-Z0-9//!#//$%&'//(//)//*//+\\-//.///:;//<=//>//?@\\[\\]//^_`//{//|//}~]{1,200}$", message = "E0015, devices.loginIdedit")
    private String loginId;

    @NotNull(message = "E0001,status")
    private Integer status;

    @NotEmpty(message = "E0015, devices.carMaker")
    @SafeHtml(message = "E0015, devices.carMaker")
    private String carMaker;

    @NotEmpty(message = "E0015, devices.carType")
    @SafeHtml(message = "E0015, devices.carType")
    private String carType;

    @NotEmpty(message = "E0001,devices.driverNameEdit")
    @SafeHtml(message = "E0015, devices.driverNameEdit")
    private String driverName;

    @NotEmpty(message = "E0001, devices.plateNameEdit")
    @SafeHtml(message = "E0015, devices.plateNameEdit")
    private String plateNumber;

    private String deviceType;

    private MultipartFile iconPath;

    private String currentImage;

}
