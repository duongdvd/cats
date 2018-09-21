package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.CustomersEntity;
import jp.co.willwave.aca.model.entity.DivisionsHasCustomersEntity;

import java.util.List;

public interface DivisionsHasCustomersDao extends BaseDao<DivisionsHasCustomersEntity> {
    DivisionsHasCustomersEntity findByDivisionIdAndCustomerId(Long divisionsId, Long customerId);

    List<CustomersEntity> getCustomers(List<Long> divisionIds);
}
