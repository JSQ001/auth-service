

package com.hand.hcf.app.core.persistence.typehandler;

import com.hand.hcf.app.core.domain.CurrencyCode;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyCodeTypeHandler extends BaseTypeHandler<CurrencyCode> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, CurrencyCode currencyCode, JdbcType jdbcType) throws SQLException {
        if (currencyCode != null) {
            preparedStatement.setInt(i, currencyCode.getId());
        }
    }

    @Override
    public CurrencyCode getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return CurrencyCode.parse(resultSet.getInt(s));
    }

    @Override
    public CurrencyCode getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return CurrencyCode.parse(resultSet.getInt(i));
    }

    @Override
    public CurrencyCode getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return CurrencyCode.parse(callableStatement.getInt(i));
    }
}
