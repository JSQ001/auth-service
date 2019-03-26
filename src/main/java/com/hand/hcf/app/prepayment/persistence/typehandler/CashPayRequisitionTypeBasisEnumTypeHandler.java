package com.hand.hcf.app.prepayment.persistence.typehandler;

import com.hand.hcf.app.prepayment.enums.CashPayRequisitionTypeBasisEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by 韩雪 on 2017/12/5.
 */
@MappedTypes(CashPayRequisitionTypeBasisEnum.class)
public class CashPayRequisitionTypeBasisEnumTypeHandler extends BaseTypeHandler<CashPayRequisitionTypeBasisEnum> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, CashPayRequisitionTypeBasisEnum cashPayRequisitionTypeBasisEnum, JdbcType jdbcType) throws SQLException {
        if (cashPayRequisitionTypeBasisEnum != null) {
            preparedStatement.setInt(i, cashPayRequisitionTypeBasisEnum.getId());
        }
    }

    @Override
    public CashPayRequisitionTypeBasisEnum getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return CashPayRequisitionTypeBasisEnum.parse(resultSet.getInt(s));
    }

    @Override
    public CashPayRequisitionTypeBasisEnum getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return CashPayRequisitionTypeBasisEnum.parse(resultSet.getInt(i));
    }

    @Override
    public CashPayRequisitionTypeBasisEnum getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return CashPayRequisitionTypeBasisEnum.parse(callableStatement.getInt(i));
    }
}
