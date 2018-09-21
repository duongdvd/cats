package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.CustomersEntity;
import jp.co.willwave.aca.model.entity.DivisionsHasCustomersEntity;
import jp.co.willwave.aca.model.enums.CustomerType;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.web.form.CustomerForm;
import jp.co.willwave.aca.web.form.SearchCustomerForm;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
public class CustomersDaoImpl extends BaseDaoImpl<CustomersEntity> implements CustomersDao {

    public CustomersDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

	@Override
	public List<CustomersEntity> getCustomersList(List<Long> cusId) throws CommonException {
		return sessionFactory.getCurrentSession()
				.createQuery("FROM CustomersEntity WHERE id IN :cusId ", CustomersEntity.class)
				.setParameter("cusId", cusId)
				.getResultList();
	}

	@Override
    public List<DivisionsHasCustomersEntity> getDivisionsHasCustomersList(List<Long> divisionIds) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM DivisionsHasCustomersEntity WHERE divisionsId IN :divisionIds", DivisionsHasCustomersEntity.class)
                .setParameter("divisionIds", divisionIds)
                .getResultList();
    }

	@Override
	public DivisionsHasCustomersEntity getDivisionsHasCustomersByCustomerId(Long customerId) {
		List<DivisionsHasCustomersEntity> divisionsHasCustomersEntities = sessionFactory.getCurrentSession()
				.createQuery("FROM DivisionsHasCustomersEntity WHERE customersId = :customersId", DivisionsHasCustomersEntity.class)
				.setParameter("customersId", customerId)
				.getResultList();
		if (CollectionUtils.isEmpty(divisionsHasCustomersEntities)) {
			return null;
		}
		return divisionsHasCustomersEntities.get(0);
	}

	@Override
	public List<CustomersEntity> getCustomersByDivisionIds(List<Long> divisionIds) {
    	String strQuery = "SELECT c FROM CustomersEntity as c INNER JOIN DivisionsHasCustomersEntity as d on" +
			" d.customersId = c.id" +
			" WHERE d.divisionsId IN :divisionIds" +
			" AND d.deleteFlg = false and c.deleteFlg = false";

		return sessionFactory.getCurrentSession()
			.createQuery(strQuery, CustomersEntity.class)
			.setParameter("divisionIds", divisionIds)
			.getResultList();
	}

	@Override
	public List<CustomerForm> searchCustomer(SearchCustomerForm searchCustomerForm, Integer offset,
	                                         Integer maxResults, CustomerType customerType) {
        Query<Object[]> query = buildQuerySearch(searchCustomerForm, customerType);

		offset = offset != null ? offset : 0;
		maxResults = maxResults != null ? maxResults : 10;

		query.setMaxResults(maxResults);
		query.setFirstResult(offset);
		List<Object[]> customerObject = query.getResultList();
		List<CustomerForm> customerForms = new ArrayList<>();
		if (!CollectionUtils.isEmpty(customerObject)) {
			customerObject.forEach(x -> {
				CustomerForm customerForm = ConversionUtil.mapper(x[0], CustomerForm.class);
				customerForm.setDivisionsId(Long.valueOf(x[1].toString()));
				customerForms.add(customerForm);
			});
		}
		return customerForms;
	}

    @Override
	public List<Object[]> searchAll(SearchCustomerForm searchCustomerForm, CustomerType customerType) {
		Query<Object[]> query = buildQuerySearch(searchCustomerForm, customerType);
		return query.getResultList();
    }

	private Query<Object[]> buildQuerySearch(SearchCustomerForm searchCustomerForm, CustomerType type) {
		StringBuilder sqlBuilder = new StringBuilder("SELECT c, dhc.divisionsId ")
				.append("FROM CustomersEntity AS c, ")
				.append("DivisionsHasCustomersEntity AS dhc WHERE 1 = 1 AND c.id = dhc.customersId ");
		if (searchCustomerForm != null) {
			if (searchCustomerForm.getId() != null) {
				sqlBuilder.append("AND c.id = :id ");
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getName())) {
                sqlBuilder.append("AND c.name LIKE CONCAT('%', CONCAT(:name,'%')) ");
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getAddress())) {
                sqlBuilder.append("AND c.address LIKE CONCAT('%', CONCAT(:address,'%')) ");
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getBuildingName())) {
				sqlBuilder.append("AND c.buildingName LIKE CONCAT('%',CONCAT(:buildingName, '%')) ");
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getDescription())) {
				sqlBuilder.append("AND c.description LIKE CONCAT('%', CONCAT(:description,'%')) ");
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getLongitude())) {
				sqlBuilder.append("AND c.longitude = :longitude ");
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getLatitude())) {
				sqlBuilder.append("AND c.latitude = :latitude ");
			}
			if (searchCustomerForm.getStatus() != null) {
				sqlBuilder.append("AND c.status = :status ");
			}
			if (searchCustomerForm.getDivisionsId() == null) {
				sqlBuilder.append("AND dhc.divisionsId IN :divisions ");
			} else {
				sqlBuilder.append("AND dhc.divisionsId = :divisions ");
			}
		}
		sqlBuilder.append("AND c.customerType = :type ORDER BY c.createDate DESC");
		Query<Object[]> query = sessionFactory.getCurrentSession().createQuery(sqlBuilder.toString(), Object[].class);
		if (searchCustomerForm != null) {
			if (searchCustomerForm.getId() != null) {
				query.setParameter("id", searchCustomerForm.getId());
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getName())) {
				query.setParameter("name", searchCustomerForm.getName());
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getAddress())) {
				query.setParameter("address", searchCustomerForm.getAddress());
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getBuildingName())) {
				query.setParameter("buildingName", searchCustomerForm.getBuildingName());
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getDescription())) {
				query.setParameter("description", searchCustomerForm.getDescription());
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getLongitude())) {
				query.setParameter("longitude", searchCustomerForm.getLongitude());
			}
			if (!StringUtils.isEmpty(searchCustomerForm.getLatitude())) {
				query.setParameter("latitude", searchCustomerForm.getLatitude());
			}
			if (searchCustomerForm.getStatus() != null) {
				query.setParameter("status", searchCustomerForm.getStatus());
			}
			if (searchCustomerForm.getDivisionsId() == null) {
				query.setParameter("divisions", searchCustomerForm.getDivisionsIds());
			} else {
				query.setParameter("divisions", searchCustomerForm.getDivisionsId());
			}
		}
		query.setParameter("type", type);
		return query;
	}
}
