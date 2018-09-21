package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.dto.api.CompanyUsageDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.web.form.SearchCompanyForm;

import java.text.ParseException;
import java.util.List;

public interface DivisionsDao extends BaseDao<DivisionsEntity> {
    List<DivisionsEntity> findDivisionByUserId(Long userLoginId) throws CommonException;

    List<DivisionsEntity> findDivisionByCompaniesId(Long companiesId) throws CommonException;

    List<DivisionsEntity> getDivisionList(Long divisionId) throws CommonException;

    List<DivisionsEntity> findDivisionFatherListByDivisionId(Long divisionId);

    List<DivisionsEntity> findDivisionChildListByDivisionId(Long divisionId);

    DivisionsEntity findManagedDivisionByUserID(Long userId);

    DivisionsEntity findBelongedDivisionByUserID(Long userId);

    List<DivisionsEntity> searchParents(Long divisionId) throws CommonException;

    List<Long> getListDivisionIdManaged(Long divisionRootId) throws CommonException;

    List<DivisionsEntity> searchChildren(Long divisionId) throws CommonException;

    List<DivisionsEntity> getListFamilyDivision(Long divisionId) throws CommonException;

    String generateDivisionCode(Long parentId, Long currentId) throws CommonException;

    DivisionsEntity findByNameAndParent(Long parentDivisionsId, String divisionName);

    List<DivisionsEntity> getListDivisionRelative(Long userId) throws CommonException;

    List<DivisionsEntity> findCompanyList(SearchCompanyForm searchCompanyForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException;

    List<DivisionsEntity> getDivisionParentAndCurrent(List<Long> divisionUses);

    DivisionsEntity findDivisionManagedDevices(Long devicesId);

    List<DivisionsEntity> getCompanyList(Boolean isRealTime);

    Long countDeviceWithStatus(List<Long> divisionIdList, Integer status);

    Long countUserWithStatus(List<Long> divisionIdList, Boolean status, Integer roleId);

    List<DivisionsEntity> findDivisionChildListWithStatus(Long divisionId, Integer status);

    DivisionsEntity findDivisionByCustomerId(Long customerId);
}
