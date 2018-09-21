package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.DivisionDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.web.form.DivisionSelectForm;
import jp.co.willwave.aca.web.form.report.DivisionForm;

import java.util.List;

public interface DivisionService {
    List<DivisionSelectForm> getDivisionManaged(Long userLoginId) throws CommonException;

    DivisionDTO findDivisionsRelative(Long userLoginId) throws CommonException;

    Long findIdByDeviceId(Long deviceId);

    Long findOnlyIdByUserId(Long userId);

    void updateDivision(DivisionsEntity divisionsEntity) throws Exception;

    void createDivision(DivisionsEntity divisionsEntity, UsersEntity usersEntity) throws Exception;

    void deleteDivision(Long divisionId) throws CommonException;

    DivisionsEntity findById(Long divisionId) throws CommonException;

    List<DivisionsEntity> searchChildren(Long divisionRootId) throws CommonException;

    List<DivisionsEntity> getListFamilyDivision(Long divisionId) throws CommonException;

    /**
     * get list of division managed by user id
     * TODO method name confusing
     * @param usersId
     * @return
     * @throws CommonException
     */
    List<DivisionForm> getDivisionForm(Long usersId) throws CommonException;

    Long getCompanyIdManagedDevice(Long devicesId);

    DivisionsEntity findByCustomerId(Long customerId);

    DivisionsEntity findCompanyByDivisionId(Long divisionId) throws CommonException;
}
