package com.ruoyi.common.config.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * BigDecimal类型处理器
 * 避免科学计数法显示问题（0E-8、1E+6等）
 *
 * @author HayDen
 */
public class BigDecimalTypeHandler extends BaseTypeHandler<BigDecimal> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BigDecimal parameter, JdbcType jdbcType) throws SQLException {
        ps.setBigDecimal(i, parameter);
    }

    @Override
    public BigDecimal getNullableResult(ResultSet rs, String columnName) throws SQLException {
        BigDecimal result = rs.getBigDecimal(columnName);
        return convertToPlainString(result);
    }

    @Override
    public BigDecimal getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        BigDecimal result = rs.getBigDecimal(columnIndex);
        return convertToPlainString(result);
    }

    @Override
    public BigDecimal getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        BigDecimal result = cs.getBigDecimal(columnIndex);
        return convertToPlainString(result);
    }

    /**
     * 将BigDecimal转换为普通字符串格式，避免科学计数法
     */
    private BigDecimal convertToPlainString(BigDecimal value) {
        if (value == null) {
            return null;
        }
        
        String originalStr = value.toString();
        // 检查是否为科学计数法格式
        if (originalStr.contains("E") || originalStr.contains("e")) {
            // 使用toPlainString()获取普通字符串，然后重新构造
            String plainStr = value.toPlainString();
            return new BigDecimal(plainStr);
        }
        
        return value;
    }
}
