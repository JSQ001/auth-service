package com.hand.hcf.app.base.user.service;

import com.hand.hcf.app.base.system.enums.DeviceStatusEnum;
import com.hand.hcf.app.base.system.enums.DeviceVerificationStatus;
import com.hand.hcf.app.base.user.constant.UserLoginLogConstant;
import com.hand.hcf.app.base.user.domain.User;
import com.hand.hcf.app.base.user.domain.UserDevice;
import com.hand.hcf.app.base.user.domain.UserLoginLog;
import com.hand.hcf.app.base.user.persistence.UserDeviceMapper;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Weishan on 2018-12-21.
 */
@Slf4j
@Service
public class UserDeviceService extends BaseService<UserDeviceMapper, UserDevice> {

    @Autowired
    UserService userService;



    public List<UserDevice> findByUserOid(UUID userOid) {
        return baseMapper.selectUserDevice(userOid);
    }

    public UserDevice update(UserDevice userDevice) {
        updateById(userDevice);
        return userDevice;
    }

    public UserDevice delete(Long id) {
        UserDevice userDevice = new UserDevice();
        userDevice.setId(id);
        userDevice.setStatus(DeviceStatusEnum.DELETE.getId());
        updateById(userDevice);
        return userDevice;
    }

    public UserDevice updateRemark(UserDevice paramUserDevice) {
        UserDevice userDevice = selectById(paramUserDevice.getId());
        if (userDevice == null) {
            throw new ValidationException(new ValidationError("user device", "not.found"));
        }
        userDevice.setRemark(paramUserDevice.getRemark());
        updateById(userDevice);
        return userDevice;
    }


    public boolean logLogin(Map<String, String> param) {
        boolean isValidate = false;
        String deviceId = param.get("deviceId");
        UserDevice paramUserDevice = new UserDevice();
        if (!StringUtils.hasText(deviceId)) {
            isValidate = true;
        } else {
            paramUserDevice = parseUserDevice(param);
            UserDevice userDevice = new UserDevice();
            userDevice.setUserOid(paramUserDevice.getUserOid());
            userDevice.setDeviceID(paramUserDevice.getDeviceID());
            userDevice = baseMapper.selectOne(userDevice);
            if (userDevice != null) {
                isValidate = DeviceStatusEnum.NOMAL.getId().equals(userDevice.getStatus());
                try {
                    userDevice.setOsVersion(paramUserDevice.getOsVersion());
                    userDevice.setAppVersion(paramUserDevice.getAppVersion());
                    updateById(userDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                insert(paramUserDevice);
            }
            User user = userService.getById(LoginInformationUtil.getCurrentUserId());
            // 新版 APP 默认开启，只有设置为close时才不需要设备验证
            if (DeviceVerificationStatus.CLOSED.name().equals(user.getDeviceVerificationStatus())) {
                isValidate = true;
            }
        }

        //保存登录记录
        String login = param.get("username");
        if (StringUtils.hasText(login)) {
            UserLoginLog userLoginLog = new UserLoginLog();
            BeanUtils.copyProperties(paramUserDevice, userLoginLog, "id", "status");
            userLoginLog.setDeviceId(deviceId);
            userLoginLog.setLogin(login);
            userLoginLog.setLoginType(param.get("loginType"));
            if (isValidate) {
                userLoginLog.setStatus(UserLoginLogConstant.USER_LOGIN_STATUS_SUCCESS);
            } else {
                userLoginLog.setStatus(UserLoginLogConstant.USER_LOGIN_STATUS_FAIL);
            }

        }
        return isValidate;
    }

    private UserDevice parseUserDevice(Map<String, String> param) {
        UserDevice userDevice = new UserDevice();
        userDevice.setDeviceID(param.get("deviceId"));
        userDevice.setUserOid(LoginInformationUtil.getCurrentUserOid());
        if (StringUtils.hasText(param.get("vendorTypeID"))) {
            userDevice.setVendorTypeID(Integer.parseInt(param.get("vendorTypeID")));
        }
        if (StringUtils.hasText(param.get("platformID"))) {
            userDevice.setPlatformID(Integer.parseInt(param.get("platformID")));
        }
        userDevice.setOsVersion(param.get("osVersion"));
        userDevice.setAppVersion(param.get("appVersion"));
        userDevice.setPixelRatio(param.get("pixelRatio"));
        userDevice.setDeviceBrand(param.get("deviceBrand"));
        userDevice.setDeviceModel(param.get("deviceModel"));
        userDevice.setDeviceName(param.get("deviceName"));
        userDevice.setStatus(DeviceStatusEnum.UNVALIDATED.getId());
        return userDevice;
    }
}
