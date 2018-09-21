package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.constants.CarStatus;
import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.constants.StatusEnum;
import jp.co.willwave.aca.dto.api.AssignDeviceDTO;
import jp.co.willwave.aca.dto.api.DeviceDTO;
import jp.co.willwave.aca.dto.api.EditDeviceDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.entity.CarsEntity;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.DeviceExportForm;
import jp.co.willwave.aca.web.form.SearchAssignDeviceForm;
import jp.co.willwave.aca.web.form.SearchDeviceForm;
import jp.co.willwave.aca.web.form.route.DeviceCarDTO;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Repository
public class DevicesDaoImpl extends BaseDaoImpl<DevicesEntity> implements DevicesDao {
    private Logger logger = Logger.getLogger(DevicesDaoImpl.class);

    public DevicesDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public DevicesEntity findByLoginId(String loginId) throws CommonException {
        logger.info("DevicesDao.findByLoginId");
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<DevicesEntity> criteria = builder.createQuery(DevicesEntity.class);
        Root<DevicesEntity> root = criteria.from(DevicesEntity.class);
        criteria.select(root).where(builder.equal(root.get("loginId"), loginId));
        List<DevicesEntity> devicesEntities = sessionFactory.getCurrentSession().createQuery(criteria)
                .getResultList();
        if (CollectionUtils.isEmpty(devicesEntities)) {
            return null;
        }
        return devicesEntities.get(0);
    }

