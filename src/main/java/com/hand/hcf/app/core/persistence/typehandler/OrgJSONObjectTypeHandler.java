package com.hand.hcf.app.core.persistence.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cbc on 2017/11/20.
 */
@MappedTypes(JSONObject.class)
public class OrgJSONObjectTypeHandler extends BaseTypeHandler<JSONObject>{
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONObject jsonObject, JdbcType jdbcType) throws SQLException {
        if(jsonObject==null){
            ps.setString(i,null);
        }else{
            ps.setString(i,jsonObject.toString());
        }
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonObject = rs.getString(columnName);
        if(StringUtils.isEmpty(jsonObject)){
            return null;
        }else{
            try {
                return new JSONObject(jsonObject);
            } catch (JSONException e) {

            }
        }
        return null;
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonObject = rs.getString(columnIndex);
        if(StringUtils.isEmpty(jsonObject)){
            return null;
        }else{
            try {
                return new JSONObject(jsonObject);
            } catch (JSONException e) {

            }
        }
        return null;

    }

    @Override
    public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonObject = cs.getString(columnIndex);
        if(StringUtils.isEmpty(jsonObject)){
            return null;
        }else{
            try {
                return new JSONObject(jsonObject);
            } catch (JSONException e) {

            }
        }
        return null;
    }
}

