package jp.co.willwave.aca.service;

import jp.co.willwave.aca.constants.StatusEnum;
import jp.co.willwave.aca.constants.UsageType;
import jp.co.willwave.aca.dao.CompanyUsageStatusDao;
import jp.co.willwave.aca.dao.DivisionsDao;
import jp.co.willwave.aca.dto.api.CompanyUsageDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.CompanyUsageStatusEntity;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.web.form.SearchUsageForm;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static jp.co.willwave.aca.constants.DateConstant.DATE_FORMAT;

@Service
@Transactional(rollbackFor = Exception.class)
public class CompanyUsageServiceImpl implements CompanyUsageService {

    private final CompanyUsageStatusDao companyUsageStatusDao;
    private final DivisionsDao divisionsDao;

    @Autowired
    public CompanyUsageServiceImpl(CompanyUsageStatusDao companyUsageStatusDao, DivisionsDao divisionsDao) {
        this.companyUsageStatusDao = companyUsageStatusDao;
        this.divisionsDao = divisionsDao;
    }

    @Override
    public List<CompanyUsageDTO> searchCompanyUsageList(SearchUsageForm searchUsageForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException {
        List<Long> companyIdList = new ArrayList<>();

        Boolean isRealTime = true;
        Date monthReport = null;
        String searchCompanyName = null;
        if (searchUsageForm.getReportType() != null) {
            if (searchUsageForm.getReportType().equals(UsageType.MONTHLY.toString())) {
                isRealTime = false;
                try {
                    if (searchUsageForm.getReportMonth() != null && org.apache.commons.lang3.StringUtils.isNotBlank(searchUsageForm.getReportMonth().trim())) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                        Date date = dateFormat.parse(searchUsageForm.getReportMonth().trim());
                        date = DateUtils.truncate(date, java.util.Calendar.DAY_OF_MONTH);
                        monthReport = date;
                    } else {
                        monthReport = new Date();
                    }
                } catch (Exception e) {
                    monthReport = new Date();
                }
            }
        }
        if (searchUsageForm.getCompanyName() != null && StringUtils.isNotBlank(searchUsageForm.getCompanyName())) {
            searchCompanyName = searchUsageForm.getCompanyName();
        }
        if (searchUsageForm.getCompanyId() == null) {
            List<DivisionsEntity> divisionsEntityList = divisionsDao.getCompanyList(isRealTime);

            if (CollectionUtils.isEmpty(divisionsEntityList)) {
                return null;
            }

            for (DivisionsEntity d : divisionsEntityList) {
                companyIdList.add(d.getId());
            }

        } else {
            companyIdList.add(searchUsageForm.getCompanyId());
        }

        List<CompanyUsageDTO> companyUsageStatusEntityList = companyUsageStatusDao.getCompanyUsageList(
                            companyIdList, isRealTime, monthReport, searchCompanyName, offset, maxResults, havePaging);

        if (CollectionUtils.isEmpty(companyUsageStatusEntityList)) {
            return null;
        }

        return companyUsageStatusEntityList;

    }

    @Override
    public List<DivisionsEntity> getCompanyList() {
        List<DivisionsEntity> divisionsEntityList = divisionsDao.getCompanyList(false);
        return divisionsEntityList;
    }

    @Override
    public Long countDivisionNumber(Long companyId) {
        Long count = 0L;
        if (companyId == null) {
            return count;
        }
        List<DivisionsEntity> divisionsEntityList = divisionsDao.findDivisionChildListWithStatus(companyId, StatusEnum.ACTIVE.getValue());

        if (CollectionUtils.isEmpty(divisionsEntityList)) {
            return count + 1;
        } else {
            count = (long) (divisionsEntityList.size() + 1);
        }

        return count;
    }

