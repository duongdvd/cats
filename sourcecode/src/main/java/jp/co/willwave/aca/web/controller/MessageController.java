package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.dto.api.TimeLineDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.exception.LogicWebException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.model.entity.RouteDetailEntity;
import jp.co.willwave.aca.model.entity.RoutesEntity;
import jp.co.willwave.aca.model.enums.LogsType;
import jp.co.willwave.aca.service.*;
import jp.co.willwave.aca.utilities.DateUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.message.DeviceForm;
import jp.co.willwave.aca.web.form.message.FormSearch;
import jp.co.willwave.aca.web.form.message.MessageDTO;
import jp.co.willwave.aca.web.form.message.RoutesActualDTO;
import jp.co.willwave.aca.web.form.notification.NotificationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MessageController extends AbstractController {

    private final MessagesService messagesService;
    private final DevicesService devicesService;
    private final RoutesService routesService;
    private final RouteDetailService routeDetailService;
    private final NotificationService notificationService;
    private final TimeLineService timeLineService;

    public MessageController(MessagesService messagesService,
                             DevicesService devicesService,
                             RoutesService routesService,
                             RouteDetailService routeDetailService,
                             NotificationService notificationService, TimeLineService timeLineService) {
        this.messagesService = messagesService;
        this.devicesService = devicesService;
        this.routesService = routesService;
        this.routeDetailService = routeDetailService;
        this.notificationService = notificationService;
        this.timeLineService = timeLineService;
    }

    @RequestMapping(value = "/viewMessage", method = RequestMethod.GET)
    public String viewMessage(Model model, @RequestParam(value = "routeActualId", required = false) Long routeActualId
                                ,Integer offset, Integer maxResults)
            throws CommonException {
        FormSearch searchForm = (FormSearch) SessionUtil.getAttribute("searchRouteForm");
        if (searchForm == null) {
            searchForm = new FormSearch();
        }

        searchForm.setTextMessage(StringUtils.trimWhitespace(searchForm.getTextMessage()));
        searchForm.setRoutesName(StringUtils.trimWhitespace(searchForm.getRoutesName()));

        Long userLoginId = getLoginUserId();
        List<DeviceForm> devicesList = devicesService.findDevicesManaged(userLoginId);
        String iconPath = "";
        Long totalCount;
        List<RoutesActualDTO> routesActualDTOs = new ArrayList<>();
        boolean fromNotification = false;
        if (routeActualId != null) {
            RoutesEntity routesEntity = routesService.findById(routeActualId);
            DevicesEntity deviceDetail = devicesService.findById(routesEntity.getDevicesId());
            iconPath = deviceDetail.getIconPath();
            searchForm = new FormSearch();

            searchForm.setFromDate(DateUtil.convertSimpleDateFormat(routesEntity.getActualDate(), "yyyy-MM-dd "));
            searchForm.setToDate(DateUtil.convertSimpleDateFormat(routesEntity.getActualDate(), "yyyy-MM-dd "));
            searchForm.setDevicesId(routesEntity.getDevicesId());
            searchForm.setRoutesName(routesEntity.getName());
            RoutesActualDTO routesActualDTO = new RoutesActualDTO();
            routesActualDTO.setId(routesEntity.getId());

            routesActualDTO.setUsersId(userLoginId);
            routesActualDTO.setRoutesName(routesEntity.getName());
            routesActualDTO.setDevicesId(routesEntity.getDevicesId());
            routesActualDTO.setDate(routesEntity.getActualDate());
            Map<Long, String> mapNameDevice = devicesList.stream().collect(Collectors.toMap(DeviceForm::getId,
                    DeviceForm::getName));
            routesActualDTO.setDevicesName(mapNameDevice.get(routesEntity.getDevicesId()));
            routesActualDTO.setUsersName(getUserLoginInfo().getLoginId());
            routesActualDTOs.add(routesActualDTO);
            totalCount = 1L;
            fromNotification = true;
        } else {
            routesActualDTOs = messagesService.searchMessage(searchForm, offset, maxResults);
            totalCount = messagesService.countMessage(searchForm);
        }

        model.addAttribute("iconPath", iconPath);
        model.addAttribute("routesList", routesActualDTOs);
        model.addAttribute("searchRouteForm", searchForm);
        model.addAttribute("devicesList", devicesList);
        model.addAttribute("offset", offset);
        model.addAttribute("maxResults", maxResults);
        model.addAttribute("count", totalCount);
        model.addAttribute("fromNotification", fromNotification);
        if (routeActualId != null) {
            model.addAttribute("routeId", routeActualId);
        }
        return "/message/message";
    }

    @RequestMapping(value = "/searchMessagePaging", method = RequestMethod.GET)
    public String searchMessage(Integer offset, Integer maxResults) {
        logger.info("search device list paging");
        return pagingRedirectToList("viewMessage", offset, 2);
    }

    @RequestMapping(value = "/searchMessage", method = RequestMethod.GET)
    public String searchMessage(@ModelAttribute("searchRouteForm") FormSearch searchMessage,
                                BindingResult bindingResult) {
        logger.info("search device list paging");
        SessionUtil.setAttribute("searchRouteForm", searchMessage);
        return redirect("viewMessage?routeActualId");
    }

    @GetMapping("/messageDetail/{routeId}")
    public ResponseEntity getLogsTime(@PathVariable("routeId") Long routeId) {
        try {
            UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
            DevicesEntity devicesEntity = devicesService.findByRouteActualId(routeId);
            String icon_path = "";
            if (devicesEntity.getIconPath() != null && devicesEntity.getIconPath().length() > 0) {
                icon_path = devicesEntity.getIconPath();
                if(icon_path.indexOf("icon") != -1){
                    icon_path = icon_path.substring(icon_path.indexOf("icon"), icon_path.length());
                }
            }
            List<TimeLineDTO> timeLineDTOs = timeLineService.getListTimeLineByRouteActualAndUser(routeId, userInfo.getLoginId(),
                    devicesEntity.getLoginId(), userInfo.getId());
            for (int i = 0; i < timeLineDTOs.size(); i++) {
                timeLineDTOs.get(i).setIconPath(icon_path);
            }
            return ResponseEntity.ok(timeLineDTOs);
        } catch (CommonException e) {
            logger.error("Error common when message detail ", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @RequestMapping(value = "notification/readDetail/{id}/{type}", method = RequestMethod.GET)
    public String readNotification(@PathVariable("id") Long id, @PathVariable("type") LogsType type) {
        try {
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setId(id);
            notificationDTO.setType(type);
            Long routeDetailId = notificationService.readNotification(notificationDTO);
            RouteDetailEntity routeDetailEntity = routeDetailService.findById(routeDetailId);
            return redirect(String.format("viewMessage?routeActualId=%d#%s-%s", routeDetailEntity.getRoutesId(), id,
                    type
                            .toString()));
        } catch (CommonException e) {
            logger.error("Error when read message", e);
            return redirect("viewMessage?routeActualId");
        }
    }

    @RequestMapping(value = "message/thread/{devicesId}", method = RequestMethod.GET)
    public String getMessageListByDevicesId(@PathVariable("devicesId") Long devicesId, Model model) {
        try {
            RoutesEntity routeActual = routesService.findRouteActualByDevices(devicesId);
            model.addAttribute("devicesId", devicesId);
            model.addAttribute("routeId", routeActual.getId());
        } catch (Exception e) {
            logger.error("Exception error ", e);
        }
        return "message/testThreadMessage";
    }

    @PostMapping(value = "message/thread/send")
    public ResponseEntity sendMessage(@RequestBody MessageDTO messageDTO) {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        try {
            messagesService.sendMessageFromAdmin(messageDTO);
        } catch (LogicWebException e) {
            logger.error("LogicWebException error ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getErrorMessage());
        } catch (Exception e) {
            logger.error("LogicWebException error ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error system");
        }
        TimeLineDTO timeLineDTO = new TimeLineDTO();
        timeLineDTO.setTitle(userInfo.getLoginId());
        timeLineDTO.setContent(messageDTO.getMessage());
        timeLineDTO.setTimeIn(new Date());
        timeLineDTO.setTimeOut(new Date());
        timeLineDTO.setType(LogsType.MESSAGE_ADMIN);
        return ResponseEntity.ok(timeLineDTO);
    }


}
