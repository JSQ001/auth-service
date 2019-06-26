package com.hand.hcf.app.ant.excel.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplateField;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/10
 */
@Component
public interface ExcelTemplateFieldMapper extends BaseMapper<ExcelTemplateField> {
    List<ExcelTemplateField> selectTemplateField(@Param("excelTemplateId") Long excelTemplateId);
}
