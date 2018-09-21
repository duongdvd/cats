package jp.co.willwave.aca.service;

import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.constants.BusinessConstants;
import jp.co.willwave.aca.dao.DivisionsDao;
import jp.co.willwave.aca.dao.UsersDao;
import jp.co.willwave.aca.dto.api.DivisionDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.exception.LogicWebException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.PasswordUtils;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.DivisionSelectForm;
import jp.co.willwave.aca.web.form.report.DivisionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class DivisionServiceImpl extends BaseService implements DivisionService {

    private final DivisionsDao divisionsDao;
    private final UsersDao usersDao;

    @Autowired
    public DivisionServiceImpl(DivisionsDao divisionsDao, UsersDao usersDao) {
        this.divisionsDao = divisionsDao;
        this.usersDao = usersDao;
    }

    @Override
    public List<DivisionSelectForm> getDivisionManaged(Long userLoginId) throws CommonException {
        DivisionsEntity divisionsEntity
                = divisionsDao.find(Constant.ColumnName.USERS_ID, String.valueOf(userLoginId), DivisionsEntity.class);
        List<DivisionsEntity> divisionsEntities;
        if (divisionsEntity == null) {
            return new ArrayList<>();
        } else {
            divisionsEntities = divisionsDao.findDivisionByUserId(userLoginId);
        }
        List<DivisionSelectForm> divisionForms = new ArrayList<>();
        if (!CollectionUtils.isEmpty(divisionsEntities)) {
            divisionsEntities.forEach(x -> {
                DivisionSelectForm divisionForm = new DivisionSelectForm();
                divisionForm.setId(x.getId());
                divisionForm.setName(x.getDivisionName());
                divisionForms.add(divisionForm);
            });
        }
        return divisionForms;
    }

    @Override
    public DivisionDTO findDivisionsRelative(Long userLoginId) throws CommonException {
        DivisionDTO dto = new DivisionDTO();
        DivisionsEntity division = divisionsDao.findManagedDivisionByUserID(userLoginId);
        if (division != null) {
            dto.setCurrentDivision(division);
            dto.setDivisionId(division.getId());
            dto.setDivisionName(division.getDivisionName());
            dto.getChildrenList().addAll(divisionsDao.searchChildren(division.getId()));
            dto.getParentList().addAll(divisionsDao.searchParents(division.getId()));
        }

        return dto;
    }

    @Override
    public Long findIdByDeviceId(Long deviceId) {
        return null;
    }

    @Override
    public Long findOnlyIdByUserId(Long userId) {
        DivisionsEntity divisionId = divisionsDao.findManagedDivisionByUserID(userId);
        Long id = 0L;
        if (divisionId != null) {
            id = divisionId.getId();
        }
        return id;
    }

    @Override
    public void updateDivision(DivisionsEntity divisionsEntity) throws Exception {
        Date sysDate = new Date();
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        List<Long> divisionManagedByUserLogin = divisionsDao.getListDivisionIdManaged(userInfo.getDivisionsId());
        if (CollectionUtils.isEmpty(divisionManagedByUserLogin)
                || !divisionManagedByUserLogin.contains(divisionsEntity.getId())) {
            throw new LogicException(messageSource.getMessage(Constant.ErrorCode.NOT_PERMISSION_MANAGED_DIVISION,
                    new String[]{userInfo.getLoginId()}));
        }
        //Check user is managing current division?, user is managing other division?
        DivisionsEntity oldDivision = divisionsDao.findById(divisionsEntity.getId(), DivisionsEntity.class);
        if (oldDivision == null
                || oldDivision.getDeleteFlg()) {
            throw new LogicException(messageSource.getMessage(Constant.ErrorCode.NOT_EXISTS,
                    new String[]{divisionsEntity.getId().toString()}));
        }
        if (!oldDivision.getUsersId().equals(divisionsEntity.getUsersId())) {
            UsersEntity newUser = usersDao.findById(divisionsEntity.getUsersId(), UsersEntity.class);
            if (!UserRole.OPERATOR.getRole().equals(newUser.getRoleId())) {
                throw new LogicException(messageSource.getMessage(Constant.ErrorCode.USER_MANAGED_DIVISION,
                        new String[]{divisionsEntity.getUsersId().toString()}));
            }
            UsersEntity oldUser = usersDao.findById(oldDivision.getUsersId(), UsersEntity.class);
            oldUser.setRoleId(UserRole.OPERATOR.getRole());
            newUser.setRoleId(UserRole.DIVISION_DIRECTOR.getRole());
            usersDao.update(oldUser);
            usersDao.update(newUser);
        }


        //Check division same name with division other of parent
        DivisionsEntity checkDivisionName = divisionsDao.findByNameAndParent(oldDivision.getParentDivisionsId(),
                StringUtils.trimWhitespace(divisionsEntity.getDivisionName()));
        if (checkDivisionName != null
                && !checkDivisionName.getId().equals(oldDivision.getId())) {
            throw new LogicException(messageSource.getMessage(Constant.ErrorCode.DIVISION_NAME,
                    new String[]{divisionsEntity.getDivisionName()}));
        }
        oldDivision.setUpdateDate(sysDate);
        oldDivision.setUpdateBy(userInfo.getId());
        oldDivision.setUsersId(divisionsEntity.getUsersId());
        oldDivision.setDivisionAddress(StringUtils.trimWhitespace(divisionsEntity.getDivisionAddress()));
        oldDivision.setDivisionMail(StringUtils.trimWhitespace(divisionsEntity.getDivisionMail()));
        oldDivision.setDivisionName(StringUtils.trimWhitespace(divisionsEntity.getDivisionName()));
        oldDivision.setStatus(divisionsEntity.getStatus());
        divisionsDao.update(oldDivision);

    }

    @Override
    public void createDivision(DivisionsEntity divisionsEntity, UsersEntity usersEntity) throws Exception {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        Date sysDate = new Date();
        DivisionsEntity checkDivisionName = divisionsDao.findByNameAndParent(divisionsEntity.getParentDivisionsId(),
                StringUtils.trimWhitespace(divisionsEntity.getDivisionName()));
        if (checkDivisionName != null) {
            throw new LogicException(messageSource.getMessage(Constant.ErrorCode.DIVISION_NAME,
                    new String[]{divisionsEntity.getDivisionName()}));
        }
        DivisionsEntity divisionParent = divisionsDao.findById(divisionsEntity.getParentDivisionsId(), DivisionsEntity.class);
        if (divisionParent == null) {
            throw new LogicException(messageSource.getMessage(Constant.ErrorCode.DIVISION_PARENT,
                    new String[]{divisionsEntity.getParentDivisionsId().toString()}));
        }
        if (usersDao.checkExistsLoginId(usersEntity.getLoginId())) {
            throw new LogicException(messageSource.getMessage(Constant.ErrorCode.ALREADY_EXISTS,
                    new String[]{usersEntity.getLoginId()}));
        }
        if (usersDao.checkExistsMail(usersEntity.getUserEmail())) {
            throw new LogicException(messageSource.getMessage(Constant.ErrorCode.ALREADY_EXISTS,
                    new String[]{usersEntity.getUserEmail()}));
        }
        divisionsEntity.setUsersId(null);
        divisionsEntity.setId(null);
        divisionsEntity.setCreateBy(userInfo.getId());
        divisionsEntity.setUpdateBy(userInfo.getId());
        divisionsEntity.setCreateDate(sysDate);
        divisionsEntity.setUpdateDate(sysDate);
        divisionsDao.insert(divisionsEntity);

        usersEntity.setDivisionsId(divisionsEntity.getId());
        usersEntity.setRoleId(UserRole.DIVISION_DIRECTOR.getRole());
        usersEntity.setCreateBy(userInfo.getId());
        usersEntity.setCreateDate(sysDate);
        usersEntity.setUpdateBy(userInfo.getId());
        usersEntity.setCreateBy(userInfo.getId());
        usersEntity.setId(null);
        usersEntity.setPasswd(PasswordUtils.generateSecurePassword(BusinessConstants.PASS_DEFAULT,
                Constant.SHA_512_PASSWORD_KEY));
        usersEntity.setStatus(divisionsEntity.getStatus() == 1);
        usersDao.insert(usersEntity);

        divisionsEntity.setUsersId(usersEntity.getId());
        divisionsEntity.setDivisionCode(divisionParent.getDivisionCode() + "_" + divisionsEntity.getId());
        divisionsDao.update(divisionsEntity);


    }

    @Override
    public void deleteDivision(Long divisionId) throws CommonException {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        if (userInfo.getDivisionsId().equals(divisionId)) {
            throw new LogicWebException(messageSource.get(Constant.ErrorCode.EXISTS_CONSTRAIN_DIVISION));
        }
        List<Long> divisionManagedByUserLogin = divisionsDao.getListDivisionIdManaged(userInfo.getDivisionsId());
        if (CollectionUtils.isEmpty(divisionManagedByUserLogin)
                || !divisionManagedByUserLogin.contains(divisionId)) {
            throw new LogicWebException(messageSource.getMessage(Constant.ErrorCode.NOT_PERMISSION_MANAGED_DIVISION,
                    new String[]{userInfo.getLoginId()}));
        }

        DivisionsEntity oldDivision = divisionsDao.findById(divisionId, DivisionsEntity.class);
        if (oldDivision == null
                || oldDivision.getDeleteFlg()) {
            return;
        }
        List<DivisionsEntity> childrenDivisions = divisionsDao.searchChildren(divisionId);
        if (!CollectionUtils.isEmpty(childrenDivisions)) {
            throw new LogicWebException(messageSource.get(Constant.ErrorCode.EXISTS_CONSTRAIN_DIVISION));
        }
        List<UsersEntity> usersEntities = usersDao.findByDivision(divisionId);
        if (!CollectionUtils.isEmpty(usersEntities) && usersEntities.size() > 1) {
            throw new LogicWebException(messageSource.get(Constant.ErrorCode.EXISTS_CONSTRAIN_DIVISION));
        }
        divisionsDao.delete(oldDivision);
        //Delete division admin
        usersDao.delete(usersEntities.get(0));

    }

    @Override
    public DivisionsEntity findById(Long divisionId) throws CommonException {
        return divisionsDao.findById(divisionId, DivisionsEntity.class);
    }

    @Override
    public List<DivisionsEntity> searchChildren(Long divisionRootId) throws CommonException {
        return divisionsDao.getListFamilyDivision(divisionRootId);
    }

    @Override
    public List<DivisionsEntity> getListFamilyDivision(Long divisionId) throws CommonException {
        return divisionsDao.getListFamilyDivision(divisionId);
    }

    @Override
    public List<DivisionForm> getDivisionForm(Long usersId) throws CommonException {
        DivisionsEntity divisionRoot = divisionsDao.findManagedDivisionByUserID(usersId);
        if (divisionRoot == null) {
            return new ArrayList<>();
        }
        List<DivisionsEntity> divisionsEntities = divisionsDao.getListFamilyDivision(divisionRoot.getId());
        return ConversionUtil.mapperAsList(divisionsEntities, DivisionForm.class);
    }

    @Override
    public Long getCompanyIdManagedDevice(Long devicesId) {
        DivisionsEntity currentDivision = divisionsDao.findDivisionManagedDevices(devicesId);
        if (currentDivision == null) {
            return null;
        }
        String divisionCode = currentDivision.getDivisionCode();
        String[] divisionIdFamily = divisionCode.split("_");
        return Long.valueOf(divisionIdFamily[1]);
    }

    @Override
    public DivisionsEntity findByCustomerId(Long customerId) {
        return divisionsDao.findDivisionByCustomerId(customerId);
    }

    @Override
    public DivisionsEntity findCompanyByDivisionId(Long divisionId) throws CommonException {
        DivisionsEntity division = divisionsDao.findById(divisionId, DivisionsEntity.class);
        if (division == null || StringUtils.isEmpty(division.getDivisionCode())) return null;
        String companyId = division.getDivisionCode().split("_")[1];
        try {
            return divisionsDao.findById(Long.valueOf(companyId), DivisionsEntity.class);
        } catch (Exception e) {
            logger.error("findCompanyByDivisionId", e);
            return null;
        }
    }
}
