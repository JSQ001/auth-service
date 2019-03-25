package com.hand.hcf.app.base.implement.web;

import com.hand.hcf.app.base.user.UserCO;
import com.hand.hcf.app.base.user.service.UserService;
import com.hand.hcf.app.base.user.enums.UserStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
public class UserControllerImpl {

    @Autowired
    private UserService userService;

    public String getLanguageByUserOid(String userOid) {
        return userService.getUserLanguage(UUID.fromString(userOid));
    }

    public UserCO saveUser(UserCO user) {
        return userService.saveUserCO(user);
    }

    public void updateUserLeaveOffice(Long userId) {
        userService.updateStatus(userId, UserStatusEnum.INVALID);
    }

    public void updateUserRecoverEntry(Long userId) {
        userService.updateStatus(userId, UserStatusEnum.VALID);
    }

    public List<UserCO> saveUserBatch(List<UserCO> users) {
        return userService.saveUserCOList(users);
    }
}

