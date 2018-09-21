package jp.co.willwave.aca.web.restController;

import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.dto.api.LocationTempDTO;
import jp.co.willwave.aca.dto.api.ResponseDTO;
import jp.co.willwave.aca.service.LocationLogsService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/location")
public class LocationRestController {

    /**
     * ロガーの定義
     */
    private static Logger logger = Logger.getLogger(LocationRestController.class);

    /**
     * ロジッククラスの定義
     */

    private final LocationLogsService locationLogsService;

    public LocationRestController(LocationLogsService locationLogsService) {
        this.locationLogsService = locationLogsService;
    }

    @PostMapping("/send")
    public ResponseDTO<Boolean> sendLocation(@RequestBody LocationTempDTO locationTemp) {
        try {
            logger.info("LocationRestController.sendLocation");
            locationLogsService.sendLocation(locationTemp);
            return new ResponseDTO<>().success();
        } catch (LogicException e) {
            logger.error("LocationRestController.sendLocation logic error", e);
            return new ResponseDTO<>().error().errorCode(e.getErrorCode()).message(e.getMessage());
        } catch (Exception e) {
            logger.error("LocationRestController.sendLocation error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @PostMapping("/send/list")
    public ResponseDTO sendLocations(@RequestBody List<LocationTempDTO> locations) {
        try {
            locationLogsService.sendLocationList(locations);
        } catch (LogicException e) {
            logger.error("LocationRestController.sendLocations logic error", e);
            return new ResponseDTO<>().error().errorCode(e.getErrorCode()).message(e.getMessage());
        } catch (Exception e) {
            logger.error("LocationRestController.sendLocations error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
        return new ResponseDTO().success();
    }

}
