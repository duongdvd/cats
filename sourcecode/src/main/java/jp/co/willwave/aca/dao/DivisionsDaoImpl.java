package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.constants.StatusEnum;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.utilities.CatStringUtil;
import jp.co.willwave.aca.web.form.SearchCompanyForm;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static jp.co.willwave.aca.constants.DateConstant.DATE_FORMAT;

@Repository
public class DivisionsDaoImpl extends BaseDaoImpl<DivisionsEntity> implements DivisionsDao {

    private Logger logger = Logger.getLogger(DivisionsDaoImpl.class);

    public DivisionsDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<DivisionsEntity> findDivisionByUserId(Long userLoginId) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder("WITH RECURSIVE divisions2 as ( ");
        sqlBuilder.append(" SELECT * FROM divisions WHERE users_id= :usersId and delete_flg = :deleteFlg ");
        sqlBuilder.append("UNION");
        sqlBuilder.append(" SELECT f.* FROM divisions AS f, divisions2 AS d WHERE f.parent_divisions_id= d.id");
        sqlBuilder.append(" and f.delete_flg = :deleteFlg and d.delete_flg = :deleteFlg");
        sqlBuilder.append(" ) ");
        sqlBuilder.append("SELECT * FROM divisions2 ");
        return sessionFactory.getCurrentSession()
                .createNativeQuery(sqlBuilder.toString(), DivisionsEntity.class)
                .setParameter("usersId", userLoginId)
                .setParameter("deleteFlg", false)
                .getResultList();
    }

    @Override
    public List<DivisionsEntity> findDivisionByCompaniesId(Long companiesId) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder("WITH RECURSIVE divisions2 as ( ");
        sqlBuilder.append(" SELECT * FROM divisions WHERE companies_id= :companiesId and delete_flg = :deleteFlg ");
        sqlBuilder.append("UNION");
        sqlBuilder.append(" SELECT f.* FROM divisions AS f, divisions2 AS d WHERE f.parent_divisions_id = d.id");
        sqlBuilder.append(" and f.delete_flg = :deleteFlg and d.delete_flg = :deleteFlg");
        sqlBuilder.append(" ) ");
        sqlBuilder.append("SELECT * FROM divisfindByDeviceIdions2 ");
        return sessionFactory.getCurrentSession()
                .createNativeQuery(sqlBuilder.toString(), DivisionsEntity.class)
                .setParameter("companiesId", companiesId)
                .setParameter("deleteFlg", false)
                .getResultList();
    }

    @Override
    public List<DivisionsEntity> getDivisionList(Long divisionId) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM divisions WHERE users_id= :divisionId");
        return sessionFactory.getCurrentSession()
                .createNativeQuery(sqlBuilder.toString(), DivisionsEntity.class)
                .setParameter("divisionId", divisionId)
                .getResultList();
    }

    @Override
    public List<DivisionsEntity> findDivisionFatherListByDivisionId(Long divisionId) {
        StringBuilder sqlBuilder = new StringBuilder("WITH RECURSIVE divisions2 as ( ");
        sqlBuilder.append(" SELECT * FROM divisions WHERE id= :divisionId and delete_flg = :deleteFlg ");
        sqlBuilder.append("UNION");
        sqlBuilder.append(" SELECT f.* FROM divisions AS f, divisions2 AS d WHERE f.id= d.parent_divisions_id");
        sqlBuilder.append(" and f.delete_flg = :deleteFlg and d.delete_flg = :deleteFlg");
        sqlBuilder.append(" ) ");
        sqlBuilder.append("SELECT * FROM divisions2 ");
        return sessionFactory.getCurrentSession()
                .createNativeQuery(sqlBuilder.toString(), DivisionsEntity.class)
                .setParameter("divisionId", divisionId)
                .setParameter("deleteFlg", false)
                .getResultList();
    }

    @Override
    public List<DivisionsEntity> findDivisionChildListByDivisionId(Long divisionId) {
        StringBuilder sqlBuilder = new StringBuilder("WITH RECURSIVE divisions2 as ( ");
        sqlBuilder.append(" SELECT * FROM divisions WHERE parent_divisions_id= :divisionId and delete_flg = :deleteFlg ");
        sqlBuilder.append("UNION");
        sqlBuilder.append(" SELECT f.* FROM divisions AS f, divisions2 AS d WHERE f.parent_divisions_id= d.id");
        sqlBuilder.append(" and f.delete_flg = :deleteFlg and d.delete_flg = :deleteFlg");
        sqlBuilder.append(" ) ");
        sqlBuilder.append("SELECT * FROM divisions2 ");
        return sessionFactory.getCurrentSession()
                .createNativeQuery(sqlBuilder.toString(), DivisionsEntity.class)
                .setParameter("divisionId", divisionId)
                .setParameter("deleteFlg", false)
                .getResultList();
    }

    // Get Division that user(userId) is director.
    @Override
    public DivisionsEntity findManagedDivisionByUserID(Long userId) {
        logger.info("DevicesDao.findManagedDivisionByUserID");
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<DivisionsEntity> criteria = builder.createQuery(DivisionsEntity.class);
        Root<DivisionsEntity> root = criteria.from(DivisionsEntity.class);
        criteria.select(root).where(builder.equal(root.get("usersId"), userId));
        List<DivisionsEntity> divisionsEntities = sessionFactory.getCurrentSession().createQuery(criteria)
                .getResultList();
        if (CollectionUtils.isEmpty(divisionsEntities)) {
            return null;
        }
        return divisionsEntities.get(0);
    }

    @Override
    public DivisionsEntity findBelongedDivisionByUserID(Long userId) {
        StringBuilder sql = new StringBuilder("SELECT d FROM DivisionsEntity AS d ");
        sql.append("INNER JOIN UsersEntity AS u ON d.id = u.divisionsId ");
        sql.append("WHERE u.id = :userId ");
        sql.append("AND d.deleteFlg = false ");
        List<DivisionsEntity> result = sessionFactory.getCurrentSession()
                .createQuery(sql.toString(), DivisionsEntity.class)
                .setParameter("userId", userId)
                .getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    @Override
    public List<DivisionsEntity> searchParents(Long divisionId) throws CommonException {
        DivisionsEntity currentDivision = findById(divisionId, DivisionsEntity.class);
        if (currentDivision == null) {
            return new ArrayList<>();
        }
        String[] divisionIdParentsString = currentDivision.getDivisionCode().split("_");
        List<Long> divisionIdParents = new ArrayList<>();
        for (String id : divisionIdParentsString) {
            if (CatStringUtil.isNumeric(id)
                    && !Long.valueOf(id).equals(divisionId)) {
                divisionIdParents.add(Long.valueOf(id));
            }
        }

        if (CollectionUtils.isEmpty(divisionIdParents)) {
            return new ArrayList<>();
        }

        String sql = "FROM DivisionsEntity WHERE id IN :divisionIds AND deleteFlg = false ";
        return sessionFactory.getCurrentSession()
                .createQuery(sql)
                .setParameter("divisionIds", divisionIdParents)
                .getResultList();
    }

    @Override
    public List<Long> getListDivisionIdManaged(Long divisionRootId) throws CommonException {
        List<Long> divisionIds = new ArrayList<>();
        List<DivisionsEntity> divisionsEntities = searchChildren(divisionRootId);
        if (!CollectionUtils.isEmpty(divisionsEntities)) {
            divisionIds = divisionsEntities.stream().map(DivisionsEntity::getId).collect(Collectors.toList());
        }
        divisionIds.add(divisionRootId);
        return divisionIds;
    }

    @Override
    public List<DivisionsEntity> searchChildren(Long divisionId) throws CommonException {
        DivisionsEntity currentDivision = findById(divisionId, DivisionsEntity.class);
        if (currentDivision == null) {
            return new ArrayList<>();
        }
        String sql = "FROM DivisionsEntity WHERE divisionCode LIKE CONCAT(:code, '\\_%') AND deleteFlg = false ";
        return sessionFactory.getCurrentSession()
                .createQuery(sql)
                .setParameter("code", currentDivision.getDivisionCode())
                .getResultList();
    }

    @Override
    public List<DivisionsEntity> getListFamilyDivision(Long divisionId) throws CommonException {
        DivisionsEntity currentDivision = findById(divisionId, DivisionsEntity.class);
        List<DivisionsEntity> familyDivision;
        if (currentDivision == null) {
            return new ArrayList<>();
        }
        String sql = "FROM DivisionsEntity WHERE divisionCode LIKE CONCAT(:code, '\\_%') AND deleteFlg = false ";
        familyDivision = sessionFactory.getCurrentSession()
                .createQuery(sql)
                .setParameter("code", currentDivision.getDivisionCode())
                .getResultList();
        if (CollectionUtils.isEmpty(familyDivision)) {
            familyDivision = new ArrayList<>();
        }
        familyDivision.add(0, currentDivision);
        return familyDivision;
    }

    @Override
    public String generateDivisionCode(Long parentId, Long currentId) throws CommonException {
        if (parentId == null) {
            return "D_" + currentId;
        } else {
            DivisionsEntity divisionsEntity = findById(parentId, DivisionsEntity.class);
            if (divisionsEntity == null) {
                throw new CommonException();
            }
            return divisionsEntity.getDivisionName() + "_" + currentId;
        }
    }

    @Override
    public List<DivisionsEntity> getListDivisionRelative(Long userId) throws CommonException {
        DivisionsEntity currentDivision = findBelongedDivisionByUserID(userId);
        if (currentDivision == null) {
            return new ArrayList<>();
        }
        List<DivisionsEntity> familyDivision = new ArrayList<>();
        List<DivisionsEntity> childrenDivisions = searchChildren(currentDivision.getId());
        if (!CollectionUtils.isEmpty(childrenDivisions)) {
            familyDivision.addAll(childrenDivisions);
        }
        List<DivisionsEntity> parentDivisions = searchParents(currentDivision.getId());
        if (!CollectionUtils.isEmpty(parentDivisions)) {
            familyDivision.addAll(parentDivisions);
        }
        familyDivision.add(currentDivision);
        return familyDivision;
    }

    @Override
    public List<DivisionsEntity> findCompanyList(SearchCompanyForm searchCompanyForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException {

        StringBuilder sb = new StringBuilder();
        sb.append("FROM DivisionsEntity d where (d.parentDivisionsId IS NULL)\n");

        Long divisionId = null;
        String companyName = null;
        String divisionMail = null;
        String description = null;
        Date fromCreateDate = null;
        Date toCreateDate = null;
        Date fromUpdateDate = null;
        Date toUpdateDate = null;
        Integer status = null;

        if (searchCompanyForm != null) {
            if (searchCompanyForm.getId() != null) {
                sb.append("AND d.id = :divisionId\n");
                divisionId = searchCompanyForm.getId();
            }
            if (searchCompanyForm.getDivisionName() != null) {
                if (StringUtils.isNotBlank(searchCompanyForm.getDivisionName().trim())) {
                    sb.append("AND d.divisionName LIKE :companyName\n");
                    companyName = searchCompanyForm.getDivisionName().trim();
                }
            }
            if (searchCompanyForm.getDivisionMail() != null) {
                if (StringUtils.isNotBlank(searchCompanyForm.getDivisionMail())) {
                    sb.append("AND d.divisionMail LIKE :divisionMail\n");
                    divisionMail = searchCompanyForm.getDivisionMail().trim();
                }
            }
            if (searchCompanyForm.getDescription() != null) {
                if (StringUtils.isNotBlank(searchCompanyForm.getDescription())) {
                    sb.append("AND d.description LIKE :description\n");
                    description = searchCompanyForm.getDescription().trim();
                }
            }
            if (searchCompanyForm.getFromCreateDate() != null) {
                if (StringUtils.isNotBlank(searchCompanyForm.getFromCreateDate())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateFormat.parse(searchCompanyForm.getFromCreateDate()));
                    cal.add(Calendar.DAY_OF_YEAR, -1);

                    fromCreateDate = DateUtils.truncate(cal.getTime(), java.util.Calendar.DAY_OF_MONTH);
                    sb.append("AND DATE(d.createDate) > DATE(:fromCreateDate)\n");
                }
            }
            if (searchCompanyForm.getToCreateDate() != null) {
                if (StringUtils.isNotBlank(searchCompanyForm.getToCreateDate())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateFormat.parse(searchCompanyForm.getToCreateDate()));
                    cal.add(Calendar.DAY_OF_YEAR, +1);

                    toCreateDate = DateUtils.truncate(cal.getTime(), java.util.Calendar.DAY_OF_MONTH);
                    sb.append("AND DATE(d.createDate) < DATE(:toCreateDate)\n");
                }
            }

            if (searchCompanyForm.getFromUpdateDate() != null) {
                if (StringUtils.isNotBlank(searchCompanyForm.getFromUpdateDate())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateFormat.parse(searchCompanyForm.getFromUpdateDate()));
                    cal.add(Calendar.DAY_OF_YEAR, -1);

                    fromUpdateDate = DateUtils.truncate(cal.getTime(), java.util.Calendar.DAY_OF_MONTH);
                    sb.append("AND DATE(d.updateDate) > DATE(:fromUpdateDate)\n");
                }
            }
            if (searchCompanyForm.getToUpdateDate() != null) {
                if (StringUtils.isNotBlank(searchCompanyForm.getToUpdateDate())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateFormat.parse(searchCompanyForm.getToUpdateDate()));
                    cal.add(Calendar.DAY_OF_YEAR, +1);


                    toUpdateDate = DateUtils.truncate(cal.getTime(), java.util.Calendar.DAY_OF_MONTH);
                    sb.append("AND DATE(d.updateDate) < DATE(:toUpdateDate)\n");
                }
            }
            if (searchCompanyForm.getStatus() != null) {
                status = searchCompanyForm.getStatus();
                sb.append("AND d.status = :status\n");
            }
        }
        sb.append("ORDER BY d.id ASC\n");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sb.toString());

        if (divisionId != null) {
            query.setParameter("divisionId", divisionId);
        }
        if (companyName != null) {
            query.setParameter("companyName", "%" + companyName + "%");
        }
        if (divisionMail != null) {
            query.setParameter("divisionMail", "%" + divisionMail + "%");
        }
        if (description != null) {
            query.setParameter("description", "%" + description + "%");
        }
        if (fromCreateDate != null) {
            query.setParameter("fromCreateDate", fromCreateDate);
        }
        if (toCreateDate != null) {
            query.setParameter("toCreateDate", toCreateDate);
        }
        if (fromUpdateDate != null) {
            query.setParameter("fromUpdateDate", fromUpdateDate);
        }
        if (toUpdateDate != null) {
            query.setParameter("toUpdateDate", toUpdateDate);
        }
        if (status != null) {
            query.setParameter("status", status);
        }

        if (havePaging) {
            query.setFirstResult(offset != null ? offset : 0);
            query.setMaxResults(maxResults != null ? maxResults : 10);
        }

        List<DivisionsEntity> divisionsEntityList = query.getResultList();


        return divisionsEntityList;
    }

    public List<DivisionsEntity> getDivisionParentAndCurrent(List<Long> divisionUses) {
        StringBuilder sql = new StringBuilder("FROM DivisionsEntity WHERE deleteFlg = false AND id IN :divisionUses ");
        List<DivisionsEntity> divisionsEntities = sessionFactory.getCurrentSession()
                .createQuery(sql.toString())
                .setParameter("divisionUses", divisionUses)
                .getResultList();
        if (CollectionUtils.isEmpty(divisionsEntities)) {
            return new ArrayList<>();
        }
        Set<Long> divisionParentId = new HashSet<>();
        divisionsEntities.forEach(divisionsEntity -> {
            String[] divisionIdParentsString = divisionsEntity.getDivisionCode().split("_");
            for (String id : divisionIdParentsString) {
                if (CatStringUtil.isNumeric(id)) {
                    divisionParentId.add(Long.valueOf(id));
                }
            }
        });
        return sessionFactory.getCurrentSession()
                .createQuery(sql.toString())
                .setParameter("divisionUses", divisionParentId)
                .getResultList();
    }

    @Override
    public DivisionsEntity findByNameAndParent(Long parentDivisionsId, String divisionName) {
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<DivisionsEntity> criteriaQuery = criteriaBuilder.createQuery(DivisionsEntity.class);
        Root<DivisionsEntity> root = criteriaQuery.from(DivisionsEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.and(criteriaBuilder.equal(root.get("parentDivisionsId"),
                parentDivisionsId), criteriaBuilder.equal(root.get("divisionName"), divisionName)));
        List<DivisionsEntity> divisionsEntities = sessionFactory.getCurrentSession().createQuery(criteriaQuery)
                .getResultList();
        if (CollectionUtils.isEmpty(divisionsEntities)) {
            return null;
        }
        return divisionsEntities.get(0);
    }

    @Override
    public DivisionsEntity findDivisionManagedDevices(Long devicesId) {
        StringBuilder sql = new StringBuilder("SELECT d FROM DivisionsEntity d, DivisionsHasDevicesEntity dhd ");
        sql.append("WHERE d.id = dhd.divisionsId ");
        sql.append("AND dhd.devicesId = :devicesId ");
        List<DivisionsEntity> divisionsEntities = sessionFactory.getCurrentSession()
                .createQuery(sql.toString(), DivisionsEntity.class)
                .setParameter("devicesId", devicesId)
                .getResultList();
        if (CollectionUtils.isEmpty(divisionsEntities)) {
            return null;
        }
        return divisionsEntities.get(0);
    }

    @Override
    public List<DivisionsEntity> getCompanyList(Boolean isRealTime) {

        StringBuilder sql = new StringBuilder("SELECT d FROM DivisionsEntity d \n");
        sql.append("WHERE (d.parentDivisionsId IS NULL) \n");
        if (isRealTime) {
            sql.append(" AND d.status = :status\n");
        }
        sql.append("ORDER BY d.id ASC ");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sql.toString());
        if (isRealTime) {
            query.setParameter("status", StatusEnum.ACTIVE.getValue());
        }

        return query.getResultList();
    }

    @Override
    public Long countDeviceWithStatus(List<Long> divisionIdList, Integer status) {
        StringBuilder sqlBuilder = new StringBuilder("Select count(1) FROM DivisionsEntity d, DivisionsHasDevicesEntity di, DevicesEntity de where \n" +
                "d.id in :divisionIdList \n" +
                "and d.id = di.divisionsId \n" +
                "and di.devicesId = de.id \n" +
                "and de.status = :status");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sqlBuilder.toString(), Long.class).setParameter("divisionIdList", divisionIdList).setParameter("status", status);

        Long result = 0L;
        List<Long> count = query.getResultList();

        if (CollectionUtils.isEmpty(count)) {
            return result;
        }

        return count.get(0);
    }

    @Override
    public Long countUserWithStatus(List<Long> divisionIdList, Boolean status, Integer roleId) {
        StringBuilder sqlBuilder = new StringBuilder("Select count(1) FROM UsersEntity u where \n" +
                "u.divisionsId in :divisionIdList \n" +
                "and u.status = :status \n" +
                "and u.roleId = :roleId \n");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sqlBuilder.toString(), Long.class).setParameter("divisionIdList", divisionIdList).setParameter("status", status).setParameter("roleId", roleId);

        Long result = 0L;
        List<Long> count = query.getResultList();

        if (CollectionUtils.isEmpty(count)) {
            return result;
        }

        return count.get(0);
    }

    @Override
    public List<DivisionsEntity> findDivisionChildListWithStatus(Long divisionId, Integer status) {

        StringBuilder sqlBuilder = new StringBuilder("WITH RECURSIVE divisions2 as ( ");
        sqlBuilder.append(" SELECT * FROM divisions WHERE parent_divisions_id= :divisionId and delete_flg = :deleteFlg and status = :status ");
        sqlBuilder.append("UNION");
        sqlBuilder.append(" SELECT f.* FROM divisions AS f, divisions2 AS d WHERE f.parent_divisions_id= d.id");
        sqlBuilder.append(" and f.delete_flg = :deleteFlg and d.delete_flg = :deleteFlg and f.status = :status and d.status = :status ");
        sqlBuilder.append(" ) ");
        sqlBuilder.append("SELECT * FROM divisions2 ");
        return sessionFactory.getCurrentSession()
                .createNativeQuery(sqlBuilder.toString(), DivisionsEntity.class)
                .setParameter("divisionId", divisionId)
                .setParameter("deleteFlg", false)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public DivisionsEntity findDivisionByCustomerId(Long customerId) {
        StringBuilder sql = new StringBuilder("SELECT d FROM DivisionsEntity AS d, DivisionsHasCustomersEntity AS dhc ");
        sql.append("WHERE d.id = dhc.divisionsId ");
        sql.append("AND dhc.customersId = :customersId ");
        sql.append("AND dhc.deleteFlg = false ");
        List<DivisionsEntity> result = sessionFactory.getCurrentSession()
                .createQuery(sql.toString(), DivisionsEntity.class)
                .setParameter("customersId", customerId)
                .getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }
}
