package jp.co.willwave.aca.web.restController;

import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.dto.api.EmergencyDTO;
import jp.co.willwave.aca.dto.api.ResponseDTO;
import jp.co.willwave.aca.service.EmergencyLogsService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmergencyRestController {
    private final Logger logger = Logger.getLogger(EmergencyRestController.class);
    private final EmergencyLogsService emergencyLogsService;

    public EmergencyRestController(EmergencyLogsService emergencyLogsService) {
        this.emergencyLogsService = emergencyLogsService;
    }

    @PostMapping("/emergency")
    public ResponseDTO sendEmergency(@RequestBody EmergencyDTO emergencyDTO) {
        try {
            emergencyLogsService.sendEmergency(emergencyDTO);
        } catch (LogicException e) {
            logger.error("Error logic exception sendEmergency ", e);
            return new ResponseDTO().errorCode(e.getErrorCode())
                    .message(e.getMessage());
        } catch (Exception e) {
            logger.error("Error exception sendEmergency ", e);
            return new ResponseDTO().error()
                    .message(e.getMessage());
        }
        return new ResponseDTO().success();
    }
}
