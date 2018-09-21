package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.dto.api.MessageDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.MessagesEntity;
import jp.co.willwave.aca.web.form.message.FormSearch;
import jp.co.willwave.aca.web.form.message.MessageDeviceDto;
import jp.co.willwave.aca.web.form.message.RoutesActualDTO;

import java.util.List;

public interface MessagesDao extends BaseDao<MessagesEntity> {

    List<MessageDTO> findByRouteDetailIds(List<Long> routeDetailIds) throws CommonException;

    MessageDTO findLastMessageByRouteDetail(List<Long> routeDetailIds) throws CommonException;

    List<Long> getRouteDetailIdByMessage(Long devicesId, String keyword) throws CommonException;

    List<MessagesEntity> findByUserId(Long userId);

    List<RoutesActualDTO> searchMessage(FormSearch formSearch, Integer offset,
                                        Integer maxResults) throws CommonException;

    Long countMessage(FormSearch formSearch) throws CommonException;

    List<MessagesEntity> getRouteDetailEntity(List<Long> routeDetailIds);

    List<MessagesEntity> getRouteDetailEntityByUser(List<Long> routeDetailIds, Long usersId);

    List<MessagesEntity> getMessageNotReadFromDevices(Long usersId);
    List<MessageDeviceDto> getNotReadMessages(Long usersId);
}
