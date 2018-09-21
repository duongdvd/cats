package jp.co.willwave.aca.web.restController;

import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.config.QuickBlox;
import jp.co.willwave.aca.dto.api.AboutDTO;
import jp.co.willwave.aca.dto.api.ChangeStatusCarDTO;
import jp.co.willwave.aca.dto.api.DeviceInfoDTO;
import jp.co.willwave.aca.dto.api.ResponseDTO;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.service.DevicesService;
import jp.co.willwave.aca.service.ExpandUserDetails;
import jp.co.willwave.aca.utilities.WebUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@PropertySource("classpath:config.properties")
public class DevicesRestController {


    @Value("${api.server.info}")
    private String apiInfo;

    private final Logger logger = Logger.getLogger(DevicesRestController.class);
    private final DevicesService devicesService;
    private final QuickBlox quickBlox;
    private final WebUtil webUtil;

    public DevicesRestController(DevicesService devicesService, QuickBlox quickBlox, WebUtil webUtil) {
        this.devicesService = devicesService;
        this.quickBlox = quickBlox;
        this.webUtil = webUtil;
    }

    @PutMapping("/devices/status")
    public ResponseDTO changeStatus(@RequestBody ChangeStatusCarDTO changeStatusCarDTO) {
        logger.info("DevicesRestController.changeStatus");
        try {
            devicesService.changeStatusCar(changeStatusCarDTO);
            return new ResponseDTO<>().success();
        } catch (LogicException e) {
            logger.info("DevicesRestController.changeStatus error logic", e);
            return new ResponseDTO<>().error()
                    .errorCode(e.getErrorCode())
                    .message(e.getMessage());
        } catch (Exception e) {
            logger.info("DevicesRestController.changeStatus error", e);
            return new ResponseDTO<>().error()
                    .message(e.getMessage());
        }
    }

    @GetMapping("device")
    public ResponseDTO getDeviceInfo() {
        logger.info("DevicesRestController.device");
        try {
            DevicesEntity devicesEntity = ((ExpandUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal()).getDevicesEntity();
            DevicesEntity currentDevices = devicesService.findById(devicesEntity.getId());
            DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO();
            deviceInfoDTO.setCarStatus(currentDevices.getCarStatus());
            deviceInfoDTO.setCallApplicationId(Long.valueOf(quickBlox.getAppId()));
            deviceInfoDTO.setCallAuthKey(quickBlox.getAuthKey());
            deviceInfoDTO.setCallAuthSecret(quickBlox.getAuthSecret());
            deviceInfoDTO.setCallAccountKey(quickBlox.getAccountKey());
            deviceInfoDTO.setCallUserName(currentDevices.getCallUserName());
            deviceInfoDTO.setCallPassword(currentDevices.getCallPassword());
            deviceInfoDTO.setCallTags(currentDevices.getUserTags());

            deviceInfoDTO.setIconPath(webUtil.getServerBaseUrl(), currentDevices.getIconPath());
            return new ResponseDTO<>().success(deviceInfoDTO);
        } catch (Exception e) {
            logger.info("DevicesRestController.changeStatus error", e);
            return new ResponseDTO<>().error()
                    .message(e.getMessage());
        }
    }

    @GetMapping("about")
    public ResponseDTO pingUrl() {
        AboutDTO aboutDTO = new AboutDTO();
        aboutDTO.setMessage(apiInfo);
        return (new ResponseDTO())
                .success()
                .body(aboutDTO);
    }
}
