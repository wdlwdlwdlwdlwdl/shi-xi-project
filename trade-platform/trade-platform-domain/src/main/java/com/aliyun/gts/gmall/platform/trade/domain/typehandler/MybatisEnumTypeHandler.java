package com.aliyun.gts.gmall.platform.trade.domain.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsCompanyTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

/**
 * chaoyin
 */
@MappedTypes({
    LogisticsCompanyTypeEnum.class,
    LogisticsTypeEnum.class,
    OrderStatusEnum.class,
    PrimaryOrderFlagEnum.class
})
public class MybatisEnumTypeHandler<E extends GenericEnum> extends BaseTypeHandler<E> {

    private final Class<E> type;
    private final E[] enums;

    public MybatisEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        this.enums = type.getEnumConstants();
        if (this.enums == null) {
            throw new IllegalArgumentException(type.getSimpleName()
                + " does not represent an enum type.");
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return fromCodeToEnum(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return fromCodeToEnum(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return fromCodeToEnum(value);
    }

    private E fromCodeToEnum(int code) {
        for (E enm : enums) {
            if (code == enm.getCode()) {
                return enm;
            }
        }
        throw new IllegalArgumentException("Cannot convert "
            + code + " to " + type.getSimpleName());
    }

}
