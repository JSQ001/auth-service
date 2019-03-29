package com.hand.hcf.app.prepayment.persistence.typehandler;

import com.hand.hcf.app.common.enums.CashPayRequisitionTypeTypeEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by 韩雪 on 2017/12/28.
 */
@MappedTypes(CashPayRequisitionTypeTypeEnum.class)
public class CashPayRequisitionTypeTypeEnumTypeHandler extends BaseTypeHandler<CashPayRequisitionTypeTypeEnum> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, CashPayRequisitionTypeTypeEnum cashPayRequisitionTypeTypeEnum, JdbcType jdbcType) throws SQLException {
        if (cashPayRequisitionTypeTypeEnum != null) {
            preparedStatement.setInt(i, cashPayRequisitionTypeTypeEnum.getId());
        }
    }

    @Override
    public CashPayRequisitionTypeTypeEnum getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return CashPayRequisitionTypeTypeEnum.parse(resultSet.getInt(s));
    }

    @Override
    public CashPayRequisitionTypeTypeEnum getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return CashPayRequisitionTypeTypeEnum.parse(resultSet.getInt(i));
    }

    @Override
    public CashPayRequisitionTypeTypeEnum getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return CashPayRequisitionTypeTypeEnum.parse(callableStatement.getInt(i));
    }
}
