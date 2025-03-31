package com.study.mybatisplus.config;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            // 注册自定义类型处理器
            configuration.getTypeHandlerRegistry().register(Boolean.class, new BooleanTypeHandler());
            configuration.getTypeHandlerRegistry().register(boolean.class, new BooleanTypeHandler());
        };
    }

    /**
     * MySQL中tinyint(1)与Boolean的转换处理器
     */
    public static class BooleanTypeHandler implements TypeHandler<Boolean> {
        @Override
        public void setParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
            ps.setInt(i, parameter != null && parameter ? 1 : 0);
        }

        @Override
        public Boolean getResult(ResultSet rs, String columnName) throws SQLException {
            int value = rs.getInt(columnName);
            return value == 1;
        }

        @Override
        public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
            int value = rs.getInt(columnIndex);
            return value == 1;
        }

        @Override
        public Boolean getResult(CallableStatement cs, int columnIndex) throws SQLException {
            int value = cs.getInt(columnIndex);
            return value == 1;
        }
    }
}