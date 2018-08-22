package com.helioscloud.atlantis.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.domain.FrontKey;

import java.util.List;

public interface FrontKeyMapper extends BaseMapper<FrontKey> {
    List<FrontKey> getListFrontKeysNotInLanguage(String lanugage);
}
