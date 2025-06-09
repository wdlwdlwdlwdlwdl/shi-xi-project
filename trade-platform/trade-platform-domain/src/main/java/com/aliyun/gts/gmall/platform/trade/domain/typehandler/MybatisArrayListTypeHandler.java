package com.aliyun.gts.gmall.platform.trade.domain.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * chaoyin
 * @param <T>
 */
@MappedJdbcTypes(value = {JdbcType.VARCHAR},includeNullJdbcType = true)
@MappedTypes({List.class})
public class MybatisArrayListTypeHandler<T extends Object> extends BaseTypeHandler<List<T>> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Class<List<T>> clazz;
// 不指定JavaType则需要无参构造方法
//    public ArrayListTypeHandler() {}

    public MybatisArrayListTypeHandler(Class<List<T>> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
//        ps.setString(i,JSON.toJSONString(parameter));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
//特别要注意在转集合的情况下，第二个参数是List.class,而不是clazz会报奇怪的错误
        return toObject(rs.getString(columnName), List.class);
//        return (List<T>)JSONObject.parseArray(rs.getString(columnName),List.class);
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toObject(rs.getString(columnIndex), List.class);
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toObject(cs.getString(columnIndex), List.class);
    }

    private String toJson(List<T> object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<T> toObject(String content, Class<?> clazz) {
        if (content != null && !content.isEmpty()) {
            try {
                return (List<T>) MAPPER.readValue(content, clazz);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

}
