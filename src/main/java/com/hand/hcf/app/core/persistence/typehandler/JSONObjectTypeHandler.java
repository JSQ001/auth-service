

package com.hand.hcf.app.core.persistence.typehandler;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JSONObjectTypeHandler extends BaseTypeHandler<JSONObject> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONObject jsonObject, JdbcType jdbcType) throws SQLException {
        if (jsonObject != null) {
            ps.setString(i, jsonObject.toJSONString());
        }
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        if (!StringUtils.isEmpty(result)) {
            return JSONObject.parseObject(result);
        } else {
            return null;
        }
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        if (!StringUtils.isEmpty(result)) {
            return JSONObject.parseObject(result);
        } else {
            return null;
        }
    }

    @Override
    public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        if (!StringUtils.isEmpty(result)) {
            return JSONObject.parseObject(result);
        } else {
            return null;
        }
    }
}
