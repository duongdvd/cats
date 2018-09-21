package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.MessageDTO;
import jp.co.willwave.aca.dto.api.ReplyMessageDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.MessagesEntity;
import jp.co.willwave.aca.web.form.message.FormSearch;
import jp.co.willwave.aca.web.form.message.RoutesActualDTO;

import java.util.List;

public interface MessagesService {
    List<MessageDTO> getMessagesByDevicesId(Long devicesId) throws CommonException;

    void readMessageFromDevice(Long messageId) throws Exception;

    void replyMessageByDevices(ReplyMessageDTO replyMessageDTO) throws Exception;

    List<MessageDTO> findByRoutesId(Long routesId) throws CommonException;

    List<RoutesActualDTO> searchMessage(FormSearch formSearch, Integer offset, Integer maxResults) throws CommonException;

    Long countMessage(FormSearch formSearch) throws CommonException;

    List<MessagesEntity> findByRoutesDetailIds(List<Long> routeDetailIds);

    List<MessagesEntity> findByRoutesDetailIdsAndUser(List<Long> routeDetailIds, Long userId);

    void sendMessageFromAdmin(jp.co.willwave.aca.web.form.message.MessageDTO messageDTO) throws Exception;
}
