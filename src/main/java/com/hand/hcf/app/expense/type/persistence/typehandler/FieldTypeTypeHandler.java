package com.hand.hcf.app.expense.type.persistence.typehandler;

import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by markfredchen on 2017/3/15.
 */
@MappedTypes(FieldType.class)
public class FieldTypeTypeHandler extends BaseTypeHandler<FieldType> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, FieldType fieldType, JdbcType jdbcType) throws SQLException {
        if (fieldType != null) {
            preparedStatement.setInt(i, fieldType.getId());
        }
    }

    @Override
    public FieldType getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return FieldType.parse(resultSet.getInt(s));
    }

    @Override
    public FieldType getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return FieldType.parse(resultSet.getInt(i));
    }

    @Override
    public FieldType getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return FieldType.parse(callableStatement.getInt(i));
    }
}
