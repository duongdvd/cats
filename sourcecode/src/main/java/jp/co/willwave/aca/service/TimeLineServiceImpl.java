package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.TimeLineDTO;
import jp.co.willwave.aca.model.entity.*;
import jp.co.willwave.aca.utilities.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class TimeLineServiceImpl implements TimeLineService {
    private final RouteDetailService routeDetailService;
    private final MessagesService messagesService;
    private final SafetyConfirmService safetyConfirmService;
    private final EmergencyLogsService emergencyLogsService;
    private final CallLogsService callLogsService;

    public TimeLineServiceImpl(RouteDetailService routeDetailService,
                               MessagesService messagesService,
                               SafetyConfirmService safetyConfirmService,
                               EmergencyLogsService emergencyLogsService,
                               CallLogsService callLogsService) {
        this.routeDetailService = routeDetailService;
        this.messagesService = messagesService;
        this.safetyConfirmService = safetyConfirmService;
        this.emergencyLogsService = emergencyLogsService;
        this.callLogsService = callLogsService;
    }


    @Override
    public List<TimeLineDTO> getListTimeLineByRouteActual(Long routeActualId, String loginUser, String loginDevice) {
        List<TimeLineDTO> timeLineDTOs = new ArrayList<>();
        List<RouteDetailEntity> routeDetailEntities = routeDetailService.findByRouteId(routeActualId);
        if (!CollectionUtils.isEmpty(routeDetailEntities)) {
            List<Long> routeDetailIds = routeDetailEntities.stream().map(RouteDetailEntity::getId).collect(Collectors.toList());
            List<MessagesEntity> messagesEntities = messagesService.findByRoutesDetailIds(routeDetailIds);
            List<SafetyConfirmLogsEntity> safetyConfirmLogsEntities =
                    safetyConfirmService.findByRoutesDetailIds(routeDetailIds);
            List<EmergencyLogsEntity> emergencyLogsEntities =
                    emergencyLogsService.findByRoutesDetailIds(routeDetailIds);
            List<CallLogsEntity> callLogsEntities = callLogsService.findByRoutesDetailIds(routeDetailIds);
            timeLineDTOs = CommonUtil.mergeTimeline(messagesEntities,
                    routeDetailEntities,
                    safetyConfirmLogsEntities,
                    emergencyLogsEntities,
                    callLogsEntities,
                    loginUser,
                    loginDevice);
        }
        return timeLineDTOs;
    }

    @Override
    public List<TimeLineDTO> getListTimeLineByRouteActualAndUser(Long routeActualId, String loginUser, String loginDevice, Long userId) {
        List<TimeLineDTO> timeLineDTOs = new ArrayList<>();
        List<RouteDetailEntity> routeDetailEntities = routeDetailService.findByRouteId(routeActualId);
        if (!CollectionUtils.isEmpty(routeDetailEntities)) {
            List<Long> routeDetailIds = routeDetailEntities.stream().map(RouteDetailEntity::getId).collect(Collectors.toList());
            List<MessagesEntity> messagesEntities = messagesService.findByRoutesDetailIdsAndUser(routeDetailIds, userId);
            List<SafetyConfirmLogsEntity> safetyConfirmLogsEntities =
                    safetyConfirmService.findByRoutesDetailIds(routeDetailIds);
            List<EmergencyLogsEntity> emergencyLogsEntities =
                    emergencyLogsService.findByRoutesDetailIds(routeDetailIds);
            List<CallLogsEntity> callLogsEntities = callLogsService.findByRoutesDetailIds(routeDetailIds);
            timeLineDTOs = CommonUtil.mergeTimeline(messagesEntities,
                    routeDetailEntities,
                    safetyConfirmLogsEntities,
                    emergencyLogsEntities,
                    callLogsEntities,
                    loginUser,
                    loginDevice);
        }
        return timeLineDTOs;
    }
}
