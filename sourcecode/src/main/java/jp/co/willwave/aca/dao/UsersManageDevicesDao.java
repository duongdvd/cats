package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.UsersManageDevicesEntity;

import java.util.List;

public interface UsersManageDevicesDao extends BaseDao<UsersManageDevicesEntity> {
    List<UsersManageDevicesEntity> findByUserId(Long userId);

    UsersManageDevicesEntity findByUserIdAndDeviceId(Long userId, Long deviceId);

    List<UsersManageDevicesEntity> findByUserIdAndDeviceIdList(Long userId, List<Long> deviceIdList);

    List<UsersManageDevicesEntity> findByDeviceId(Long deviceId);
}
