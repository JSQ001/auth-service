package com.helioscloud.atlantis.persistence;

import com.helioscloud.atlantis.AuthServiceSelectTest;
import com.helioscloud.atlantis.domain.PasswordHistory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordHistoryMapperTest extends AuthServiceSelectTest {

    @Autowired
    private PasswordHistoryMapper passwordHistoryMapper;
    
    @Test
    public void getPasswordHistoryOrderByCreateDate() {
        String s = "329e6ede-ff54-4e87-a213-684e89bb4b30";
        List<PasswordHistory> historyList = passwordHistoryMapper.getPasswordHistoryOrderByCreateDate(s);
        assertThat(historyList.isEmpty()).isFalse();
        assertThat(historyList.get(0).getUserOID().toString()).isEqualTo(s);
    }
}