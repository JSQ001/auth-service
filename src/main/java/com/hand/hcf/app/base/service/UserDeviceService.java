package com.hand.hcf.app.base.service;

import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import com.hand.hcf.app.base.domain.UserDevice;
import com.hand.hcf.app.base.domain.enumeration.DeviceStatusEnum;
import com.hand.hcf.app.base.persistence.UserDeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by Transy on 2017-12-21.
 */
@Service
public class UserDeviceService {
    @Autowired
    private UserDeviceMapper userDeviceMapper;

    public List<UserDevice> findByUserOID(UUID userOID) {
        return userDeviceMapper.selectUserDevice(userOID);
    }

    public UserDevice update(UserDevice userDevice) {
        userDeviceMapper.updateById(userDevice);
        return userDevice;
    }

    public UserDevice delete(Long id) {
        UserDevice userDevice = new UserDevice();
        userDevice.setId(id);
        userDevice.setStatus(DeviceStatusEnum.DELETE.getID());
        userDeviceMapper.updateById(userDevice);
        return userDevice;
    }

    public UserDevice updateRemark(UserDevice paramUserDevice) {
        UserDevice userDevice = userDeviceMapper.selectById(paramUserDevice.getId());
        if (userDevice == null) {
            throw new ValidationException(new ValidationError("user device", "not.found"));
        }
        userDevice.setRemark(paramUserDevice.getRemark());
        userDeviceMapper.updateById(userDevice);
        return userDevice;
    }
}
