package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.dto.api.MessageDTO;
import jp.co.willwave.aca.dto.api.RouteDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.MessagesEntity;
import jp.co.willwave.aca.model.entity.RoutesEntity;
import jp.co.willwave.aca.utilities.DateUtil;
import jp.co.willwave.aca.web.form.message.FormSearch;
import jp.co.willwave.aca.web.form.message.MessageDeviceDto;
import jp.co.willwave.aca.web.form.message.RoutesActualDTO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class MessagesDaoImpl extends BaseDaoImpl<MessagesEntity> implements MessagesDao {

    public MessagesDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<MessageDTO> findByRouteDetailIds(List<Long> routeDetailIds) throws CommonException {
        List<MessagesEntity> messagesEntities = sessionFactory.getCurrentSession()
                .createQuery("FROM MessagesEntity WHERE routeDetailId IN :routeDetailIds ")
                .setParameter("routeDetailIds", routeDetailIds)
                .getResultList();
        List<MessageDTO> messageDTOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(messagesEntities)) {
            messagesEntities.forEach(messagesEntity -> {
                MessageDTO messageDTO = convertFromMessagesEntity(messagesEntity);
                messageDTOs.add(messageDTO);
            });
        }
        return messageDTOs;
    }

    @Override
    public MessageDTO findLastMessageByRouteDetail(List<Long> routeDetailIds) throws CommonException {
        StringBuilder sql = new StringBuilder("FROM MessagesEntity ");
        sql.append("WHERE routeDetailId IN :routeDetailIds ");
        sql.append("AND isFromDevice = false ");
        sql.append("ORDER BY createdTime DESC ");
        List<MessagesEntity> messagesEntities = sessionFactory.getCurrentSession()
                .createQuery(sql.toString(), MessagesEntity.class)
                .setParameter("routeDetailIds", routeDetailIds)
                .setMaxResults(1)
                .getResultList();
        if (!CollectionUtils.isEmpty(messagesEntities)) {
            return convertFromMessagesEntity(messagesEntities.get(0));
        }
        return null;
    }

    @Override
    public List<Long> getRouteDetailIdByMessage(Long devicesId, String keyword) throws CommonException {
        //TODO query by date
        StringBuilder sql = new StringBuilder("SELECT DISTINCT(routeDetailId) FROM MessagesEntity ");
        sql.append("WHERE devicesId = :devicesId ");
        sql.append("AND UPPER(content) LIKE CONCAT('%', CONCAT(:keyword, '%')) ");
        sql.append("ORDER BY createdTime DESC ");
        return sessionFactory.getCurrentSession()
                .createQuery(sql.toString(), Long.class)
                .setParameter("devicesId", devicesId)
                .setParameter("keyword", keyword.toUpperCase())
                .getResultList();
    }

    @Override
    public List<MessagesEntity> findByUserId(Long userId) {
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<MessagesEntity> criteriaQuery = criteriaBuilder.createQuery(MessagesEntity.class);
        Root<MessagesEntity> root = criteriaQuery.from(MessagesEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("usersId"), userId));
        return sessionFactory.getCurrentSession().createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<RoutesActualDTO> searchMessage(FormSearch formSearch, Integer offset,
                                               Integer maxResults) throws CommonException {
        Query<Object[]> query = buildSqlSearch(formSearch);
        offset = offset != null ? offset : 0;
        maxResults = maxResults != null ? maxResults : 2;
        query.setMaxResults(maxResults);
        query.setFirstResult(offset);
        List<Object[]> restList = query.getResultList();
        List<RoutesActualDTO> routesActualDTOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(restList)) {
            restList.forEach(o -> {
                RoutesActualDTO routesActualDTO = new RoutesActualDTO();
                if (o[0] instanceof RoutesEntity) {
                    RoutesEntity r = (RoutesEntity) o[0];
                    routesActualDTO.setId(r.getId());
                    routesActualDTO.setRoutesName(r.getName());
                    routesActualDTO.setDevicesId(r.getDevicesId());
                    routesActualDTO.setDate(r.getActualDate());
                }
                routesActualDTO.setDevicesName((String) o[1]);
                routesActualDTOs.add(routesActualDTO);
            });
        }
        return routesActualDTOs;
    }

    @Override
    public Long countMessage(FormSearch formSearch) throws CommonException {
        Query<Object[]> query = buildSqlSearch(formSearch);
        List<Object[]> restList = query.getResultList();
        if (CollectionUtils.isEmpty(restList)) {
            return 0L;
        }
        return Long.valueOf(restList.size());
    }

    @Override
    public List<MessagesEntity> getRouteDetailEntity(List<Long> routeDetailIds) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM MessagesEntity WHERE routeDetailId IN :routeDetailIds ORDER BY  createdTime")
                .setParameter("routeDetailIds", routeDetailIds)
                .getResultList();
    }

    @Override
    public List<MessagesEntity> getRouteDetailEntityByUser(List<Long> routeDetailIds, Long usersId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM MessagesEntity WHERE routeDetailId IN :routeDetailIds AND usersId = :usersId ORDER BY  createdTime")
                .setParameter("routeDetailIds", routeDetailIds)
                .setParameter("usersId", usersId)
                .getResultList();
    }

    @Override
    public List<MessagesEntity> getMessageNotReadFromDevices(Long usersId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM MessagesEntity WHERE usersId = :usersId AND isRead = FALSE AND isFromDevice = TRUE" +
                        "  AND DATE(createdTime) = CURDATE() ")
                .setParameter("usersId", usersId)
                .getResultList();
    }

    @Override
    public List<MessageDeviceDto> getNotReadMessages(Long usersId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT mes, dev.loginId");
        sql.append(" FROM MessagesEntity mes");
        sql.append("    INNER JOIN DevicesEntity dev ");
        sql.append("        ON mes.devicesId = dev.id ");
        sql.append(" WHERE ");
        sql.append("    mes.usersId = :usersId ");
        sql.append("    AND mes.isRead = false ");
        sql.append("    AND DATE(mes.createdTime) = CURDATE() ");
        sql.append("    AND mes.isFromDevice = TRUE ");

        Query<Object[]> query = sessionFactory.getCurrentSession()
            .createQuery(sql.toString(), Object[].class)
            .setParameter("usersId", usersId);
        List<Object[]> result = query.getResultList();

        List<MessageDeviceDto> messageDeviceDtos = new ArrayList<>();
        for (Object[] object : result) {
            if (object[0] instanceof MessagesEntity && object[1] instanceof String) {
                messageDeviceDtos.add(new MessageDeviceDto((MessagesEntity) object[0], (String) object[1]));
            }
        }

        return messageDeviceDtos;
    }


    private Query<Object[]> buildSqlSearch(FormSearch formSearch) throws CommonException {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT r, d.loginId FROM RoutesEntity r, DevicesEntity d ");
        if (!StringUtils.isEmpty(formSearch.getTextMessage())) {
            sql.append(", MessagesEntity m, RouteDetailEntity rd ");
        }
        sql.append(" WHERE r.devicesId = d.id ");
        if (!StringUtils.isEmpty(formSearch.getTextMessage())) {
            sql.append("AND m.routeDetailId = rd.id AND rd.routesId = r.id  ");
            sql.append("AND UPPER(m.content) LIKE CONCAT('%', CONCAT(:textMessage, '%')) ");
            sql.append("AND m.deleteFlg = FALSE ");
        }

        if (formSearch.getUsersId() != null) {
            sql.append("AND r.planedRoutesId IN ( SELECT id FROM RoutesEntity WHERE createBy = :usersId )");
        }

        sql.append("AND r.actualDate IS NOT NULL ");
        if (formSearch.getFromDate() != null && !StringUtils.isEmpty(formSearch.getFromDate())) {
            sql.append("AND DATE(r.actualDate) >= DATE(:fromDate) ");
        }
        if (formSearch.getToDate() != null && !StringUtils.isEmpty(formSearch.getToDate())) {
            sql.append("AND DATE(r.actualDate) <= DATE(:toDate) ");
        }
        if (!StringUtils.isEmpty(formSearch.getRoutesName())) {
            sql.append("AND UPPER(r.name) LIKE CONCAT('%', CONCAT(:name, '%')) ");
        }
        if (formSearch.getDevicesId() != null) {
            sql.append("AND d.id = :devicesId ");
        }
        sql.append("AND r.deleteFlg = FALSE ");
        sql.append("AND d.deleteFlg = FALSE ");
        sql.append("ORDER BY r.actualDate ");
        Query<Object[]> query = sessionFactory.getCurrentSession().createQuery(sql.toString());
        if (!StringUtils.isEmpty(formSearch.getTextMessage())) {
            query.setParameter("textMessage", StringUtils.trimWhitespace(formSearch.getTextMessage().toUpperCase()));
        }
        if (formSearch.getUsersId() != null) {
            query.setParameter("usersId", formSearch.getUsersId());
        }
        if (!StringUtils.isEmpty(formSearch.getFromDate())) {
            query.setParameter("fromDate", formSearch.getFromDate().toString());
        }
        if (!StringUtils.isEmpty(formSearch.getToDate())) {
            query.setParameter("toDate", formSearch.getToDate().toString());
        }
        if (!StringUtils.isEmpty(formSearch.getRoutesName())) {
            query.setParameter("name", StringUtils.trimWhitespace(formSearch.getRoutesName().toUpperCase()));
        }
        if (formSearch.getDevicesId() != null) {
            query.setParameter("devicesId", formSearch.getDevicesId());
        }
        return query;
    }

    private MessageDTO convertFromMessagesEntity(MessagesEntity messagesEntity) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(messagesEntity.getId());
        messageDTO.setDevicesId(messagesEntity.getDevicesId());
        messageDTO.setContent(messagesEntity.getContent());
        messageDTO.setRouteDetailId(messagesEntity.getRouteDetailId());
        messageDTO.setIsFromDevice(messagesEntity.getIsFromDevice());
        messageDTO.setParentMessageId(messagesEntity.getParentMessageId());
        messageDTO.setCreateTime(messagesEntity.getCreatedTime());
        messageDTO.setIsRead(messagesEntity.getIsRead());
        messageDTO.setIsReply(messagesEntity.getIsReply());
        return messageDTO;
    }
}
