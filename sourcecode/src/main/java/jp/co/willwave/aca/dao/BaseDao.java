package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.SearchCondition;
import jp.co.willwave.aca.model.entity.BaseEntity;

import java.util.List;

public interface BaseDao<T extends BaseEntity> {
    T findById(Long id, Class<T> tClass) throws CommonException;

    T find(String columnName, String value, Class<T> tClass) throws CommonException;

    List<T> search(SearchCondition searchCondition, Class<T> tClass) throws CommonException;

    void insert(T o) throws CommonException;

    void update(T o) throws CommonException;

    void delete(T o) throws CommonException;
    void saveOrUpdate(T o) throws CommonException;

    List<T> findAll(Class<T> tClass) throws CommonException;
}