    @Override
    public Long countDeviceNumber(Long companyId) {

        Long count = 0L;
        if (companyId == null) {
            return count;
        }
        List<DivisionsEntity> divisionsEntityList = divisionsDao.findDivisionChildListWithStatus(companyId, StatusEnum.ACTIVE.getValue());

        List<Long> divisionIdList = new ArrayList<>();
        divisionIdList.add(companyId);
        for (DivisionsEntity d : divisionsEntityList
                ) {
            divisionIdList.add(d.getId());
        }

        count = divisionsDao.countDeviceWithStatus(divisionIdList, StatusEnum.ACTIVE.getValue());

        return count;
    }

    @Override
    public Long countUserNumber(Long companyId) {
        Long count = 0L;
        if (companyId == null) {
            return count;
        }
        List<DivisionsEntity> divisionsEntityList = divisionsDao.findDivisionChildListWithStatus(companyId, StatusEnum.ACTIVE.getValue());

        List<Long> divisionIdList = new ArrayList<>();
        divisionIdList.add(companyId);
        for (DivisionsEntity d : divisionsEntityList
                ) {
            divisionIdList.add(d.getId());
        }

        count = divisionsDao.countUserWithStatus(divisionIdList, true, UserRole.OPERATOR.getRole());

        return count;
    }

    @Override
    public void updateCompanyUsage(Long companyId) throws CommonException {
        Long divisionNumber = countDivisionNumber(companyId);
        Long deviceNumber = countDeviceNumber(companyId);
        Long userNumber = countUserNumber(companyId);

        CompanyUsageStatusEntity companyUsageStatusEntity = companyUsageStatusDao.findByCompanyId(companyId, UsageType.REALTIME.toString());
        if (companyUsageStatusEntity == null) {
            companyUsageStatusEntity = new CompanyUsageStatusEntity();
            companyUsageStatusEntity.setCompanyId(companyId);
            companyUsageStatusEntity.setDeviceActiveCount(deviceNumber);
            companyUsageStatusEntity.setDivisionActiveCount(divisionNumber);
            companyUsageStatusEntity.setUserActiveCount(userNumber);
            companyUsageStatusEntity.setMonthReport(new Date());
            companyUsageStatusEntity.setUsageType(UsageType.REALTIME.toString());
            companyUsageStatusDao.insert(companyUsageStatusEntity);
        } else {
            if (divisionNumber > companyUsageStatusEntity.getDivisionActiveCount()) {
                companyUsageStatusEntity.setDivisionActiveCount(divisionNumber);
            }
            if (deviceNumber > companyUsageStatusEntity.getDeviceActiveCount()) {
                companyUsageStatusEntity.setDeviceActiveCount(deviceNumber);
            }
            if (userNumber > companyUsageStatusEntity.getUserActiveCount()) {
                companyUsageStatusEntity.setUserActiveCount(userNumber);
            }
            companyUsageStatusDao.update(companyUsageStatusEntity);
        }

    }

    @Override
    public void updateUsageForMonth() throws CommonException {

        List<CompanyUsageStatusEntity> companyUsageStatusEntityList = companyUsageStatusDao.getAllByType(UsageType.REALTIME.toString());
        if (!CollectionUtils.isEmpty(companyUsageStatusEntityList)) {
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.MONTH, -1);

            List<Long> companyList = new ArrayList<>();

            for (CompanyUsageStatusEntity companyUsageStatusEntity : companyUsageStatusEntityList
                    ) {
                companyUsageStatusEntity.setMonthReport(c.getTime());
                companyUsageStatusEntity.setUsageType(UsageType.MONTHLY.toString());
                companyUsageStatusDao.update(companyUsageStatusEntity);
                companyList.add(companyUsageStatusEntity.getCompanyId());
            }

            List<DivisionsEntity> divisionsEntityList = divisionsDao.getCompanyList(true);

            if (!CollectionUtils.isEmpty(divisionsEntityList)) {
                for (DivisionsEntity d : divisionsEntityList
                        ) {
                    companyList.add(d.getId());
                }
            }

            for (Long companyId : companyList
                    ) {
                updateCompanyUsage(companyId);
            }

        }

    }

}
