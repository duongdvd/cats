package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.dto.api.CompanyUsageDTO;
import jp.co.willwave.aca.model.entity.CompanyUsageStatusEntity;

import java.util.Date;
import java.util.List;

public interface CompanyUsageStatusDao extends BaseDao<CompanyUsageStatusEntity> {

    List<CompanyUsageDTO> getCompanyUsageList(List<Long> companyIdList, Boolean isRealTime, Date monthReport,
                                          String searchCompanyName, Integer offset, Integer maxResults, Boolean havePaging);

    CompanyUsageStatusEntity findByCompanyId(Long companyId, String usageType);

    List<CompanyUsageStatusEntity> getAllByType(String usageType);
}
