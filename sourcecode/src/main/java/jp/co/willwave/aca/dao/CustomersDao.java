package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.CustomersEntity;
import jp.co.willwave.aca.model.entity.DivisionsHasCustomersEntity;
import jp.co.willwave.aca.model.enums.CustomerType;
import jp.co.willwave.aca.web.form.CustomerForm;
import jp.co.willwave.aca.web.form.SearchCustomerForm;

import java.util.List;

public interface CustomersDao extends BaseDao<CustomersEntity> {
    List<CustomersEntity> getCustomersList(List<Long> cusId) throws CommonException;

    List<DivisionsHasCustomersEntity> getDivisionsHasCustomersList(List<Long> divisionIds);

    /**
     * get {@link DivisionsHasCustomersEntity} by customer id
     * @param customerId customer id
     * @return
     */
    DivisionsHasCustomersEntity getDivisionsHasCustomersByCustomerId(Long customerId);

    List<CustomerForm> searchCustomer(SearchCustomerForm searchCustomerForm, Integer offset, Integer maxResults, CustomerType customerType);

    List<Object[]> searchAll(SearchCustomerForm searchCustomerForm, CustomerType customerType);

    List<CustomersEntity> getCustomersByDivisionIds(List<Long> divisionIds);
}
