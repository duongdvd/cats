package jp.co.willwave.aca.service;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.web.form.notification.MessageForm;
import jp.co.willwave.aca.web.form.notification.NotificationDTO;
import jp.co.willwave.aca.web.form.notification.NotificationForm;

import java.util.List;

public interface NotificationService {
    List<NotificationForm> findNotification();

    List<MessageForm> findMessage();

    Long readNotification(NotificationDTO notificationDTO) throws CommonException;
}