    @Override
    public DevicesEntity findByToken(String token) {
        logger.info("DevicesDao.findByToken");
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<DevicesEntity> criteriaQuery = criteriaBuilder.createQuery(DevicesEntity.class);
        Root<DevicesEntity> root = criteriaQuery.from(DevicesEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("loginToken"), token));
        List<DevicesEntity> devicesEntities = sessionFactory.getCurrentSession().createQuery(criteriaQuery)
                .getResultList();
        if (CollectionUtils.isEmpty(devicesEntities)) {
            return null;
        }
        return devicesEntities.get(0);
    }

    @Override
    public List<DeviceDTO> findByDisivions(List<Long> divisionIdList, Integer offset, Integer maxResults, Boolean noPaging, Boolean isAssignNewDevice) {
        logger.info("DevicesDao.findByDivisionList");

        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        StringBuilder sb = new StringBuilder();
        sb.append("Select de.id, de.loginId ,d.id , d.divisionName , c.driverName, c.plateNumber,  de.status, de.deviceType \n" +
                "FROM DivisionsEntity d, DivisionsHasDevicesEntity di ,DevicesEntity de , CarsEntity c\n" +
                "WHERE\n" +
                "d.id in :divisionIdList\n" +
                "AND\n" +
                "d.id = di.divisionsId\n" +
                "AND\n" +
                "di.devicesId = de.id\n" +
                "AND\n" +
                "de.id = c.devicesId\n" +
                "AND\n" +
                "d.deleteFlg = 0\n" +
                "AND\n" +
                "di.deleteFlg = 0\n" +
                "AND\n" +
                "de.deleteFlg = 0\n" +
                "AND\n" +
                "c.deleteFlg = 0\n" +
                "AND\n" +
                "c.latestFlg = 1\n");


        if (isAssignNewDevice) {
            sb.append("AND de.id\n" +
                    "NOT IN\n" +
                    "(\n" +
                    "SELECT device.id FROM DevicesEntity device , UsersManageDevicesEntity usermanage\n" +
                    "WHERE\n" +
                    "usermanage.devicesId = device.id\n" +
                    "AND usermanage.usersId = :usersId\n" +
                    "AND usermanage.deleteFlg = 0\n" +
                    "AND device.deleteFlg = 0\n" +
                    ")");
        }

        sb.append("ORDER BY de.id ASC\n");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sb.toString()).setParameter("divisionIdList", divisionIdList);
        if (isAssignNewDevice) {
            query.setParameter("usersId", userLoginId);
        }

        if (!noPaging) {
            query.setFirstResult(offset != null ? offset : 0);
            query.setMaxResults(maxResults != null ? maxResults : 10);
        }

        List<Object[]> devicesEntities = query.getResultList();

        List<DeviceDTO> deviceDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(devicesEntities)) {
            for (Object[] object : devicesEntities
                    ) {
                DeviceDTO deviceDTO = new DeviceDTO();
                if (object[0] != null) {
                    deviceDTO.setDeviceId(Long.valueOf(object[0].toString()));
                }
                if (object[1] != null) {
                    deviceDTO.setDeviceLoginId(String.valueOf(object[1].toString()));
                }
                if (object[2] != null) {
                    deviceDTO.setDivisionId(Long.valueOf(object[2].toString()));
                }
                if (object[3] != null) {
                    deviceDTO.setDivisionName(String.valueOf(object[3].toString()));
                }
                if (object[4] != null) {
                    deviceDTO.setDriverName(String.valueOf(object[4].toString()));
                }
                if (object[5] != null) {
                    deviceDTO.setPlateNumber(String.valueOf(object[5].toString()));
                }
                if (object[6] != null) {
                    deviceDTO.setStatus(Integer.valueOf(object[6].toString()));
                }
                deviceDTO.setDeviceType(object[7].toString());
                deviceDTOList.add(deviceDTO);
            }
        }


        return deviceDTOList;
    }

    @Override
    public List<DeviceExportForm> exportByDisivions(List<Long> divisionIdList) {
        logger.info("DevicesDao.findByDivisionList");
        StringBuilder sb = new StringBuilder();
        sb.append("Select de.loginId, de.status, de.iconPath, c.carMaker, c.carType, c.driverName, c.plateNumber \n" +
                "FROM DivisionsEntity d, DivisionsHasDevicesEntity di ,DevicesEntity de , CarsEntity c\n" +
                "WHERE\n" +
                "d.id in :divisionIdList\n" +
                "AND\n" +
                "d.id = di.divisionsId\n" +
                "AND\n" +
                "di.devicesId = de.id\n" +
                "AND\n" +
                "de.id = c.devicesId\n" +
                "AND\n" +
                "d.deleteFlg = 0\n" +
                "AND\n" +
                "di.deleteFlg = 0\n" +
                "AND\n" +
                "de.deleteFlg = 0\n" +
                "AND\n" +
                "c.deleteFlg = 0\n" +
                "AND\n" +
                "c.latestFlg = 1\n");
        sb.append("ORDER BY de.id ASC\n");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sb.toString()).setParameter("divisionIdList", divisionIdList);

        List<Object[]> devicesEntities = query.getResultList();
        List<DeviceExportForm> deviceExportFormList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(devicesEntities)) {
            for (Object[] object : devicesEntities
                    ) {
                DeviceExportForm deviceExportForm = new DeviceExportForm();
                if (object[0] != null) {
                    deviceExportForm.setLoginId(object[0].toString());
                }
                if (object[1] != null) {
                    deviceExportForm.setStatus(Integer.valueOf(object[1].toString()));
                }
                if (object[2] != null) {
                    deviceExportForm.setIconPath(object[2].toString());
                }
                if (object[3] != null) {
                    deviceExportForm.setCarMaker(object[3].toString());
                }
                if (object[4] != null) {
                    deviceExportForm.setCarType(object[4].toString());
                }
                if (object[5] != null) {
                    deviceExportForm.setDriverName(object[5].toString());
                }
                if (object[6] != null) {
                    deviceExportForm.setPlateNumber(object[6].toString());
                }
                deviceExportFormList.add(deviceExportForm);
            }
            return deviceExportFormList;
        }
        return null;
    }

    @Override
    public List<DeviceExportForm> exportByMultiCondition(SearchDeviceForm searchDeviceForm, List<Long> divisionIdList) {
        logger.info("DevicesDao.exportByMultiCondition");
        Long deviceId = null;
        if (searchDeviceForm.getDeviceId() != null) {
            deviceId = searchDeviceForm.getDeviceId();
        }
        String deviceLoginId = null;
        if(searchDeviceForm.getDeviceLoginId() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchDeviceForm.getDeviceLoginId())) {
                deviceLoginId = searchDeviceForm.getDeviceLoginId();
            }
        }

        List<Long> divisionId = new ArrayList<>();
        if (searchDeviceForm.getDivisionId() != null) {
            divisionId.add(searchDeviceForm.getDivisionId());
        } else {
            divisionId.addAll(divisionIdList);
        }
        String driverName = null;
        if(searchDeviceForm.getDriverName() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchDeviceForm.getDriverName())) {
                driverName = searchDeviceForm.getDriverName();
            }
        }
        String plateNumber = null;
        if(searchDeviceForm.getPlateNumber() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchDeviceForm.getPlateNumber())) {
                plateNumber = searchDeviceForm.getPlateNumber();
            }
        }
        Integer status = null;
        if (searchDeviceForm.getStatus() != null) {
            status = searchDeviceForm.getStatus();
        }


        StringBuilder sb = new StringBuilder();
        sb.append("Select de.loginId, de.status, de.iconPath, c.carMaker, c.carType, c.driverName, c.plateNumber \n" +
                "FROM DivisionsEntity d, DivisionsHasDevicesEntity di, DevicesEntity de, CarsEntity c\n");
        sb.append("WHERE\n" +
                "d.id = di.divisionsId\n" +
                "AND\n" +
                "di.devicesId = de.id\n" +
                "AND\n" +
                "de.id = c.devicesId\n" +
                "AND\n" +
                "d.deleteFlg = 0\n" +
                "AND\n" +
                "di.deleteFlg = 0\n" +
                "AND\n" +
                "de.deleteFlg = 0\n" +
                "AND\n" +
                "c.deleteFlg = 0\n" +
                "AND\n" +
                "c.latestFlg = 1\n");


        if (deviceId != null) {
            sb.append("AND\n" +
                    "de.id = :deviceId\n");
        }

        if (deviceLoginId != null) {
            sb.append("AND\n" +
                    "de.loginId LIKE  :deviceLoginId\n");
        }

        if (driverName != null) {
            sb.append("AND\n" +
                    "c.driverName LIKE  :driverName\n");
        }

        if (plateNumber != null) {
            sb.append("AND\n" +
                    "c.plateNumber LIKE  :plateNumber\n");
        }

        if (status != null) {
            sb.append("AND\n" +
                    "de.status =  :status\n");
        }


        Query query;
        sb.append("AND\n" +
                "d.id in :divisionId \n");
        sb.append("ORDER BY de.id ASC\n");
        query = sessionFactory.getCurrentSession().createQuery(sb.toString()).setParameter("divisionId", divisionId);

        if (deviceId != null) {
            query.setParameter("deviceId", deviceId);
        }

        if (deviceLoginId != null) {
            query.setParameter("deviceLoginId", "%" + deviceLoginId + "%");
        }

        if (driverName != null) {
            query.setParameter("driverName", "%" + driverName + "%");
        }

        if (plateNumber != null) {
            query.setParameter("plateNumber", "%" + plateNumber + "%");
        }

        if (status != null) {
            query.setParameter("status", status);
        }

        List<Object[]> devicesEntities = query.getResultList();
        List<DeviceExportForm> deviceExportFormList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(devicesEntities)) {
            for (Object[] object : devicesEntities
                    ) {
                DeviceExportForm deviceExportForm = new DeviceExportForm();
                if (object[0] != null) {
                    deviceExportForm.setLoginId(object[0].toString());
                }
                if (object[1] != null) {
                    deviceExportForm.setStatus(Integer.valueOf(object[1].toString()));
                }
                if (object[2] != null) {
                    deviceExportForm.setIconPath(object[2].toString());
                }
                if (object[3] != null) {
                    deviceExportForm.setCarMaker(object[3].toString());
                }
                if (object[4] != null) {
                    deviceExportForm.setCarType(object[4].toString());
                }
                if (object[5] != null) {
                    deviceExportForm.setDriverName(object[5].toString());
                }
                if (object[6] != null) {
                    deviceExportForm.setPlateNumber(object[6].toString());
                }
                deviceExportFormList.add(deviceExportForm);
            }
            return deviceExportFormList;
        }

        return null;
    }

    @Override
    public List<DeviceDTO> findByMultiCondition(SearchDeviceForm searchDeviceForm, Integer offset, Integer maxResults, Boolean noPaging, List<Long> divisionTotalList, Boolean isAssignNewDevice) {
        logger.info("DevicesDao.findByMultiCondition");
        Long deviceId = null;

        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();

        if (searchDeviceForm.getDeviceId() != null) {
            deviceId = searchDeviceForm.getDeviceId();
        }
        String deviceLoginId = null;
        if (searchDeviceForm.getDeviceLoginId() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchDeviceForm.getDeviceLoginId())) {
                deviceLoginId = searchDeviceForm.getDeviceLoginId();
            }
        }

        List<Long> divisionId = new ArrayList<>();
        if (searchDeviceForm.getDivisionId() != null) {
            divisionId.add(searchDeviceForm.getDivisionId());
        } else {
            divisionId.addAll(divisionTotalList);
        }
        String driverName = null;
        if (searchDeviceForm.getDriverName() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchDeviceForm.getDriverName())) {
                driverName = searchDeviceForm.getDriverName();
            }
        }
        String plateNumber = null;
        if (searchDeviceForm.getPlateNumber() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchDeviceForm.getPlateNumber())) {
                plateNumber = searchDeviceForm.getPlateNumber();
            }
        }
        Integer status = null;
        if (searchDeviceForm.getStatus() != null) {
            status = searchDeviceForm.getStatus();
        }


        StringBuilder sb = new StringBuilder();
        sb.append("Select de.id, de.loginId ,d.id , d.divisionName , c.driverName, c.plateNumber,  de.status, de.deviceType \n" +
                "FROM DivisionsEntity d, DivisionsHasDevicesEntity dhd, DevicesEntity de, CarsEntity c\n");
        sb.append("WHERE\n" +
                "dhd.devicesId = de.id\n" +
                "AND\n" +
                "de.id = c.devicesId\n" +
                "AND\n" +
                "dhd.divisionsId = d.id\n" +
                "AND\n" +
                "d.deleteFlg = 0\n" +
                "AND\n" +
                "dhd.deleteFlg = 0\n" +
                "AND\n" +
                "de.deleteFlg = 0\n" +
                "AND\n" +
                "c.deleteFlg = 0\n" +
                "AND\n" +
                "c.latestFlg = 1\n");


        if (deviceId != null) {
            sb.append("AND\n" +
                    "de.id = :deviceId\n");
        }

        if (deviceLoginId != null) {
            sb.append("AND\n" +
                    "de.loginId LIKE  :deviceLoginId\n");
        }

        if (driverName != null) {
            sb.append("AND\n" +
                    "c.driverName LIKE  :driverName\n");
        }

        if (plateNumber != null) {
            sb.append("AND\n" +
                    "c.plateNumber LIKE  :plateNumber\n");
        }

        if (status != null) {
            sb.append("AND\n" +
                    "de.status =  :status\n");
        }

        if (isAssignNewDevice) {
            sb.append("AND de.id\n" +
                    "NOT IN\n" +
                    "(\n" +
                    "SELECT device.id FROM DevicesEntity device , UsersManageDevicesEntity usermanage\n" +
                    "WHERE\n" +
                    "usermanage.devicesId = device.id\n" +
                    "AND usermanage.usersId = :usersId\n" +
                    "AND usermanage.deleteFlg = 0\n" +
                    "AND device.deleteFlg = 0\n" +
                    ")");
        }

        Query query;
        sb.append("AND\n" +
                "d.id in :divisionId\n");
        sb.append("ORDER BY de.id ASC\n");
        query = sessionFactory.getCurrentSession().createQuery(sb.toString()).setParameter("divisionId", divisionId);

        if (isAssignNewDevice) {
            query.setParameter("usersId", userLoginId);
        }

        if (deviceId != null) {
            query.setParameter("deviceId", deviceId);
        }

        if (deviceLoginId != null) {
            query.setParameter("deviceLoginId", "%" + deviceLoginId + "%");
        }

        if (driverName != null) {
            query.setParameter("driverName", "%" + driverName + "%");
        }

        if (plateNumber != null) {
            query.setParameter("plateNumber", "%" + plateNumber + "%");
        }

        if (status != null) {
            query.setParameter("status", status);
        }

        if (!noPaging) {
            query.setFirstResult(offset != null ? offset : 0);
            query.setMaxResults(maxResults != null ? maxResults : 10);
        }

        List<Object[]> devicesEntities = query.getResultList();

        List<DeviceDTO> deviceDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(devicesEntities)) {
            for (Object[] object : devicesEntities
                    ) {
                DeviceDTO deviceDTO = new DeviceDTO();
                if (object[0] != null) {
                    deviceDTO.setDeviceId(Long.valueOf(object[0].toString()));
                }
                if (object[1] != null) {
                    deviceDTO.setDeviceLoginId(String.valueOf(object[1].toString()));
                }
                if (object[2] != null) {
                    deviceDTO.setDivisionId(Long.valueOf(object[2].toString()));
                }
                if (object[3] != null) {
                    deviceDTO.setDivisionName(String.valueOf(object[3].toString()));
                }
                if (object[4] != null) {
                    deviceDTO.setDriverName(String.valueOf(object[4].toString()));
                }
                if (object[5] != null) {
                    deviceDTO.setPlateNumber(String.valueOf(object[5].toString()));
                }
                if (object[6] != null) {
                    deviceDTO.setStatus(Integer.valueOf(object[6].toString()));
                }
                deviceDTO.setDeviceType(object[7].toString());
                deviceDTOList.add(deviceDTO);
            }
        }
        return deviceDTOList;
    }

    @Override
    public Long findDivisionIdBasedOnDevice(Long deviceId) {
        logger.info("DevicesDao.findDivisionIdBasedOnDevice");
        Query<Long> query = sessionFactory.getCurrentSession()
                .createQuery("Select d.id from DevicesEntity de, DivisionsHasDevicesEntity dhd, DivisionsEntity d \n" +
                        "WHERE\n" +
                        "de.id = :deviceId \n" +
                        "AND \n" +
                        "de.id = dhd.devicesId \n" +
                        "AND\n" +
                        "dhd.divisionsId = d.id\n" +
                        "AND\n" +
                        "d.deleteFlg = 0\n" +
                        "AND\n" +
                        "dhd.deleteFlg = 0\n" +
                        "AND\n" +
                        "de.deleteFlg = 0", Long.class).setParameter("deviceId", deviceId);
        if (query.getResultList() == null || CollectionUtils.isEmpty(query.getResultList())) {
            return null;
        }
        return query.getResultList().get(0);
    }

    @Override
    public EditDeviceDTO findEditDevice(Long deviceId) {
        logger.info("DevicesDao.findEditDevice");
        Query query = sessionFactory.getCurrentSession()
                .createQuery("SELECT de.id, de.loginId, de.status, c.carMaker, c.carType, c.driverName, c.plateNumber, de.deviceType, de.iconPath FROM CarsEntity c , DevicesEntity de\n" +
                        "where\n" +
                        "de.id = :deviceId\n" +
                        "AND\n" +
                        "c.devicesId = de.id \n" +
                        "AND\n" +
                        "c.deleteFlg = 0\n" +
                        "AND\n" +
                        "de.deleteFlg = 0" +
                        "AND\n" +
                        "c.latestFlg = 1").setParameter("deviceId", deviceId);


        List<Object[]> deviceEntiry = query.getResultList();
        if (!CollectionUtils.isEmpty(deviceEntiry)) {
            EditDeviceDTO editDeviceDTO = new EditDeviceDTO();
            Object[] objects = deviceEntiry.get(0);
            editDeviceDTO.setId(Long.valueOf(objects[0].toString()));
            editDeviceDTO.setLoginId(objects[1].toString());
            editDeviceDTO.setStatus(Integer.parseInt(objects[2].toString()));
            if (objects[3] != null) {
                editDeviceDTO.setCarMaker(objects[3].toString());
            }
            if (objects[4] != null) {
                editDeviceDTO.setCarType(objects[4].toString());
            }
            if (objects[5] != null) {
                editDeviceDTO.setDriverName(objects[5].toString());
            }
            if (objects[6] != null) {
                editDeviceDTO.setPlateNumber(objects[6].toString());
            }

            editDeviceDTO.setDeviceType(objects[7].toString());

            if (objects[8] != null) {
                editDeviceDTO.setCurrentImage(objects[8].toString());
            }
            return editDeviceDTO;
        }

        return null;
    }

    @Override
    public List<AssignDeviceDTO> getAssignDeviceUser(Long userId, Integer offset, Integer maxResults, Boolean noPaging) {
        List<AssignDeviceDTO> assignDeviceDTOList = new ArrayList<>();
        Query query = sessionFactory.getCurrentSession()
                .createQuery("SELECT de.id, de.loginId, d.id, d.divisionName, c.driverName, c.plateNumber, de.deviceType \n" +
                        "FROM UsersEntity u, UsersManageDevicesEntity m, DevicesEntity de, CarsEntity c, DivisionsEntity d, DivisionsHasDevicesEntity dd \n" +
                        "where \n" +
                        "u.id = :usersId \n" +
                        "AND u.id = m.usersId \n" +
                        "AND u.roleId = :roleId \n" +
                        "AND m.devicesId = de.id \n" +
                        "AND de.id = c.devicesId\n" +
                        "AND dd.divisionsId = d.id\n" +
                        "AND dd.devicesId = de.id\n" +
                        "AND u.deleteFlg = 0\n" +
                        "AND m.deleteFlg = 0\n" +
                        "AND de.deleteFlg = 0\n" +
                        "AND c.deleteFlg = 0\n" +
                        "AND d.deleteFlg = 0\n" +
                        "AND dd.deleteFlg = 0\n" +
                        "AND c.latestFlg = 1\n" +
                        "ORDER BY de.id ASC").setParameter("usersId", userId).setParameter("roleId", UserRole.OPERATOR.getRole());

        if (!noPaging) {
            query.setFirstResult(offset != null ? offset : 0);
            query.setMaxResults(maxResults != null ? maxResults : 10);
        }

        List<Object[]> assignDeviceList = query.getResultList();

        if (!CollectionUtils.isEmpty(assignDeviceList)) {
            for (Object[] objects : assignDeviceList
                    ) {
                AssignDeviceDTO assignDeviceDTO = new AssignDeviceDTO();
                assignDeviceDTO.setDeviceId(Long.valueOf(objects[0].toString()));
                if (objects[1] != null) {
                    assignDeviceDTO.setDeviceLoginId(objects[1].toString());
                }
                assignDeviceDTO.setDivisionId(Long.valueOf(objects[2].toString()));
                if (objects[3] != null) {
                    assignDeviceDTO.setDivisionName(objects[3].toString());
                }
                if (objects[4] != null) {
                    assignDeviceDTO.setDriverName(objects[4].toString());
                }
                if (objects[5] != null) {
                    assignDeviceDTO.setPlateNumber(objects[5].toString());
                }
                if (objects[6] != null) {
                    assignDeviceDTO.setDeviceType(objects[6].toString());
                }
                assignDeviceDTOList.add(assignDeviceDTO);
            }
        }


        return assignDeviceDTOList;
    }

    @Override
    public List<AssignDeviceDTO> getAssignDeviceUserByMultiCondition(Long userId, SearchAssignDeviceForm searchAssignDeviceForm, Integer offset, Integer maxResults, Boolean noPaging) {

        List<AssignDeviceDTO> assignDeviceDTOList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT de.id, de.loginId, d.id, d.divisionName, c.driverName, c.plateNumber, de.deviceType \n" +
                "FROM UsersEntity u, UsersManageDevicesEntity m, DevicesEntity de, CarsEntity c, DivisionsEntity d, DivisionsHasDevicesEntity dd \n" +
                "where \n" +
                "u.id = :usersId \n" +
                "AND u.id = m.usersId \n" +
                "AND u.roleId = :roleId \n" +
                "AND m.devicesId = de.id \n" +
                "AND de.id = c.devicesId\n" +
                "AND dd.divisionsId = d.id\n" +
                "AND dd.devicesId = de.id\n" +
                "AND u.deleteFlg = 0\n" +
                "AND m.deleteFlg = 0\n" +
                "AND de.deleteFlg = 0\n" +
                "AND c.deleteFlg = 0\n" +
                "AND dd.deleteFlg = 0\n" +
                "AND c.latestFlg = 1\n" +
                "AND d.deleteFlg = 0\n");

        Long deviceId = null;
        if (searchAssignDeviceForm.getDeviceId() != null) {
            sb.append("AND\n" +
                    "de.id = :deviceId\n");
            deviceId = searchAssignDeviceForm.getDeviceId();
        }
        String deviceLoginId = null;
        if (searchAssignDeviceForm.getDeviceLoginId() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchAssignDeviceForm.getDeviceLoginId())) {
                sb.append("AND\n" +
                        "de.loginId LIKE  :deviceLoginId\n");
                deviceLoginId = searchAssignDeviceForm.getDeviceLoginId();
            }
        }
        Long divisionId = null;
        if (searchAssignDeviceForm.getDivisionId() != null) {
            sb.append("AND\n" +
                    "d.id =  :divisionId\n");
            divisionId = searchAssignDeviceForm.getDivisionId();
        }
        String driverName = null;
        if (searchAssignDeviceForm.getDriverName() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchAssignDeviceForm.getDriverName())) {
                sb.append("AND\n" +
                        "c.driverName LIKE  :driverName\n");
                driverName = searchAssignDeviceForm.getDriverName();
            }
        }

        String plateNumber = null;
        if (searchAssignDeviceForm.getPlateNumber() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchAssignDeviceForm.getPlateNumber())) {
                sb.append("AND\n" +
                        "c.plateNumber LIKE  :plateNumber\n");
                plateNumber = searchAssignDeviceForm.getPlateNumber();
            }
        }
        sb.append("ORDER BY de.id ASC\n");
        Query query = sessionFactory.getCurrentSession()
                .createQuery(sb.toString()).setParameter("usersId", userId).setParameter("roleId", UserRole.OPERATOR.getRole());

        if (deviceId != null) {
            query.setParameter("deviceId", deviceId);
        }

        if (deviceLoginId != null) {
            query.setParameter("deviceLoginId", "%" + deviceLoginId + "%");
        }

        if (divisionId != null) {
            query.setParameter("divisionId", divisionId);
        }

        if (driverName != null) {
            query.setParameter("driverName", "%" + driverName + "%");
        }

        if (plateNumber != null) {
            query.setParameter("plateNumber", "%" + plateNumber + "%");
        }

        if (!noPaging) {
            query.setFirstResult(offset != null ? offset : 0);
            query.setMaxResults(maxResults != null ? maxResults : 10);
        }
        List<Object[]> assignDeviceList = query.getResultList();

        if (!CollectionUtils.isEmpty(assignDeviceList)) {
            for (Object[] objects : assignDeviceList
                    ) {
                AssignDeviceDTO assignDeviceDTO = new AssignDeviceDTO();
                assignDeviceDTO.setDeviceId(Long.valueOf(objects[0].toString()));
                if (objects[1] != null) {
                    assignDeviceDTO.setDeviceLoginId(objects[1].toString());
                }
                assignDeviceDTO.setDivisionId(Long.valueOf(objects[2].toString()));
                if (objects[3] != null) {
                    assignDeviceDTO.setDivisionName(objects[3].toString());
                }
                if (objects[4] != null) {
                    assignDeviceDTO.setDriverName(objects[4].toString());
                }
                if (objects[5] != null) {
                    assignDeviceDTO.setPlateNumber(objects[5].toString());
                }
                if (objects[6] != null) {
                    assignDeviceDTO.setDeviceType(objects[6].toString());
                }
                assignDeviceDTOList.add(assignDeviceDTO);
            }
        }

        return assignDeviceDTOList;
    }

    @Override
    public Long countAllValidDevice(List<Long> deviceIdList) {
        Query query = sessionFactory.getCurrentSession()
                .createQuery("SELECT count(1) from DevicesEntity d WHERE d.id in :deviceIdList AND d.deleteFlg = 0", Long.class).setParameter("deviceIdList", deviceIdList);

        List<Long> count = query.getResultList();

        if (CollectionUtils.isEmpty(count)) {
            return 0L;
        }
        return count.get(0);
    }

    @Override
    public List<DevicesEntity> findDevicesActiveTodayAndOnline(Date date) {
        StringBuilder sql = new StringBuilder("FROM DevicesEntity WHERE id IN ( ");
        sql.append("SELECT r.devicesId FROM RoutesEntity r WHERE DATE(r.actualDate) = DATE(:date) ");
        sql.append("AND r.deleteFlg = false ) AND deleteFlg = false AND carStatus = :carStatus ");
        return sessionFactory.getCurrentSession().createQuery(sql.toString())
                .setParameter("date", date)
                .setParameter("carStatus", CarStatus.ONLINE)
                .getResultList();
    }

    @Override
    public List<DevicesEntity> findRunningDevicesAndActive() {
        StringBuilder sql = new StringBuilder("FROM DevicesEntity WHERE id IN ( ");
        sql.append("SELECT r.devicesId FROM RoutesEntity r WHERE r.runningStatus = :runningStatus ");
        sql.append("AND r.deleteFlg = false ) AND deleteFlg = false AND carStatus = :carStatus ");
        return sessionFactory.getCurrentSession().createQuery(sql.toString())
                .setParameter("runningStatus", RunningStatus.RUNNING)
                .setParameter("carStatus", CarStatus.ONLINE)
                .getResultList();
    }

    @Override
    public List<DevicesEntity> findDevicesManaged(Long usersId) {
        StringBuilder sql = new StringBuilder("SELECT d FROM DevicesEntity d, UsersManageDevicesEntity umd ");
        sql.append("WHERE d.id = umd.devicesId ");
        sql.append("AND umd.usersId  = :usersId ");
        sql.append("AND d.deleteFlg = FALSE ");
        sql.append("AND umd.deleteFlg = FALSE ");
        return sessionFactory.getCurrentSession()
                .createQuery(sql.toString())
                .setParameter("usersId", usersId)
                .getResultList();
    }

    @Override
    public List<DeviceCarDTO> findCarsManaged(Long usersId) {
        StringBuilder sql = new StringBuilder("SELECT d, c FROM DevicesEntity d, UsersManageDevicesEntity umd, " +
                "CarsEntity c ");
        sql.append("WHERE d.id = umd.devicesId ");
        sql.append("AND c.devicesId  = d.id ");
        sql.append("AND c.latestFlg  = 1 ");
        sql.append("AND umd.usersId  = :usersId ");
        sql.append("AND d.deleteFlg = FALSE ");
//        sql.append("AND umd.deleteFlg = FALSE ");
        List<Object[]> objects = sessionFactory.getCurrentSession()
                .createQuery(sql.toString())
                .setParameter("usersId", usersId)
                .getResultList();
        List<DeviceCarDTO> deviceCarDTOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(objects)) {
            objects.forEach(o -> {
                DeviceCarDTO deviceCarDTO = new DeviceCarDTO();
                deviceCarDTO.setDeviceId(((DevicesEntity) o[0]).getId());
                deviceCarDTO.setLoginId(((DevicesEntity) o[0]).getLoginId());
                deviceCarDTO.setStatus(StatusEnum.ACTIVE.getValue().equals(((DevicesEntity) o[0]).getStatus()));
                deviceCarDTO.setPlateNumber(((CarsEntity) o[1]).getPlateNumber());
                deviceCarDTOs.add(deviceCarDTO);
            });
        }
        return deviceCarDTOs;
    }

    @Override
    public List<Object[]> getDevicesManageByOperator(Long userId, CarStatus carStatus, RunningStatus runningStatus) {
        String getColumns = "dev, car";
        StringBuilder sql = new StringBuilder("SELECT ")
            .append(getColumns);
        sql.append(" FROM UsersEntity usr ");
        sql.append("    INNER JOIN UsersManageDevicesEntity umd ");
        sql.append("        on usr.id = umd.usersId ");
        sql.append("    INNER JOIN DevicesEntity dev ");
        sql.append("        on umd.devicesId = dev.id ");

        if (carStatus != null) {
            sql.append(" and dev.carStatus = :carStatus ");
        }

        sql.append("    INNER JOIN RoutesEntity rou ");// Route Plan.
        sql.append("        on dev.id = rou.devicesId ");
        sql.append("        and usr.id = rou.createBy ");

        if (runningStatus != null) {
            sql.append("    and rou.runningStatus = :runningStatus ");
        }

        sql.append("    INNER JOIN RoutesEntity roua ");// Route Actual.
        sql.append("        on rou.id = roua.planedRoutesId ");

        sql.append("    INNER JOIN CarsEntity car ");
        sql.append("        on roua.carsId = car.id ");
        sql.append(" WHERE ");
        sql.append("    usr.id = :userId ");
        sql.append("    and usr.roleId = :roleId ");
        sql.append(" GROUP BY dev.id ");

	    Query<Object[]> query = sessionFactory.getCurrentSession()
		    .createQuery(sql.toString(), Object[].class)
		    .setParameter("userId", userId)
		    .setParameter("roleId", UserRole.OPERATOR.getRole());

	    if (carStatus != null) {
		    query = query.setParameter("carStatus", carStatus);
	    }

	    if (runningStatus != null) {
            query = query.setParameter("runningStatus", runningStatus);
        }

        return query.getResultList();
    }

	@Override
	public List<Object[]> getDevicesByDivisions(List<Long> divisionIds, CarStatus carStatus, RunningStatus runningStatus) {
        String getColumns = "dev, car";
        StringBuilder sql = new StringBuilder("SELECT ").append(getColumns);
        sql.append(" FROM DivisionsEntity dvs ");
        sql.append("    INNER JOIN UsersEntity usr ");
        sql.append("        on dvs.id = usr.divisionsId ");
        sql.append("        and usr.roleId = :roleId ");
        sql.append("    INNER JOIN UsersManageDevicesEntity umd ");
        sql.append("        on usr.id = umd.usersId ");
        sql.append("    INNER JOIN DevicesEntity dev ");
        sql.append("        on umd.devicesId = dev.id ");

        if (carStatus != null) {
            sql.append(" and dev.carStatus = :carStatus ");
        }

        sql.append("    INNER JOIN RoutesEntity rou ");// Route plan
        sql.append("        on dev.id = rou.devicesId ");

        if (carStatus != null) {
            sql.append("    and rou.runningStatus = :runningStatus ");
        }

        sql.append("    INNER JOIN RoutesEntity roua ");// Route Actual
        sql.append("        on rou.id = roua.planedRoutesId ");

        sql.append("    INNER JOIN CarsEntity car ");
        sql.append("        on roua.carsId = car.id ");
        sql.append(" WHERE ");
        sql.append("    dvs.id in :divisionIdList ");
        sql.append(" GROUP BY dev.id ");

        Query<Object[]> query = sessionFactory.getCurrentSession()
			.createQuery(sql.toString(), Object[].class)
            .setParameter("divisionIdList", divisionIds)
			.setParameter("roleId", UserRole.OPERATOR.getRole());

		if (carStatus != null) {
			query = query.setParameter("carStatus", carStatus);
		}

        if (runningStatus != null) {
            query = query.setParameter("runningStatus", runningStatus);
        }

        return query.getResultList();
	}

    @Override
    public List<DevicesEntity> findDevicesByLoginIdList(List<String> loginIdList) {

        if(CollectionUtils.isEmpty(loginIdList)) {
            return null;
        }

        Query query = sessionFactory.getCurrentSession()
                .createQuery("SELECT d from DevicesEntity d WHERE d.loginId in :loginIdList").setParameter("loginIdList", loginIdList);

        List<DevicesEntity> devicesEntityList = query.getResultList();

        return devicesEntityList;
    }

    @Override
    public DevicesEntity findByRouteActualId(Long routeId) {
        StringBuilder sql = new StringBuilder("SELECT d FROM DevicesEntity d , RoutesEntity r ");
        sql.append("WHERE d.id = r.devicesId ");
        sql.append("AND r.id = :routeId ");
        List<DevicesEntity> devicesEntities = sessionFactory.getCurrentSession().createQuery(sql.toString())
                .setParameter("routeId", routeId)
                .getResultList();
        return CollectionUtils.isEmpty(devicesEntities) ? null : devicesEntities.get(0);
    }

    @Override
    public Long countCarWithPlateNumber(String plateNumber) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT Count(*) from DevicesEntity d INNER JOIN CarsEntity c ");
        sqlBuilder.append("ON d.id = c.devicesId ");
        sqlBuilder.append("WHERE d.status = 1 AND c.latestFlg = 1 ");
        sqlBuilder.append("AND c.plateNumber =:plateNumber ");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sqlBuilder.toString(), Long.class).setParameter("plateNumber", plateNumber);

        Long result = 0L;
        List<Long> count = query.getResultList();

        if (CollectionUtils.isEmpty(count)) {
            return result;
        }

        return count.get(0);
    }

    }
