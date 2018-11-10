package com.hand.hcf.app.base.persistence.typehandler;

import com.hand.hcf.core.domain.CurrencyCode;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mybatis TypeHandler for CurrencyCode enum
 * <p>
 * Created by markfredchen on 15/04/2017.
 */
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
