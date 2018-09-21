package jp.co.willwave.aca.service;

import jp.co.willwave.aca.common.ErrorCodeConfig;
import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.dao.MessagesDao;
import jp.co.willwave.aca.dao.RouteDetailDao;
import jp.co.willwave.aca.dao.RoutesDao;
import jp.co.willwave.aca.dto.api.MessageDTO;
import jp.co.willwave.aca.dto.api.ReplyMessageDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.exception.LogicWebException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.CustomersEntity;
import jp.co.willwave.aca.model.entity.MessagesEntity;
import jp.co.willwave.aca.model.entity.RouteDetailEntity;
import jp.co.willwave.aca.model.entity.RoutesEntity;
import jp.co.willwave.aca.utilities.CatsMessageResource;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.message.FormSearch;
import jp.co.willwave.aca.web.form.message.RoutesActualDTO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class MessagesServiceImpl implements MessagesService {
    private Logger logger = Logger.getLogger(MessagesServiceImpl.class);

    private final RoutesDao routesDao;
    private final RouteDetailDao routeDetailDao;
    private final MessagesDao messagesDao;
    private final CatsMessageResource messageSource;

    @Autowired
    public MessagesServiceImpl(RoutesDao routesDao,
                               RouteDetailDao routeDetailDao,
                               MessagesDao messagesDao,
                               CatsMessageResource messageSource) {
        this.routesDao = routesDao;
        this.routeDetailDao = routeDetailDao;
        this.messagesDao = messagesDao;
        this.messageSource = messageSource;
    }

    @Override
    public List<MessageDTO> getMessagesByDevicesId(Long devicesId) throws CommonException {
        logger.info("MessagesLogic.getMessagesByDevicesId");
        RoutesEntity routeActual = routesDao.getRouteActualRunningByDevicesId(devicesId);
        if (routeActual == null) {
            return new ArrayList<>();
        }
        List<MessageDTO> messageDTOs = findByActualRoutesId(routeActual.getId());
        if (!CollectionUtils.isEmpty(messageDTOs)) {
            messageDTOs.forEach(messageDTO -> {
                messageDTO.setRoutesId(routeActual.getId());
            });
        }
        return messageDTOs;
    }

    @Override
    public void readMessageFromDevice(Long messageId) throws Exception {
        logger.info("MessagesLogic.readMessageFromDevice");
        Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getDevicesEntity().getId();
        MessagesEntity messagesEntity = messagesDao.findById(messageId, MessagesEntity.class);
        if (messagesEntity == null || messagesEntity.getDevicesId() == null) {
            throw new LogicException(ErrorCodeConfig.MESSAGES_NOT_FOUND, messageSource.get(Constant.ErrorCode.MESSAGE_NOT_FOUND, new String[]{""}));
        }
        if (!devicesId.equals(messagesEntity.getDevicesId())) {
            throw new LogicException(ErrorCodeConfig.NOT_AUTHORIZATION, messageSource.get(Constant.ErrorCode.HAVE_NO_PERMISSION, new String[]{""}));
        }
        messagesEntity.setIsRead(true);
        messagesEntity.setUpdateBy(devicesId);
        messagesEntity.setUpdateDate(new Timestamp((new Date()).getTime()));
        messagesDao.update(messagesEntity);
    }

    @Override
    public void replyMessageByDevices(ReplyMessageDTO replyMessageDTO) throws Exception {
        logger.info("MessagesLogic.replyMessageByDevices");
        Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getDevicesEntity().getId();
        Date sysDate = new Date();
        MessagesEntity messagesEntity = messagesDao.findById(replyMessageDTO.getMessageId(), MessagesEntity.class);
        if (messagesEntity == null || !devicesId.equals(messagesEntity.getDevicesId())) {
            throw new LogicException(ErrorCodeConfig.MESSAGES_NOT_FOUND, messageSource.get(Constant.ErrorCode.MESSAGE_NOT_FOUND, new String[]{""}));
        }
        if (messagesEntity.getIsReply()) {
            return;
        }
        messagesEntity.setIsReply(true);
        messagesEntity.setIsRead(true);
        messagesEntity.setUpdateDate(new Timestamp(sysDate.getTime()));
        messagesEntity.setUpdateBy(devicesId);
        messagesDao.update(messagesEntity);

        MessagesEntity replyMessage = new MessagesEntity();
        replyMessage.setUsersId(messagesEntity.getUsersId());
        replyMessage.setDevicesId(devicesId);
        replyMessage.setContent(replyMessageDTO.getContent());
        replyMessage.setIsFromDevice(true);
        replyMessage.setParentMessageId(messagesEntity.getId());
        replyMessage.setCreatedTime(sysDate);
        replyMessage.setRouteDetailId(messagesEntity.getRouteDetailId());
        replyMessage.setIsRead(false);
        replyMessage.setIsReply(true);
        replyMessage.setCreateBy(devicesId);
        replyMessage.setCreateBy(null);
        replyMessage.setCreateDate(new Timestamp(sysDate.getTime()));
        replyMessage.setUpdateBy(null);
        replyMessage.setUpdateDate(new Timestamp(sysDate.getTime()));
        messagesDao.insert(replyMessage);
    }

    @Override
    public List<MessageDTO> findByRoutesId(Long routesId) throws CommonException {
        logger.info("MessagesLogic.findByRoutesId");
        Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getDevicesEntity().getId();
        RoutesEntity routesEntity = routesDao.findById(routesId, RoutesEntity.class);
        if (routesEntity != null
                && routesEntity.getDevicesId().equals(devicesId)
                && routesEntity.getActualDate() != null) {
            return findByActualRoutesId(routesId);
        }
        return new ArrayList<>();
    }

    @Override
    public List<RoutesActualDTO> searchMessage(FormSearch formSearch, Integer offset, Integer maxResults) throws CommonException {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        formSearch.setUsersId(userInfo.getId());
        List<RoutesActualDTO> routesActualDTOs = messagesDao.searchMessage(formSearch, offset, maxResults);
        if (!CollectionUtils.isEmpty(routesActualDTOs)) {
            routesActualDTOs.forEach(routesActualDTO -> {
                routesActualDTO.setUsersId(userInfo.getId());
                routesActualDTO.setUsersName(userInfo.getLoginId());
            });
        }
        return routesActualDTOs;
    }

    @Override
    public Long countMessage(FormSearch formSearch) throws CommonException {
        return messagesDao.countMessage(formSearch);
    }

    @Override
    public List<MessagesEntity> findByRoutesDetailIds(List<Long> routeDetailIds) {
        return messagesDao.getRouteDetailEntity(routeDetailIds);
    }

    @Override
    public List<MessagesEntity> findByRoutesDetailIdsAndUser(List<Long> routeDetailIds, Long userId) {
        return messagesDao.getRouteDetailEntityByUser(routeDetailIds, userId);
    }

    @Override
    public void sendMessageFromAdmin(jp.co.willwave.aca.web.form.message.MessageDTO messageDTO) throws Exception {
        if (messageDTO.getDevicesId() == null) {
            throw new LogicWebException(messageSource.getMessage
                    (Constant.ErrorCode.NOT_EMPTY, new String[]{"message.devicesId"})
            );
        }
        if (StringUtils.isEmpty(messageDTO.getMessage())) {
            throw new LogicWebException(messageSource.getMessage
                    (Constant.ErrorCode.NOT_EMPTY, new String[]{"message.message"})
            );
        }
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        Date sysDate = new Date();
        RoutesEntity routesActual = routesDao.getRouteActualRunningByDevicesId(messageDTO.getDevicesId());
        if (routesActual == null) {
            throw new LogicWebException(messageSource.getMessage
                    (Constant.ErrorCode.NOT_EXISTS, new String[]{"message.route.actual"})
            );
        }
        RoutesEntity routesPlan = routesDao.findById(routesActual.getPlanedRoutesId(), RoutesEntity.class);
        if (!routesPlan.getCreateBy().equals(userInfo.getId())) {
            throw new LogicWebException(messageSource.getMessage
                    (Constant.ErrorCode.USER_NOT_MANAGED_DEVICE, new String[]{userInfo.getLoginId()})
            );
        }
        RouteDetailEntity routeDetail = routeDetailDao.findRouteDetailActualLastFinishByRouteId(routesActual.getId());
        MessagesEntity message = new MessagesEntity();
        message.setIsRead(Boolean.FALSE);
        message.setIsReply(Boolean.FALSE);
        message.setContent(StringUtils.trimWhitespace(messageDTO.getMessage()));
        message.setDevicesId(messageDTO.getDevicesId());
        message.setIsFromDevice(Boolean.FALSE);
        message.setUsersId(userInfo.getId());
        message.setRouteDetailId(routeDetail.getId());
        message.setCreatedTime(sysDate);
        message.setCreateDate(sysDate);
        message.setCreateBy(userInfo.getId());
        message.setUpdateDate(sysDate);
        message.setUpdateBy(userInfo.getId());
        messagesDao.insert(message);
    }

    private List<MessageDTO> findByActualRoutesId(Long routesId) throws CommonException {
        List<RouteDetailEntity> routeDetails = routeDetailDao.getRouteDetailByRouteId(routesId);
        if (!CollectionUtils.isEmpty(routeDetails)) {
            List<Long> routeDetailIds = routeDetails.stream()
                    .map(RouteDetailEntity::getId).collect(Collectors.toList());
            Map<Long, CustomersEntity> customerMap = routeDetails.stream()
                    .collect(Collectors.toMap(RouteDetailEntity::getId, RouteDetailEntity::getCustomers));
            List<MessageDTO> messageDTOs = messagesDao.findByRouteDetailIds(routeDetailIds);
            if (!CollectionUtils.isEmpty(messageDTOs)) {
                messageDTOs.forEach(messageDTO -> {
                    messageDTO.setRoutesId(routesId);
                    messageDTO.setRouteDetailName(customerMap.get(messageDTO.getRouteDetailId()).getName());
                });
                return messageDTOs;
            }
        }
        return new ArrayList<>();
    }

}

