package jp.co.willwave.aca.web.restController;

import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.dto.api.ResponseDTO;
import jp.co.willwave.aca.dto.api.RouteMapDTO;
import jp.co.willwave.aca.dto.api.RoutesDTO;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.service.ExpandUserDetails;
import jp.co.willwave.aca.service.RoutesService;
import jp.co.willwave.aca.utilities.ValidatorUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/route")
public class RouteRestController {

    private final Logger logger = Logger.getLogger(RouteRestController.class);

    private final RoutesService routesService;
    protected final ValidatorUtil validatorUtil;

    @Autowired
    public RouteRestController(RoutesService routesService, ValidatorUtil validatorUtil) {
        this.routesService = routesService;
        this.validatorUtil = validatorUtil;
    }

    @GetMapping("/detail")
    public ResponseDTO<RoutesDTO> getListRouteDetailByTime() {
        logger.info("RouteRestController.getListRouteDetailByTime");
        try {
            Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getDevicesEntity().getId();
            return new ResponseDTO<>().success(routesService.getRouteByTime(devicesId));
        } catch (LogicException e) {
            logger.error("RouteRestController.getListRouteDetailByTime logic error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        } catch (Exception e) {
            logger.error("RouteRestController.getListRouteDetailByTime error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @GetMapping("/direction")
    public ResponseDTO<List<RouteMapDTO>> getListRouteDetailMap() {
        logger.info("RouteRestController.getListRouteDetailMap");
        try {
            Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getDevicesEntity().getId();
            return new ResponseDTO<>().success(routesService.getListRouteDetailMap(devicesId));
        } catch (Exception e) {
            logger.error("RouteRestController.getListRouteDetailByTime", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @PostMapping("/start")
    public ResponseDTO startRoute(@RequestParam("routeUpdateTime") Long routeUpdateTime) {
        logger.info("RouteRestController.startRoute");
        try {
            return new ResponseDTO<>().success(routesService.startRoute(routeUpdateTime));
        } catch (LogicException e) {
            logger.error("RouteRestController.getListRouteDetailByTime logic error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        } catch (Exception e) {
            logger.error("RouteRestController.getListRouteDetailByTime error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseDTO<List<RoutesDTO>> getAllRouteActualByDevices() {
        logger.info("RouteRestController.getAllRouteActualByDevices");
        try {
            Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getDevicesEntity().getId();
            return new ResponseDTO<>().success(routesService.getAllRouteActual(devicesId));
        } catch (Exception e) {
            logger.error("RouteRestController.getAllRouteActualByDevices error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @GetMapping("/actual")
    public ResponseDTO getRouteActualByPage(@RequestParam("page") Long page, @RequestParam
            ("numberRecord") Long numberRecord) {
        logger.info("RouteRestController.getRouteActualByPage");
        try {
            Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getDevicesEntity().getId();
            return new ResponseDTO<>().success(routesService.getRouteActualByPage(devicesId, page, numberRecord));
        } catch (Exception e) {
            logger.error("RouteRestController.getAllRouteActualByDevices error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @GetMapping("/{routeId}")
    public ResponseDTO getRouteDetailByRouteId(@PathVariable("routeId") Long routeId) {
        logger.info("RouteRestController.getRouteDetailByRouteId");
        try {
            return new ResponseDTO<>().success(routesService.getRouteDetailByRouteId(routeId));
        } catch (LogicException e) {
            logger.error("RouteRestController.getListRouteDetailByTime logic error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        } catch (Exception e) {
            logger.error("RouteRestController.getListRouteDetailByTime error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @GetMapping("/actual/detail")
    public ResponseDTO getRouteDetailActualToday() {
        logger.info("RouteRestController.getRouteDetailActualToday");
        try {
            DevicesEntity devicesEntity = ((ExpandUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal()).getDevicesEntity();
            RoutesDTO routesDTO = routesService.getRouteActualDetail(devicesEntity.getId());
            if (routesDTO != null && !CollectionUtils.isEmpty(routesDTO.getRouteDetails())) {
                routesDTO.getRouteDetails().forEach(x -> x.setFinished(true));
            }
            return new ResponseDTO<>().success(routesDTO);
        } catch (LogicException e) {
            logger.error("RouteRestController.getListRouteDetailByTime logic error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        } catch (Exception e) {
            logger.error("RouteRestController.getListRouteDetailByTime error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @PutMapping("/finished")
    public ResponseDTO finishedRoute() {
        logger.info("RouteRestController.finishedRoute");
        DevicesEntity devicesEntity = ((ExpandUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getDevicesEntity();
        try {
            routesService.finishedRoute(devicesEntity.getId());
        } catch (LogicException e) {
            logger.error("RouteRestController.finishedRoute logic error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        } catch (Exception e) {
            logger.error("RouteRestController.finishedRoute error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
        return new ResponseDTO().success();
    }


}
