package jp.co.willwave.aca.service;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.SearchCondition;
import jp.co.willwave.aca.model.entity.CustomersEntity;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.DivisionsHasCustomersEntity;
import jp.co.willwave.aca.model.enums.CustomerType;
import jp.co.willwave.aca.web.form.CustomerForm;
import jp.co.willwave.aca.web.form.DivisionSelectForm;
import jp.co.willwave.aca.web.form.SearchCustomerForm;

import java.util.List;

public interface CustomersService {
    CustomersEntity getCustomer(Long id) throws CommonException;

    List<CustomersEntity> searchCustomers(SearchCondition searchCondition) throws CommonException;

    List<DivisionSelectForm> getDivisionList(Long userLoginId) throws CommonException;

    DivisionsEntity getBelongedDivision(Long userLoginId) throws CommonException;

    DivisionsEntity getDivision(Long customerId) throws CommonException;

    void create(CustomerForm customerForm)
            throws CommonException;

    void delete(Long customerId, CustomerType customerType) throws CommonException;

    void changeStatus(Long customerId, CustomerType customerType) throws CommonException;

    void update(CustomerForm customerForm)
            throws CommonException;

    Long findCustomerIdByDevicesId(Long devicesId);

    List<CustomersEntity> getCustomersList(List<Long> customerIds) throws CommonException;

    List<DivisionsHasCustomersEntity> getDivisionsHasCustomersList(List<Long> divisionId) throws CommonException;

    List<CustomerForm> searchCustomer(SearchCustomerForm searchCustomerForm, Integer offset, Integer maxResults, CustomerType customerType)
            throws CommonException;

    Long countSearchCustomer(SearchCustomerForm searchCustomerForm, CustomerType customerType) throws CommonException;

    List<DivisionSelectForm> getListDivisionFamily() throws CommonException;

    List<Object[]> searchAllCustomerExport(SearchCustomerForm searchCustomerForm,CustomerType customerType);

    List<CustomersEntity> getCustomersByUserId(Long userId) throws CommonException;

    List<CustomersEntity> getActiveCustomersByUserId(Long userId) throws CommonException;

    /**
     * check edit permission of the user (param userId)
     * @param userId
     * @param customerId
     * @return
     * @throws CommonException
     */
    boolean hasEditPermission(Long userId, Long customerId) throws CommonException;

    /**
     * check edit permission of login user
     * @param customerId
     * @return
     * @throws CommonException
     */
    boolean loginUserHasEditPermission(Long customerId) throws CommonException;

    /**
     * Find first inactive customer in list of customer id
     * @param customersId customers id
     * @return the first inactive {@link CustomersEntity}
     * @throws CommonException
     */
    CustomersEntity findInActive(List<Long> customersId) throws CommonException;

    /**
     * set edit, delete, change status permission
     * <p>for disabled edit, delete, change status button on customer list screen</p>
     * @param customerForm
     */
    void setPermission(CustomerForm customerForm) throws CommonException;

    /**
     * set edit, delete, change status permission
     * <p>for disabled edit, delete, change status button on customer list screen</p>
     * @param customerForm
     */
    void setPermission(List<CustomerForm> customerForm);
}
