package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.constants.UsageType;
import jp.co.willwave.aca.dto.api.CompanyUsageDTO;
import jp.co.willwave.aca.model.entity.CompanyUsageStatusEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class CompanyUsageStatusDaoImpl extends BaseDaoImpl<CompanyUsageStatusEntity> implements CompanyUsageStatusDao {
    public CompanyUsageStatusDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<CompanyUsageDTO> getCompanyUsageList(List<Long> companyIdList, Boolean isRealTime, Date monthReport,
                                                     String searchCompanyName, Integer offset, Integer maxResults, Boolean havePaging) {

        String usageType = UsageType.MONTHLY.toString();

        if (isRealTime) {
            usageType = UsageType.REALTIME.toString();
        }

        if (CollectionUtils.isEmpty(companyIdList)) {
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT c.companyId, d.divisionName, c.divisionActiveCount, c.userActiveCount, c.deviceActiveCount, c.monthReport FROM CompanyUsageStatusEntity c , DivisionsEntity d " +
                "WHERE c.companyId in :companyIdList AND c.companyId = d.id AND c.usageType = :usageType ");

        if (!isRealTime) {
            sqlBuilder.append("\n AND DATE_FORMAT(c.monthReport,'%Y%m') = DATE_FORMAT(:monthReport,'%Y%m') ");
        }

        if (searchCompanyName != null && StringUtils.isNotBlank(searchCompanyName.trim())) {
            sqlBuilder.append("\n AND d.divisionName LIKE :searchCompanyName");
        }
        sqlBuilder.append("\nORDER BY c.companyId ASC");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sqlBuilder.toString()).setParameter("companyIdList", companyIdList).setParameter("usageType", usageType);
        if (!isRealTime) {
            query.setParameter("monthReport", monthReport);
        }
        if (searchCompanyName != null && StringUtils.isNotBlank(searchCompanyName.trim())) {
            query.setParameter("searchCompanyName", "%" + searchCompanyName + "%");
        }

        if (havePaging) {
            query.setFirstResult(offset != null ? offset : 0);
            query.setMaxResults(maxResults != null ? maxResults : 10);
        }


        List<Object[]> companyUsageStatusEntityList = query.getResultList();

        if (CollectionUtils.isEmpty(companyUsageStatusEntityList)) {
            return null;
        }

        List<CompanyUsageDTO> companyUsageDTOList = new ArrayList<>();
        for (Object[] objects : companyUsageStatusEntityList
                ) {
            CompanyUsageDTO companyUsageDTO = new CompanyUsageDTO();
            if (objects[0] != null) {
                companyUsageDTO.setCompanyId(Long.valueOf(objects[0].toString()));
            }
            if (objects[1] != null) {
                companyUsageDTO.setCompanyName(objects[1].toString());
            }
            if (objects[2] != null) {
                companyUsageDTO.setDivisionActiveCount(Long.valueOf(objects[2].toString()));
            }
            if (objects[3] != null) {
                companyUsageDTO.setUserActiveCount(Long.valueOf(objects[3].toString()));
            }
            if (objects[4] != null) {
                companyUsageDTO.setDeviceActiveCount(Long.valueOf(objects[4].toString()));
            }

            if (!isRealTime) {
                if (objects[5] != null) {
                    companyUsageDTO.setMonthReport((Date) objects[5]);
                }
            }

            companyUsageDTOList.add(companyUsageDTO);

        }

        return companyUsageDTOList;
    }

    @Override
    public CompanyUsageStatusEntity findByCompanyId(Long companyId, String usageType) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT c FROM CompanyUsageStatusEntity c " +
                "WHERE c.companyId = :companyId AND c.usageType = :usageType ");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sqlBuilder.toString()).setParameter("companyId", companyId).setParameter("usageType", usageType);

        List<CompanyUsageStatusEntity> companyUsageStatusEntityList = query.getResultList();
        if (CollectionUtils.isEmpty(companyUsageStatusEntityList)) {
            return null;
        }
        return companyUsageStatusEntityList.get(0);
    }

    @Override
    public List<CompanyUsageStatusEntity> getAllByType(String usageType) {

        if(usageType == null || StringUtils.isBlank(usageType)) {
            usageType = UsageType.REALTIME.toString();
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT c FROM CompanyUsageStatusEntity c " +
                "WHERE c.usageType = :usageType ");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sqlBuilder.toString()).setParameter("usageType", usageType);

        return query.getResultList();
    }

}
