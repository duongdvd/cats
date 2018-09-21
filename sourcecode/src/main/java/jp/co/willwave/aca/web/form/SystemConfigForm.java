package jp.co.willwave.aca.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SystemConfigForm {

//    @NotNull
    private Long divisionId;
    private String divisionName;
    private String parentDivisionName;
//    @NotEmpty(message = "E0001,notificationTime")
//    @Pattern(regexp = "^[0-9]+$", message = "E0015, notificationTime")
    private String notificationTime;

//    @NotEmpty(message = "E0001,notificationTime")
    @Email(message = "E0015, systemConfig.notificationEmail")
    private String notificationEmail;

    private MultipartFile mobileIconFile;
    private MultipartFile customerIconFile;

    private String mobileIcon;
    private String customerIcon;

//    @Pattern(regexp = "^[0-9]+$", message = "E0015, timeMessage")
    private String timeMessage;
//    @Pattern(regexp = "^[0-9]+$", message = "E0015, travelTimeAlert")
    private String travelTimeAlert;
//    @Pattern(regexp = "^[0-9]+$", message = "E0015, distanceFinished")
    private String distanceFinished;

    private String startEndPointColor;
    private String arrivedPoint;
    private String notArrivedPoint;

    private boolean setNotificationTime = true;
    private boolean setNotificationEmail = true;
    private boolean setMobileIconFile = true;
    private boolean setCustomerIconFile = true;
    private boolean setTimeMessage = true;
    private boolean setTravelTimeAlert = true;
    private boolean setDistanceFinished = true;
    private boolean setStartEndPointColor = true;
    private boolean setArrivedPointColor = true;
    private boolean setNotArrivedPointColor = true;

    private boolean canEditFlg = false;
}
