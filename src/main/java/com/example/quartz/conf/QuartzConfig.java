package com.example.quartz.conf;

import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by shirukai on 2018/9/4
 */
@Configuration
public class QuartzConfig {
    @Autowired
    private JobFactory jobFactory;
    //租户ID
    @Value("${tenantId}")
    private String tenant_id;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
//    @Autowired
//    DataSource dataSource;

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }


    @Bean
    public DataSource quartzSource() throws IOException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        Properties properties = quartzProperties();
        dataSource.setDriverClassName(properties.getProperty("org.quartz.dataSource.myDS.driver"));
        dataSource.setUrl(properties.getProperty("org.quartz.dataSource.myDS.URL"));
        dataSource.setUsername(properties.getProperty("org.quartz.dataSource.myDS.user"));
        dataSource.setPassword(properties.getProperty("org.quartz.dataSource.myDS.password"));
        return dataSource;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        //初始化数据库
        //使用springboot的DataSource
        //initDataBase(dataSource);
        //使用自定义的DataSource
        initDataBase(quartzSource());
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        schedulerFactoryBean.setJobFactory(jobFactory);
        //通过租户ID来设置SchedulerName从而实现多租户
        schedulerFactoryBean.setSchedulerName(tenant_id);
        return schedulerFactoryBean;
    }

    public void initDataBase(DataSource dataSource) {
        log.info("============== init quartz database started ==============");
        try {
            //加载SQL
            ClassPathResource recordsSys = new ClassPathResource("quartz_tables.sql");
            //使用DataSourceInitializer初始化
            DataSourceInitializer dsi = new DataSourceInitializer();
            dsi.setDataSource(dataSource);
            dsi.setDatabasePopulator(new ResourceDatabasePopulator(true, true, "utf-8", recordsSys));
            dsi.setEnabled(true);
            dsi.afterPropertiesSet();
            log.info("============== init quartz database succeed ==============");
        } catch (Exception e) {
            log.error("init quartz database failed:{}", e.getMessage());
        }
    }

    // 创建schedule
    @Bean(name = "scheduler")
    public Scheduler scheduler() throws IOException {
        return schedulerFactoryBean().getScheduler();
    }
}
