package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.SearchEmployeeForm;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class UsersDaoImpl extends BaseDaoImpl<UsersEntity> implements UsersDao {
    protected final SessionFactory sessionFactory;

    public UsersDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UsersEntity getByLoginId(String userName) throws CommonException {
        UsersEntity usersEntity = find("loginId", userName, UsersEntity.class);
        if (usersEntity != null && usersEntity.getLoginId().equals(userName)){
            return usersEntity;
        }
        return null;
    }

    @Override
    public long updatePassword(String userName, String newPassword) {
        return 0;
    }

    @Override
    public List<UsersEntity> findUsersByDivisionId(List<Long> divisionIds) throws CommonException {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM UsersEntity WHERE divisionsId IN :divisionIds AND deleteFlg = 0 ", UsersEntity.class)
                .setParameter("divisionIds", divisionIds)
                .getResultList();
    }

    @Override
    public List<UsersEntity> searchEmployee(SearchEmployeeForm searchEmployeeForm, Integer offset, Integer
            maxResults) {
        Query<UsersEntity> query = buildQuerySearch(searchEmployeeForm);
        query.setFirstResult(offset != null ? offset : 0);
        query.setMaxResults(maxResults != null ? maxResults : 10);
        return query.getResultList();
    }

    @Override
    public List<UsersEntity> searchAll(SearchEmployeeForm searchEmployeeForm) {
        Query<UsersEntity> query = buildQuerySearch(searchEmployeeForm);
        return query.getResultList();
    }

    @Override
    public boolean checkExistsLoginId(String loginId) throws CommonException {
        UsersEntity userLoginId = find("loginId", loginId, UsersEntity.class);
        return userLoginId != null;
    }

    @Override
    public boolean checkExistsMail(String email) throws CommonException {
        if (StringUtils.isEmpty(email)) return false;
        UsersEntity usersEmail = find("userEmail", email, UsersEntity.class);
        return usersEmail != null;
    }


    @Override
    public UsersEntity findUserByDivisionId(Long divisionId) {
        UsersEntity usersEntity = sessionFactory.getCurrentSession()
                .createQuery("FROM UsersEntity WHERE divisionsId = :divisionIds AND deleteFlg = 0 ", UsersEntity.class)
                .setParameter("divisionIds", divisionId)
                .getResultList().get(0);
        return usersEntity;
    }

    public List<UsersEntity> findByDivision(Long divisionId) {
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<UsersEntity> criteriaQuery = criteriaBuilder.createQuery(UsersEntity.class);
        Root<UsersEntity> root = criteriaQuery.from(UsersEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("divisionsId"), divisionId));
        return sessionFactory.getCurrentSession().createQuery(criteriaQuery).getResultList();
    }

    @Override
    public UsersEntity getUserByEmail(String email) throws CommonException {
        UsersEntity usersEmail = find("userEmail", email, UsersEntity.class);
        if (usersEmail != null) {
            return usersEmail;
        }
        return null;
    }


    @Override
    public List<UsersEntity> getInfoUserManageDevice(Long devicesId) {
        StringBuilder sql = new StringBuilder("SELECT u FROM UsersEntity AS u, UsersManageDevicesEntity AS umd ");
        sql.append("WHERE u.id = umd.usersId AND umd.devicesId = :devicesId ");
        sql.append("AND u.deleteFlg = false ");
        sql.append("AND umd.deleteFlg = false ");
        return sessionFactory.getCurrentSession()
                .createQuery(sql.toString(), UsersEntity.class)
                .setParameter("devicesId", devicesId)
                .getResultList();
    }

    @Override
    public UsersEntity getUserCreateRoute(Long routePlanedId) {
        StringBuilder sql = new StringBuilder("SELECT u FROM UsersEntity AS u, RoutesEntity AS r ");
        sql.append("WHERE u.id = r.createBy ");
        sql.append("AND r.id = :routePlanedId ");
        List<UsersEntity> usersEntities = sessionFactory.getCurrentSession()
                .createQuery(sql.toString(), UsersEntity.class)
                .setParameter("routePlanedId", routePlanedId)
                .getResultList();
        if (!CollectionUtils.isEmpty(usersEntities)) {
            return usersEntities.get(0);
        }
        return null;
    }

    private Query<UsersEntity> buildQuerySearch(SearchEmployeeForm searchEmployeeForm) {
        UserInfo userInfo = (UserInfo) SessionUtil.getSession().getAttribute(Constant.Session.USER_LOGIN_INFO);
        StringBuilder sqlBuilder = new StringBuilder("FROM UsersEntity WHERE 1 = 1 ");
        if (searchEmployeeForm != null) {
            if (searchEmployeeForm.getId() != null) {
                sqlBuilder.append("AND id = :id ");

            }
            if (!StringUtils.isEmpty(searchEmployeeForm.getLoginId())) {
                sqlBuilder.append("AND loginId = :loginId ");
            }
            if (!StringUtils.isEmpty(searchEmployeeForm.getName())) {
                sqlBuilder.append("AND ( firstName LIKE CONCAT('%', CONCAT(:name,'%')) OR  lastName LIKE CONCAT" +
                        "('%', CONCAT( :name,'%')) ) ");
            }
            if (!StringUtils.isEmpty(searchEmployeeForm.getEmail())) {
                sqlBuilder.append("AND mail LIKE CONCAT('%', CONCAT(:mail,'%')) ");
            }
            if (!StringUtils.isEmpty(searchEmployeeForm.getFixedPhone())) {
                sqlBuilder.append("AND fixedPhone = :fixedPhone ");
            }
            if (!StringUtils.isEmpty(searchEmployeeForm.getMobilePhone())) {
                sqlBuilder.append("AND mobilePhone = :mobilePhone ");
            }
            if (searchEmployeeForm.getStatus() != null) {
                sqlBuilder.append("AND status = :status ");
            }
            if (searchEmployeeForm.getDivisionsId() != null) {
                sqlBuilder.append("AND divisionsId = :division ");
            } else {
                sqlBuilder.append("AND divisionsId IN :division ");
            }
            if (searchEmployeeForm.getRoleId() != null) {
                sqlBuilder.append("AND roleId = :roleId ");
            } else {
                sqlBuilder.append("AND roleId IN :roleId ");
            }
        }
        sqlBuilder.append("AND deleteFlg = 0 ");
        sqlBuilder.append("AND id <> :currentId ");
        sqlBuilder.append("ORDER BY id ");
        Query<UsersEntity> query = sessionFactory.getCurrentSession()
                .createQuery(sqlBuilder.toString(), UsersEntity.class);

        if (searchEmployeeForm != null) {
            if (searchEmployeeForm.getId() != null) {
                query.setParameter("id", searchEmployeeForm.getId());
            }
            if (!StringUtils.isEmpty(searchEmployeeForm.getLoginId())) {
                query.setParameter("loginId", searchEmployeeForm.getLoginId());
            }
            if (!StringUtils.isEmpty(searchEmployeeForm.getName())) {
                query.setParameter("name", searchEmployeeForm.getName());
            }
            if (!StringUtils.isEmpty(searchEmployeeForm.getEmail())) {
                query.setParameter("mail", searchEmployeeForm.getEmail());
            }
            if (!StringUtils.isEmpty(searchEmployeeForm.getFixedPhone())) {
                query.setParameter("fixedPhone", searchEmployeeForm.getFixedPhone());
            }
            if (!StringUtils.isEmpty(searchEmployeeForm.getMobilePhone())) {
                query.setParameter("mobilePhone", searchEmployeeForm.getMobilePhone());
            }
            if (searchEmployeeForm.getStatus() != null) {
                query.setParameter("status", searchEmployeeForm.getStatus());
            }
            if (searchEmployeeForm.getDivisionsId() != null) {
                query.setParameter("division", searchEmployeeForm.getDivisionsId());
            } else {
                query.setParameter("division", searchEmployeeForm.getDivisionsIds());
            }
            if (searchEmployeeForm.getRoleId() != null) {
                query.setParameter("roleId", searchEmployeeForm.getRoleId());
            } else {
                query.setParameter("roleId", searchEmployeeForm.getRoleIds());
            }
        }
        query.setParameter("currentId", userInfo.getId());
        return query;
    }
}
