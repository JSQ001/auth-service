package com.hand.hcf.app.prepayment.persistence.typehandler;

import com.hand.hcf.app.prepayment.enums.CashPayRequisitionTypeEmployeeEnum;
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
@MappedTypes(CashPayRequisitionTypeEmployeeEnum.class)
public class CashPayRequisitionTypeEmployeeEnumTypeHandler extends BaseTypeHandler<CashPayRequisitionTypeEmployeeEnum> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, CashPayRequisitionTypeEmployeeEnum cashPayRequisitionTypeEmployeeEnum, JdbcType jdbcType) throws SQLException {
        if (cashPayRequisitionTypeEmployeeEnum != null) {
            preparedStatement.setInt(i, cashPayRequisitionTypeEmployeeEnum.getId());
        }
    }

    @Override
    public CashPayRequisitionTypeEmployeeEnum getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return CashPayRequisitionTypeEmployeeEnum.parse(resultSet.getInt(s));
    }

    @Override
    public CashPayRequisitionTypeEmployeeEnum getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return CashPayRequisitionTypeEmployeeEnum.parse(resultSet.getInt(i));
    }

    @Override
    public CashPayRequisitionTypeEmployeeEnum getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return CashPayRequisitionTypeEmployeeEnum.parse(callableStatement.getInt(i));
    }
}
