package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dao.*;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.SearchCondition;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.*;
import jp.co.willwave.aca.model.enums.CustomerType;
import jp.co.willwave.aca.utilities.CatsMessageResource;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.CustomerForm;
import jp.co.willwave.aca.web.form.DivisionSelectForm;
import jp.co.willwave.aca.web.form.SearchCustomerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class CustomersServiceImpl extends BaseService implements CustomersService {

    private final CustomersDao customersDao;
    private final DivisionsDao divisionsDao;
    private final UsersDao userDao;
    private final DivisionsHasCustomersDao divisionsHasCustomersDao;
    private final CatsMessageResource catsMessageResource;
    private final RouteDetailDao routeDetailDao;

    @Autowired
    public CustomersServiceImpl(CustomersDao customersDao, DivisionsDao divisionsDao,
                                UsersDao userDao, DivisionsHasCustomersDao divisionsHasCustomersDao,
                                CatsMessageResource catsMessageResource, RouteDetailDao routeDetailDao) {
        this.customersDao = customersDao;
        this.divisionsDao = divisionsDao;
        this.userDao = userDao;
        this.divisionsHasCustomersDao = divisionsHasCustomersDao;
        this.catsMessageResource = catsMessageResource;
        this.routeDetailDao = routeDetailDao;
    }


    @Override
    public CustomersEntity getCustomer(Long id) throws CommonException {
        return customersDao.findById(id, CustomersEntity.class);
    }

    @Override
    public List<CustomersEntity> searchCustomers(SearchCondition searchCondition) throws CommonException {
        return customersDao.search(searchCondition, CustomersEntity.class);
    }

    @Override
    public List<DivisionSelectForm> getDivisionList(Long userLoginId) throws CommonException {
        List<DivisionsEntity> divisionsEntities;
        divisionsEntities = divisionsDao.getListDivisionRelative(userLoginId);
        List<DivisionSelectForm> divisionForms = new ArrayList<>();
        if (!CollectionUtils.isEmpty(divisionsEntities)) {
            divisionsEntities.forEach(x -> {
                DivisionSelectForm divisionForm = new DivisionSelectForm(x.getId(), x.getDivisionName());
                divisionForms.add(divisionForm);
            });
        }
        return divisionForms;
    }

    @Override
    public DivisionsEntity getBelongedDivision(Long userLoginId) throws CommonException {
        return divisionsDao.findBelongedDivisionByUserID(userLoginId);
    }

    @Override
    public DivisionsEntity getDivision(Long customerId) throws CommonException {
        return divisionsDao.findById(customerId, DivisionsEntity.class);
    }

    @Override
    public void create(CustomerForm customerForm) throws CommonException {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        validateCustomer(customerForm);
        CustomersEntity newCustomer = ConversionUtil.mapper(customerForm, CustomersEntity.class);

        newCustomer.setDeleteFlg(Boolean.FALSE);
        newCustomer.setId(null);
        customersDao.insert(newCustomer);
        DivisionsHasCustomersEntity divisionsHasCustomersEntity = new DivisionsHasCustomersEntity();
        divisionsHasCustomersEntity.setDivisionsId(userInfo.getDivisionsId());
        divisionsHasCustomersEntity.setCustomersId(newCustomer.getId());
        divisionsHasCustomersEntity.setDeleteFlg(Boolean.FALSE);
        divisionsHasCustomersDao.insert(divisionsHasCustomersEntity);
    }

    @Override
    public void delete(Long customerId, CustomerType customerType) throws CommonException {
        CustomersEntity oldCustomer = customersDao.findById(customerId, CustomersEntity.class);
        if (oldCustomer == null) {
            return;
        }
        if (!oldCustomer.getCustomerType().equals(customerType)) {
            throw new CommonException(catsMessageResource.getMessage(Constant.ErrorCode.FORMAT_INVALID, new String[]
                    {"customer.customer.type"}));
        }
        //check delete permission
        if (!this.hasDeleteAndChangeStatusPermission(customerId)) {
            throw new CommonException(catsMessageResource.get(Constant.ErrorCode.HAVE_NO_PERMISSION));
        }
        // check edit permission
        if (!this.loginUserHasEditPermission(oldCustomer.getId())) {
            throw new CommonException(catsMessageResource.get(Constant.ErrorCode.HAVE_NO_PERMISSION));
        }

        customersDao.delete(oldCustomer);
        DivisionsHasCustomersEntity divisionsHasCustomersEntity = divisionsHasCustomersDao.find("customersId",
                customerId.toString(), DivisionsHasCustomersEntity.class);
        if (divisionsHasCustomersEntity == null) {
            return;
        }
        divisionsHasCustomersDao.delete(divisionsHasCustomersEntity);
    }

    @Override
    public void changeStatus(Long customerId, CustomerType customerType) throws CommonException {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        CustomersEntity oldCustomer = customersDao.findById(customerId, CustomersEntity.class);
        if (oldCustomer == null) {
            throw new CommonException(catsMessageResource.getMessage(Constant.ErrorCode.NOT_EXISTS, new String[]
                    {"customer.customer"}));
        }
        if (!oldCustomer.getCustomerType().equals(customerType)) {
            throw new CommonException(catsMessageResource.getMessage(Constant.ErrorCode.FORMAT_INVALID, new String[]
                    {"customer.customer.type"}));
        }
        //check change status permission
        if (!this.hasDeleteAndChangeStatusPermission(customerId)) {
            throw new CommonException(catsMessageResource.get(Constant.ErrorCode.HAVE_NO_PERMISSION));
        }
        //check edit permission
        if (!this.loginUserHasEditPermission(oldCustomer.getId())) {
            throw new CommonException(catsMessageResource.get(Constant.ErrorCode.HAVE_NO_PERMISSION));
        }
        oldCustomer.setStatus(!oldCustomer.getStatus());
        oldCustomer.setUpdateDate(new Date());
        oldCustomer.setUpdateBy(userInfo.getId());
        customersDao.update(oldCustomer);
    }

    @Override
    public void update(CustomerForm customersForm)
            throws CommonException {
        validateCustomer(customersForm);
        CustomersEntity oldCustomer = customersDao.findById(customersForm.getId(), CustomersEntity.class);
        if (oldCustomer == null) {
            throw new CommonException(catsMessageResource.getMessage(Constant.ErrorCode.NOT_EXISTS, new String[]
                    {"customer.customer"}));
        }
        if (!this.loginUserHasEditPermission(oldCustomer.getId())) {
            throw new CommonException(catsMessageResource.get(Constant.ErrorCode.HAVE_NO_PERMISSION));
        }
        ConversionUtil.mapper(customersForm, oldCustomer);
        customersDao.update(oldCustomer);
    }

    @Override
    public Long findCustomerIdByDevicesId(Long devicesId) {
        return null;
    }

    @Override
    public List<CustomersEntity> getCustomersList(List<Long> customersId) throws CommonException {
        return customersDao.getCustomersList(customersId);
    }

    @Override
    public List<DivisionsHasCustomersEntity> getDivisionsHasCustomersList(List<Long> divisionIds) throws CommonException {
        return customersDao.getDivisionsHasCustomersList(divisionIds);
    }

    @Override
    public List<CustomerForm> searchCustomer(SearchCustomerForm searchCustomerForm,
                                             Integer offset, Integer maxResults, CustomerType customerType) throws
            CommonException {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        //for get edit permissions of login user
        searchCustomerForm.setLoginUserId(userInfo.getId());
        DivisionsEntity divisionManageByLoginUser = divisionsDao.findManagedDivisionByUserID(userInfo.getId());
        if (divisionManageByLoginUser != null) {
            searchCustomerForm.setDivisionIdsManagedByLoginUser(divisionsDao.getListDivisionIdManaged(divisionManageByLoginUser.getId()));
        }

        Map<Long, String> mapDivision = new HashMap<>();
        if (searchCustomerForm.getDivisionsId() == null) {
            List<DivisionsEntity> divisionsEntities = divisionsDao.getListDivisionRelative(userInfo.getId());
            if (CollectionUtils.isEmpty(divisionsEntities)) {
                return new ArrayList<>();
            }
            List<Long> divisionsIdRelative = divisionsEntities.stream()
                    .map(DivisionsEntity::getId).collect(Collectors.toList());
            searchCustomerForm.setDivisionsIds(divisionsIdRelative);
            mapDivision = divisionsEntities.stream().collect(
                    Collectors.toMap(DivisionsEntity::getId, DivisionsEntity::getDivisionName));
        } else {
            DivisionsEntity currentDivision = divisionsDao.findById(searchCustomerForm.getDivisionsId(),
                    DivisionsEntity.class);
            if (currentDivision == null) {
                return new ArrayList<>();
            }
            mapDivision.put(currentDivision.getId(), currentDivision.getDivisionName());
        }
        List<CustomerForm> customers = customersDao.searchCustomer(searchCustomerForm, offset, maxResults, customerType);
        if (!CollectionUtils.isEmpty(customers)) {
            Map<Long, String> finalMapDivision = mapDivision;
            customers.forEach(c -> {
                c.setDivisionName(finalMapDivision.get(c.getDivisionsId()));
            });
        }
        return customers;
    }

    @Override
    public Long countSearchCustomer(SearchCustomerForm searchCustomerForm, CustomerType customerType)
            throws CommonException {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        if (searchCustomerForm.getDivisionsId() == null) {
            List<DivisionsEntity> divisionsEntities = divisionsDao.getListDivisionRelative(userInfo.getId());
            if (CollectionUtils.isEmpty(divisionsEntities)) {
                return 0L;
            }
            List<Long> divisionsIdRelative = divisionsEntities.stream()
                    .map(DivisionsEntity::getId).collect(Collectors.toList());
            searchCustomerForm.setDivisionsIds(divisionsIdRelative);
        }
        List<Object[]> customersEntities = customersDao.searchAll(searchCustomerForm, customerType);
        return (long) customersEntities.size();
    }

    @Override
    public List<DivisionSelectForm> getListDivisionFamily() throws CommonException {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        DivisionsEntity divisionsEntity = divisionsDao.findManagedDivisionByUserID(userInfo.getId());
        if (divisionsEntity == null) {
            return new ArrayList<>();
        }
        List<DivisionsEntity> childrenDivision = divisionsDao.searchChildren(divisionsEntity.getId());
        List<DivisionsEntity> familyDivision = new ArrayList<>();
        if (!CollectionUtils.isEmpty(childrenDivision)) {
            familyDivision.addAll(childrenDivision);
        }
        familyDivision.add(divisionsEntity);
        return ConversionUtil.mapperAsList(familyDivision, DivisionSelectForm.class);
    }

    @Override
    public List<Object[]> searchAllCustomerExport(SearchCustomerForm searchCustomerForm, CustomerType customerType) {
        return customersDao.searchAll(searchCustomerForm,customerType);
    }

    @Override
    public List<CustomersEntity> getCustomersByUserId(Long userId) throws CommonException {
        List<Long> divisionIds = new ArrayList<>();
        List<DivisionsEntity> divisions = new ArrayList<>();
        UsersEntity usersEntity = userDao.findById(userId, UsersEntity.class);
        if (usersEntity != null) {
            divisionIds.add(usersEntity.getDivisionsId());
            divisions.addAll(divisionsDao.searchChildren(usersEntity.getDivisionsId()));
            divisions.addAll(divisionsDao.searchParents(usersEntity.getDivisionsId()));
            for (DivisionsEntity d : divisions) {
                divisionIds.add(d.getId());
            }
        }

        return customersDao.getCustomersByDivisionIds(divisionIds);
    }

    @Override
    public List<CustomersEntity> getActiveCustomersByUserId(Long userId) throws CommonException {
        return this.getCustomersByUserId(userId).stream().filter(c -> c.getStatus()).collect(Collectors.toList());
    }

    @Override
    public boolean hasEditPermission(Long userId, Long customerId) throws CommonException {
        CustomersEntity customersEntity = customersDao.findById(customerId, CustomersEntity.class);
        if (userId.equals(customersEntity.getCreateBy())) return true;

        DivisionsEntity divisionManaged = divisionsDao.findManagedDivisionByUserID(userId);
        //if the user isn't division director
        if (divisionManaged == null) return false;

        //check if the user is division director
        DivisionsHasCustomersEntity divisionsHasCustomersEntity = customersDao.getDivisionsHasCustomersByCustomerId(customerId);
        if (divisionsHasCustomersEntity == null) return false;
        List<Long> divisionIdsManaged = divisionsDao.getListDivisionIdManaged(divisionManaged.getId());
        return divisionIdsManaged.contains(divisionsHasCustomersEntity.getDivisionsId());
    }

    @Override
    public boolean loginUserHasEditPermission(Long customerId) throws CommonException {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        return this.hasEditPermission(userInfo.getId(), customerId);
    }

    private boolean hasDeleteAndChangeStatusPermission(Long customerId) {
        // if customer is in used
        //if (routeDetailDao.countByCustomerId(customerId) > 0) return false;
        return true;
    }

    private void initDivision(DivisionsHasCustomersEntity divisionsHasCustomersEntity) {
        UserInfo userInfo = (UserInfo) SessionUtil.getSession().getAttribute(Constant.Session.USER_LOGIN_INFO);
        divisionsHasCustomersEntity.setDivisionsId(userInfo.getDivisionsId());
    }

    private void validateCustomer(CustomerForm customer) throws CommonException {
        if (StringUtils.isEmpty(customer.getName())) {
            throw new CommonException(catsMessageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]
                    {"customer.name"}));
        } else {
            customer.setName(StringUtils.trimWhitespace(customer.getName()));
        }
        if (StringUtils.isEmpty(customer.getAddress())) {
            throw new CommonException(catsMessageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]
                    {"customer.address"}));
        } else {
            customer.setAddress(StringUtils.trimWhitespace(customer.getAddress()));
        }
        if (!StringUtils.isEmpty(customer.getBuildingName())) {
            customer.setBuildingName(StringUtils.trimWhitespace(customer.getBuildingName()));
        }
        if (StringUtils.isEmpty(customer.getLatitude())) {
            throw new CommonException(catsMessageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]
                    {"customer.latitude"}));
        } else {
            customer.setLatitude(StringUtils.trimWhitespace(customer.getLatitude()));
        }
        if (StringUtils.isEmpty(customer.getLongitude())) {
            throw new CommonException(catsMessageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]
                    {"customer.longitude"}));
        } else {
            customer.setLongitude(StringUtils.trimWhitespace(customer.getLongitude()));
        }
    }

    @Override
    public CustomersEntity findInActive(List<Long> customersId) throws CommonException {
        for (Long customerId: customersId) {
            CustomersEntity customer = this.getCustomer(customerId);
            if (customer != null && !customer.getStatus()) return customer;
        }
        return null;
    }

    @Override
    public void setPermission(CustomerForm customerForm) throws CommonException {
        customerForm.setEditPermission(this.loginUserHasEditPermission(customerForm.getId()));
        customerForm.setDeleteAndChangeStatusPermission(this.hasDeleteAndChangeStatusPermission(customerForm.getId()));
    }

    @Override
    public void setPermission(List<CustomerForm> customersForm) {
        customersForm.forEach(customer -> {
            try {
                this.setPermission(customer);
            } catch (CommonException e) {
                logger.error("setPermission", e);
            }
        });
    }
}
