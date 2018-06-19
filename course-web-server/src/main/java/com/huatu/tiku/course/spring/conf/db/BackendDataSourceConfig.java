package com.huatu.tiku.course.spring.conf.db;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 引用 backendDataSource 配置文件信息
 * 此处的配置文件 引用 自定义组件-druid 中的配置方式，该配置为主库
 * Created by lijun on 2018/6/19
 */
@Configuration
public class BackendDataSourceConfig {


    @Primary
    @Bean(name = "backendSqlSessionFactory")
    public SqlSessionFactory backendSqlSessionFactory(
            @Qualifier("dataSource") DataSource dataSource
    ) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/course/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "backendTransactionManager")
    public DataSourceTransactionManager backendTransactionManager(
            @Qualifier("dataSource") DataSource dataSource
    ) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Primary
    @Bean(name = "backendSqlSessionTemplate")
    public SqlSessionTemplate backendSqlSessionTemplate(
            @Qualifier("backendSqlSessionFactory") SqlSessionFactory sqlSessionFactory
    ) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
