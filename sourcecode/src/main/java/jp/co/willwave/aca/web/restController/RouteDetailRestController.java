package jp.co.willwave.aca.web.restController;

import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.dto.api.FinishedRouteDetailDTO;
import jp.co.willwave.aca.dto.api.ResponseDTO;
import jp.co.willwave.aca.service.ExpandUserDetails;
import jp.co.willwave.aca.service.MasterArrivalNotesService;
import jp.co.willwave.aca.service.RouteDetailService;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/route-detail")
public class RouteDetailRestController {

    private final Logger logger = Logger.getLogger(this.getClass());

    private final RouteDetailService routeDetailService;
    private final MasterArrivalNotesService masterArrivalNotesService;

    public RouteDetailRestController(RouteDetailService routeDetailService,
                                     MasterArrivalNotesService masterArrivalNotesService) {
        this.routeDetailService = routeDetailService;
        this.masterArrivalNotesService = masterArrivalNotesService;
    }

    @PostMapping("/finished")
    public ResponseDTO finished(@RequestBody FinishedRouteDetailDTO finishedRouteDetail) {
        logger.info("RouteDetailRestController.finished");
        try {
            logger.info("message " + finishedRouteDetail.getMessage());
            routeDetailService.finishedRouteDetail(finishedRouteDetail);
            return new ResponseDTO<>().success();
        } catch (LogicException e) {
            logger.error("logic error RouteDetailRestController.finished", e);
            return new ResponseDTO<>().error().errorCode(e.getErrorCode()).message(e.getMessage());
        } catch (Exception e) {
            logger.error("error RouteDetailRestController.finished", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @GetMapping("finished/message")
    public ResponseDTO getFinishedMessage() {
        logger.info("RouteDetailRestController.getFinishedMessage");
        try {
            Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getDevicesEntity().getId();
            return new ResponseDTO<>().success(masterArrivalNotesService.getFinishedMessages(devicesId));
        } catch (Exception e) {
            logger.error("error RouteDetailRestController.getFinishedMessage error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @PutMapping("/re-depart")
    public ResponseDTO reDepartRouteDetail() {
        logger.info("RouteDetailRestController.reDepartRouteDetail");
        try {
            routeDetailService.reDepartRouteDetail();
            return new ResponseDTO<>().success();
        } catch (LogicException e) {
            logger.error("logic error RouteDetailRestController.reDepartRouteDetail", e);
            return new ResponseDTO<>().error().errorCode(e.getErrorCode()).message(e.getMessage());
        } catch (Exception e) {
            logger.error("error RouteDetailRestController.reDepartRouteDetail", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }
}
