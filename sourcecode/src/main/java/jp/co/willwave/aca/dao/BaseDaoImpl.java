package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.ColumnCondition;
import jp.co.willwave.aca.model.Condition;
import jp.co.willwave.aca.model.SearchCondition;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.BaseEntity;
import jp.co.willwave.aca.model.enums.SearchOperator;
import jp.co.willwave.aca.model.enums.Type;
import jp.co.willwave.aca.utilities.CatStringUtil;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public abstract class BaseDaoImpl<T extends BaseEntity> implements BaseDao<T> {
    public static final String SPACE = " ";
    protected Logger logger;
    protected final SessionFactory sessionFactory;

    @Autowired
    public BaseDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public T findById(Long id, Class<T> tClass) throws CommonException {
        return ConversionUtil.castObject(executeWithException(() -> {
            return sessionFactory.getCurrentSession().get(tClass, id);
        }), tClass);
    }

    @Override
    public T find(String columnName, String value, Class<T> tClass) throws CommonException {
        return ConversionUtil.castObject(executeWithException(() -> {
            Session session = sessionFactory.getCurrentSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(tClass);
            Root<T> table = query.from(tClass);
            List<T> resultList = session.createQuery(
                    query.select(table)
                            .where(
                                    builder.equal(table.get(columnName), value))).getResultList();
            return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
        }), tClass);

    }

    @Override
    public List<T> search(SearchCondition searchCondition, Class<T> tClass) throws CommonException {
        return ConversionUtil.castList(executeWithException(() -> {
            SqlSearch sqlSearch = new SqlSearch();
            sqlSearch = createQuerySql(sqlSearch, tClass.getSimpleName(), searchCondition);

            String querySql = "FROM " + tClass.getSimpleName();
            String sqlWhere = sqlSearch.getSql();
            if (!CatStringUtil.isEmpty(sqlWhere)) {
                querySql += " WHERE " + sqlWhere;
            }

            logger.info("Search SQL statement:" + querySql);

            Query query = sessionFactory.getCurrentSession().createQuery(querySql);
            Map<String, Object> paramMap = sqlSearch.getParamMap();
            for (String key : paramMap.keySet()) {
                query.setParameter(key, paramMap.get(key));
            }
            return query.getResultList();
        }), tClass);

    }

    @Override
    public void insert(T o) throws CommonException {
        executeWithException(() -> {
            UserInfo userInfo = SessionUtil.getLoginUser();
            if (userInfo != null) {
                o.setCreateBy(userInfo.getId());
                o.setUpdateBy(userInfo.getId());
            } else {
                o.setCreateBy(null);
                o.setUpdateBy(null);
            }

            Date sysDate = new Date();
            o.setUpdateDate(sysDate);
            o.setCreateDate(sysDate);
            return sessionFactory.getCurrentSession().save(o);
        });
    }

    @Override
    public void update(T t) throws CommonException {
        try {
            Session session = sessionFactory.getCurrentSession();
            UserInfo userInfo = SessionUtil.getLoginUser();
            if (userInfo != null) {
                t.setUpdateBy(userInfo.getId());
            }
            t.setUpdateDate(new Date());
            session.update(t);
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void delete(T o) throws CommonException {
        try {
            o.setDeleteFlg(Boolean.TRUE);
            update(o);
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }

    @Override
    public List<T> findAll(Class<T> tClass) throws CommonException {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(tClass);
        Root<T> table = query.from(tClass);
        return session.createQuery(query.select(table))
                .getResultList();
    }

    private SqlSearch createQuerySql(SqlSearch sqlSearch, String entityName, SearchCondition searchCondition) {
        List<ColumnCondition> columnConditions = searchCondition.getColumnConditions();
        StringBuilder sql = new StringBuilder();
        for (ColumnCondition cc : columnConditions) {
            List<Condition> conditions = cc.getConditions();
            for (Condition condition : conditions) {
                if (sql.length() == 0) {
                    sql.append(createCondition(sqlSearch, entityName, condition, cc.getType()).getSql()).append(SPACE);
                } else {
                    sql.append("AND ").append(createCondition(
                            sqlSearch, entityName, condition, cc.getType()).getSql()).append(SPACE);
                }
            }
        }
        sqlSearch.setSql(sql.toString());
        return sqlSearch;
    }

    public class SqlSearch {
        private String sql;
        private Map<String, Object> paramMap = new HashMap<>();

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public Map<String, Object> getParamMap() {
            return paramMap;
        }

        public void setParamMap(Map<String, Object> paramMap) {
            this.paramMap = paramMap;
        }

        public void put(String key, Object o) {
            paramMap.put(key, o);
        }

        public Object get(String key) {
            return paramMap.get(key);
        }

    }

    private SqlSearch createCondition(SqlSearch sqlSearch, String entityName, Condition condition, Type type) {
        StringBuilder sql = new StringBuilder("");
        switch (condition.getOperator()) {
            case SearchOperator.C_EQUAL:
                sql.append(condition.getName()).append(" = :").append(condition.getName()).append(SPACE);
                sqlSearch.put(condition.getName(), condition.getValueList().get(0));
                break;
            case SearchOperator.C_GREATER:
                sql.append(condition.getName()).append(" > :").append(condition.getName()).append(SPACE);
                sqlSearch.put(condition.getName(), condition.getValueList().get(0));
                break;
            case SearchOperator.C_GREATER_OR_EQUAL:
                sql.append(condition.getName()).append(" >= :").append(condition.getName()).append(SPACE);
                sqlSearch.put(condition.getName(), condition.getValueList().get(0));
                break;
            case SearchOperator.C_LESS:
                sql.append(condition.getName()).append(" < :").append(condition.getName()).append(SPACE);
                sqlSearch.put(condition.getName(), condition.getValueList().get(0));
                break;
            case SearchOperator.C_LESS_OR_EQUAL:
                sql.append(condition.getName()).append(" <= :").append(condition.getName()).append(SPACE);
                sqlSearch.put(condition.getName(), condition.getValueList().get(0));
                break;
            case SearchOperator.C_LIKE:
                sql.append("UPPER(").append(condition.getName()).append(")").append(" LIKE CONCAT('%', CONCAT(:")
                        .append(condition.getName()).append(",'%'))")
                        .append(SPACE);
                sqlSearch.put(condition.getName(), condition.getValueList().get(0));
                break;
            case SearchOperator.C_IS_NULL:
                sql.append(condition.getName()).append(" IS NULL").append(SPACE);
                break;
            case SearchOperator.C_IS_NOT_NULL:
                sql.append(condition.getName()).append(" IS NOT NULL").append(SPACE);
                break;
            case SearchOperator.C_IN_VALUES:
                sql.append(condition.getName()).append(" IN :").append(condition.getName()).append(SPACE);
                sqlSearch.put(condition.getName(), condition.getValueList());
                break;
        }

        sqlSearch.setSql(sql.toString());
        return sqlSearch;
    }


    public interface DaoCaller {
        Object execute() throws CommonException;
    }

    protected Object executeWithException(DaoCaller caller) throws CommonException {
        try {
            return caller.execute();
        } catch (Exception ex) {
            throw new CommonException(ex);
        }
    }

    @Override
    public void saveOrUpdate(T o) {
        UserInfo userInfo = SessionUtil.getLoginUser();
        Long id = o.getId();
        Date sysDate = new Date();
        if (id == null) {
            o.setCreateDate(sysDate);
            o.setCreateBy(userInfo == null ? null : userInfo.getId());
        }
        o.setUpdateBy(userInfo == null ? null : userInfo.getId());
        o.setUpdateDate(sysDate);

        sessionFactory.getCurrentSession().merge(o);
    }
}
