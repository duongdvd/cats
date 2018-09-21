package jp.co.willwave.aca.web.restController;

import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.dto.api.DevicesDTO;
import jp.co.willwave.aca.dto.api.ResponseDTO;
import jp.co.willwave.aca.service.DevicesService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RELIPA
 */
@RestController
@RequestMapping("/api")
public class LoginRestController {
    /**
     * ロガーの定義
     */
    private static Logger logger = Logger.getLogger(LoginRestController.class);

    /**
     * ロジッククラスの定義
     */
    private final DevicesService devicesService;

    public LoginRestController(DevicesService devicesService) {
        this.devicesService = devicesService;
    }

    @PostMapping("/login")
    public ResponseDTO<DevicesDTO> login(@RequestBody DevicesDTO devicesDTO) {
        try {
            logger.info("LoginRestController.login");
            return new ResponseDTO<>().success(devicesService.login(devicesDTO));
        } catch (LogicException e) {
            logger.error("UserController.login logic error ", e);
            return new ResponseDTO<>().error().errorCode(e.getErrorCode()).message(e.getMessage());
        } catch (Exception e) {
            logger.error("UserController.login error ", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseDTO<String> logout() {
        try {
            logger.info("LoginRestController.logout");
            devicesService.logout();
            return new ResponseDTO<>().success();
        } catch (Exception e) {
            logger.error("UserController.logout error ", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }
}
