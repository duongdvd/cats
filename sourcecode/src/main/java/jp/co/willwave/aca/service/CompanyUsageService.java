package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.CompanyUsageDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.web.form.SearchUsageForm;

import java.text.ParseException;
import java.util.List;

public interface CompanyUsageService {

    List<CompanyUsageDTO> searchCompanyUsageList(SearchUsageForm searchUsageForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException;

    List<DivisionsEntity> getCompanyList();

    Long countDivisionNumber(Long companyId);

    Long countDeviceNumber(Long companyId);

    Long countUserNumber(Long companyId);

    void updateCompanyUsage(Long companyId) throws CommonException;

    void updateUsageForMonth() throws CommonException;
}
