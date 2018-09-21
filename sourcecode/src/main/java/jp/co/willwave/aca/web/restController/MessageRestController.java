package jp.co.willwave.aca.web.restController;

import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.dto.api.*;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.model.entity.RoutesEntity;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.service.*;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageRestController {

    private final Logger logger = Logger.getLogger(MessageRestController.class);

    private final MessagesService messagesService;
    private final RoutesService routesService;
    private final TimeLineService timeLineService;
    private final UserService userService;


    public MessageRestController(MessagesService messagesService,
                                 RoutesService routesService,
                                 TimeLineService timeLineService,
                                 UserService userService) {
        this.messagesService = messagesService;
        this.routesService = routesService;
        this.timeLineService = timeLineService;
        this.userService = userService;
    }

    @GetMapping("/route")
    public ResponseDTO<List<MessageDTO>> getListMessageByDevicesId() {
        logger.info("MessageRestController.getListMessageByDevicesId");
        try {
            Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal()).getDevicesEntity().getId();
            return new ResponseDTO<>().success(messagesService.getMessagesByDevicesId(devicesId));
        } catch (Exception e) {
            logger.error("MessageRestController.getListMessageByDevicesId error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @PutMapping("/{messageId}")
    public ResponseDTO<String> readMessageRoute(@PathVariable("messageId") Long messageId) {
        logger.info("MessageRestController.readMessageRoute");
        try {
            messagesService.readMessageFromDevice(messageId);
            return new ResponseDTO<>().success();
        } catch (LogicException e) {
            logger.error("MessageRestController.readMessageRoute logic exception ", e);
            return new ResponseDTO<>().error().errorCode(e.getErrorCode()).message(e.getMessage());
        } catch (Exception e) {
            logger.error("MessageRestController.readMessageRoute exception ", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @PostMapping("/reply")
    public ResponseDTO<String> replyMessageByDevices(@RequestBody ReplyMessageDTO replyMessageDTO) {
        logger.info("MessageRestController.replyMessageByDevices");
        try {
            messagesService.replyMessageByDevices(replyMessageDTO);
            return new ResponseDTO<>().success();
        } catch (LogicException e) {
            logger.error("MessageRestController.replyMessageByDevices logic exception ", e);
            return new ResponseDTO<>().error().errorCode(e.getErrorCode()).message(e.getMessage());
        } catch (Exception e) {
            logger.error("MessageRestController.replyMessageByDevices error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @GetMapping("/route/{routesId}")
    public ResponseDTO<List<MessageDTO>> findByRouteActual(@PathVariable("routesId") Long routesId) {
        logger.info("MessageRestController.findByRoute");
        try {
            return new ResponseDTO<>().success(messagesService.findByRoutesId(routesId));
        } catch (Exception e) {
            logger.error("MessageRestController.getListMessageByDevicesId error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseDTO<List<RouteActualDTO>> searchMessage(@RequestParam("keyword") String keyword) {
        logger.info("MessageRestController.findByRoute");
        try {
            if (StringUtils.isEmpty(keyword)) {
                return new ResponseDTO<>().success(new ArrayList<>());
            }
            return new ResponseDTO<>()
                    .success(routesService.getRouteActualByMessage(StringUtils.trimWhitespace(keyword)));
        } catch (Exception e) {
            logger.error("MessageRestController.getListMessageByDevicesId error", e);
            return new ResponseDTO<>().error().message(e.getMessage());
        }
    }

    @GetMapping("/timeline/{routeActualId}")
    public ResponseDTO getTimeLineByRouteActualId(@PathVariable("routeActualId") Long routeActualId) {
        try {
            DevicesEntity devicesEntity = ((ExpandUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getDevicesEntity();
            RoutesEntity routeActual = routesService.findById(routeActualId);
            UsersEntity usersEntity = userService.getUserCreateRoute(routeActual.getPlanedRoutesId());
            List<TimeLineDTO> timeLineDTOs = timeLineService.getListTimeLineByRouteActual(routeActualId,
                    usersEntity.getLoginId(),
                    devicesEntity.getLoginId());
            return new ResponseDTO<>().success(timeLineDTOs);
        } catch (Exception e) {
            logger.error("Error exception getTimeLineByRouteActualId ", e);
            return new ResponseDTO<>().error();
        }
    }


}
