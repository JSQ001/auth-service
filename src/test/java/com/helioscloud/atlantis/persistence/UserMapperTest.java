package com.helioscloud.atlantis.persistence;

import com.helioscloud.atlantis.AuthServiceSelectTest;
import com.helioscloud.atlantis.dto.UserDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest extends AuthServiceSelectTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void findOneByContactEmail() {
        String s = "yunong.li@hand-china.com";
        UserDTO userDTO = userMapper.findOneByContactEmail(s);
        assertThat(userDTO.getEmail()).isEqualTo(s);
    }

    @Test
    public void findUserByUserBind() {
        String login = "yunong.li@hand-china.com";
        List<UserDTO> dtoList = userMapper.findUserByUserBind(login);
        assertThat(dtoList.isEmpty()).isFalse();
    }

    @Test
    public void findOneByLogin() {
        String s = "18616808523";
        UserDTO userDTO = userMapper.findOneByLogin(s);
        assertThat(userDTO.getLogin()).isEqualTo(s);
    }

    @Test
    public void findOneByMobile() {
        String s = "18002288387";
        UserDTO userDTO = userMapper.findOneByMobile(s);
        assertThat(userDTO.getMobile()).isEqualTo(s);
    }

    @Test
    public void findOneByUserOID() {
        String s = "92ffa877-3f26-4d1a-82de-5b856672937f";
        UserDTO userDTO = userMapper.findOneByUserOID(UUID.fromString(s));
        assertThat(userDTO.getUserOID().toString()).isEqualTo(s);
    }

    @Test
    public void findOneByID() {
        Long s = 71L;
        UserDTO userDTO = userMapper.findOneByID(s);
        assertThat(userDTO.getId()).isEqualTo(s);
    }
}