package jp.co.willwave.aca.web.restController;

import jp.co.willwave.aca.dto.api.ResponseDTO;
import jp.co.willwave.aca.service.ExpandUserDetails;
import jp.co.willwave.aca.service.MasterSysConfigsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configs")
public class ConfigRestController {
    private Logger logger = Logger.getLogger(ConfigRestController.class);

    private final MasterSysConfigsService masterSysConfigsService;

    @Autowired
    public ConfigRestController(MasterSysConfigsService masterSysConfigsService) {
        this.masterSysConfigsService = masterSysConfigsService;
    }

    @GetMapping("/mobile")
    public ResponseDTO getConfigsApp() {
        logger.info("ConfigRestController.getConfigsApp");
        try {
            Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getDevicesEntity().getId();
            return new ResponseDTO<>().success(masterSysConfigsService.getConfigsMobile(devicesId));
        } catch (Exception e) {
            logger.error("ConfigRestController.getConfigsApp error", e);
            return new ResponseDTO<>().error();
        }
    }
}
