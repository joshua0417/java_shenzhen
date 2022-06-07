package com.cet.pq.pqgovernanceservice.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.cet.pq.pqgovernanceservice.model.common.Constants;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author ：gongtong
 * @ClassName DataDBConfig
 * @date ：Created in 2021/1/22 10:21
 * @description： data DB 连接
 */
@Configuration
@PropertySource(value = {
        Constants.LOAD_CONFIG_FILE_PATH + "application.yml"})
@MapperScan(basePackages = MatterhornDBConfig.PACKAGE, sqlSessionFactoryRef = "matterhornSqlSessionFactory")
public class MatterhornDBConfig {

    static final String PACKAGE = "com.cet.pq.pqgovernanceservice.mapper";
    static final String ORACLE_MAPPER_LOCATION = "classpath:mapper/oracle/*.xml";
    static final String PG_MAPPER_LOCATION = "classpath:mapper/pg/*.xml";

    @Value("${datasource.dbtype}")
    private String dbType;

    @Bean(name = "matterhornDataSource")
    @ConfigurationProperties(prefix = "datasource.druid.matterhorn")
    public DataSource matterhornDB() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "matterhornTransactionManager")
    public DataSourceTransactionManager dataTransactionManager() {
        return new DataSourceTransactionManager(matterhornDB());
    }

    @Bean(name = "matterhornSqlSessionFactory")
    public SqlSessionFactory dataSqlSessionFactory(@Qualifier("matterhornDataSource") DataSource dataDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataDataSource);
        if ("oracle".equals(dbType.toLowerCase())) {
            sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                    .getResources(MatterhornDBConfig.ORACLE_MAPPER_LOCATION));
        } else {
            sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                    .getResources(MatterhornDBConfig.PG_MAPPER_LOCATION));
        }
        SqlSessionFactory sqlSessionFactory = sessionFactory.getObject();
        sqlSessionFactory.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
        return sessionFactory.getObject();
    }

}
