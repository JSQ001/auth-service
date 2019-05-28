package com.hand.hcf.app.core.persistence.typehandler;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.util.Map;

/**
 * Created by Ray Ma on 2017/9/6.
 */
public class MapTypeHandler implements TypeHandler<Map<String,Object>> {

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, Map<String, Object> stringObjectMap, JdbcType jdbcType) throws SQLException {
        if (stringObjectMap == null) {
            preparedStatement.setNull(i, Types.VARCHAR);
        } else {
            preparedStatement.setString(i, stringObjectMap.toString());
        }
    }

    @Override
    public Map<String, Object> getResult(ResultSet resultSet, String s) throws SQLException {
        String value = resultSet.getString(s);
        Map map = JSON.parseObject(value, Map.class);
        return map ;
    }

    @Override
    public Map<String, Object> getResult(ResultSet resultSet, int i) throws SQLException {
        String value = resultSet.getString(i);
        Map map = JSON.parseObject(value,Map.class);
        return map;
    }

    @Override
    public Map<String, Object> getResult(CallableStatement callableStatement, int i) throws SQLException {
        String value = callableStatement.getString(i);
        Map map = JSON.parseObject(value,Map.class);
        return map;
    }
}
