package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dao.DevicesDao;
import jp.co.willwave.aca.dao.EmergencyLogsDao;
import jp.co.willwave.aca.dao.MessagesDao;
import jp.co.willwave.aca.dao.SafetyConfirmLogsDao;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.*;
import jp.co.willwave.aca.model.enums.LogsType;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.message.MessageDeviceDto;
import jp.co.willwave.aca.web.form.notification.MessageForm;
import jp.co.willwave.aca.web.form.notification.NotificationDTO;
import jp.co.willwave.aca.web.form.notification.NotificationForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class NotificationServiceImpl implements NotificationService {


    private final MessagesDao messagesDao;
    private final EmergencyLogsDao emergencyLogsDao;
    private final SafetyConfirmLogsDao safetyConfirmLogsDao;
    private final DevicesDao devicesDao;

    public NotificationServiceImpl(MessagesDao messagesDao,
                                   EmergencyLogsDao emergencyLogsDao,
                                   SafetyConfirmLogsDao safetyConfirmLogsDao,
                                   DevicesDao devicesDao) {
        this.messagesDao = messagesDao;
        this.emergencyLogsDao = emergencyLogsDao;
        this.safetyConfirmLogsDao = safetyConfirmLogsDao;
        this.devicesDao = devicesDao;
    }

    @Override
    public List<NotificationForm> findNotification() {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        List<SafetyConfirmLogsEntity> safetyConfirmLogsNotRead = safetyConfirmLogsDao.getSafetyConfirmLogsNotRead
                (userInfo.getId());
        List<EmergencyLogsEntity> emergencyLogsNotRead = emergencyLogsDao.getEmergencyLogs(userInfo.getId());
        return convertNotification(safetyConfirmLogsNotRead, emergencyLogsNotRead, userInfo.getId());
    }

    @Override
    public List<MessageForm> findMessage() {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        List<MessageForm> messageForms = new ArrayList<>();
        List<MessageDeviceDto> messagesNotRead = messagesDao.getNotReadMessages(userInfo.getId());
        if (!CollectionUtils.isEmpty(messagesNotRead)) {
            messagesNotRead.forEach(message -> {
                MessagesEntity mes = message.getMessagesEntity();
                MessageForm messageForm = new MessageForm();
                messageForm.setId(mes.getId());
                messageForm.setDevicesId(mes.getId());
                messageForm.setRouteDetailId(mes.getRouteDetailId());
                messageForm.setTime(mes.getCreatedTime());
                messageForm.setMessage(mes.getContent());
                messageForm.setDeviceName(message.getDeviceLoginId());
                messageForms.add(messageForm);
            });
        }
        Collections.sort(messageForms, (o1, o2) -> {
            return -o1.getTime().compareTo(o2.getTime());
        });
        return messageForms;
    }

    @Override
    public Long readNotification(NotificationDTO notificationDTO) throws CommonException {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        Long routeDetailId = 0L;
        Date sysDate = new Date();
        switch (notificationDTO.getType()) {
            case MESSAGE:
                MessagesEntity messages = messagesDao.findById(notificationDTO.getId(), MessagesEntity.class);
                if (messages != null
                        && userInfo.getId().equals(messages.getUsersId())) {
                    messages.setIsRead(true);
                    messages.setUpdateBy(userInfo.getId());
                    messages.setUpdateDate(sysDate);
                    messagesDao.update(messages);
                    routeDetailId = messages.getRouteDetailId();
                }
                break;
            case EMERGENCY:
                EmergencyLogsEntity emergencyLogs = emergencyLogsDao.findById(notificationDTO.getId(),
                        EmergencyLogsEntity.class);
                if (emergencyLogs != null
                        && userInfo.getId().equals(emergencyLogs.getUsersId())) {
                    emergencyLogs.setIsRead(true);
                    emergencyLogs.setUpdateBy(userInfo.getId());
                    emergencyLogs.setUpdateDate(sysDate);
                    emergencyLogsDao.update(emergencyLogs);
                    routeDetailId = emergencyLogs.getRouteDetailId();
                }
                break;
            case SAFETY_CONFIRM:
                SafetyConfirmLogsEntity safetyConfirmLogs = safetyConfirmLogsDao.findById(notificationDTO.getId(),
                        SafetyConfirmLogsEntity.class);
                if (safetyConfirmLogs != null
                        && userInfo.getId().equals(safetyConfirmLogs.getUsersId())) {
                    safetyConfirmLogs.setIsRead(true);
                    safetyConfirmLogs.setUpdateBy(userInfo.getId());
                    safetyConfirmLogs.setUpdateDate(sysDate);
                    safetyConfirmLogsDao.update(safetyConfirmLogs);
                    routeDetailId = safetyConfirmLogs.getRouteDetailId();
                }
                break;
            default:
                break;
        }
        return routeDetailId;
    }

    private List<NotificationForm> convertNotification(List<SafetyConfirmLogsEntity> safetyConfirmLogs,
                                                       List<EmergencyLogsEntity> emergencyLogs, Long usersId) {
        List<DevicesEntity> devicesEntities = devicesDao.findDevicesManaged(usersId);
        Map<Long, DevicesEntity> mapDevices = devicesEntities.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
        List<NotificationForm> notifications = new ArrayList<>();
        if (!CollectionUtils.isEmpty(safetyConfirmLogs)) {
            safetyConfirmLogs.forEach(safetyConfirm -> {
                NotificationForm notification = new NotificationForm();
                notification.setId(safetyConfirm.getId());
                notification.setType(LogsType.SAFETY_CONFIRM);
                String safetyConfirmPattern
                        = mapDevices.get(safetyConfirm.getDevicesId()).getLoginId() + ": " + safetyConfirm.getAction()
                        + "-" + safetyConfirm.getNotificationTime();
                notification.setMessage(safetyConfirmPattern);
                notification.setRouteDetailId(safetyConfirm.getRouteDetailId());
                notification.setTime(safetyConfirm.getNotificationTime());
                notifications.add(notification);
            });
        }
        if (!CollectionUtils.isEmpty(emergencyLogs)) {
            emergencyLogs.forEach(emergency -> {
                NotificationForm notification = new NotificationForm();
                notification.setId(emergency.getId());
                notification.setType(LogsType.EMERGENCY);
                String notificationPattern
                        = mapDevices.get(emergency.getDevicesId()).getLoginId() + ": " + emergency.getMessage()
                        + "-" + emergency.getNotificationTime();
                notification.setMessage(notificationPattern);
                notification.setRouteDetailId(emergency.getRouteDetailId());
                notification.setTime(emergency.getNotificationTime());
                notifications.add(notification);
            });
        }
        notifications.sort(((o1, o2) -> {
            return -o1.getTime().compareTo(o2.getTime());
        }));
        return notifications;
    }
}
