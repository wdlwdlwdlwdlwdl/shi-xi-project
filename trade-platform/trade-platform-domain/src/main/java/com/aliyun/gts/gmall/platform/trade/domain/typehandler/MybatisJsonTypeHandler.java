package com.aliyun.gts.gmall.platform.trade.domain.typehandler;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.*;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * chaoyin
 *
 * @param <T>
 */
@MappedTypes({
        HashMap.class,
        Map.class,
        OrderAttrDO.class,
        SalesInfoDO.class,
        OrderFeeAttrDO.class,
        PromotionAttrDO.class,
        ReceiverInfoDO.class,
        ReversalFeatureDO.class,
        RefundFeatureDO.class,
        OrderItemFeatureDO.class,
        CartFeatureDO.class,
        StepOrderFeeDO.class,
        StepOrderFeatureDO.class,
})
public class MybatisJsonTypeHandler<T extends Object> extends BaseTypeHandler<T> {
    private final Class<T> clazz;

    public MybatisJsonTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        String json;
        try {
            json = JSON.toJSONString(parameter);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        ps.setString(i, json);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return fromStringToObject(json);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return fromStringToObject(json);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return fromStringToObject(json);
    }

    private T fromStringToObject(String str) throws SQLException {
        try {
            return JSON.parseObject(str, clazz);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
