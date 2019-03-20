package com.hand.hcf.app.auth.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.auth.dto.UserDTO;
import com.hand.hcf.app.auth.dto.UserQO;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface UserMapper extends BaseMapper<UserDTO> {

   List<UserDTO> listDtoByQO(UserQO userQO);

   Integer countLoginBind(UUID userOid);


   List<ZonedDateTime> listLastPasswordDate(UUID userOid);

   Integer updateUserLockStatus(UserDTO userDTO);
}
