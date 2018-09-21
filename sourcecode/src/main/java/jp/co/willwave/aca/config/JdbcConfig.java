package jp.co.willwave.aca.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:jdbc.properties")
@EnableTransactionManagement
public class JdbcConfig {
    private static final String MYSQL_URL = "jdbc:mysql://{server}:{port}/{schema}";
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private static final Logger logger = LoggerFactory.getLogger(JdbcConfig.class);

    @Value("${spring.datasource.schema-name}")
    private String schemaName;
    @Value("${spring.datasource.server}")
    private String server;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.maximum-pool-size}")
    private String maxPoolSize;
    @Value("${spring.datasource.db-type}")
    private String dbType;
    @Value("${spring.datasource.port}")
    private String port;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Bean
    public DataSource dataSource() {
        logger.debug("Create new hikari data source");
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(MYSQL_DRIVER);
        config.setJdbcUrl(createDbUrl());
        config.setUsername(username);
        config.setPassword(password);
        config.setAutoCommit(false);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.setMaximumPoolSize(Integer.parseInt(maxPoolSize));

        return new HikariDataSource(config);
    }


    private String createDbUrl() {
        logger.info("Create Database Url");
        String dbUrl = MYSQL_URL;
        dbUrl = dbUrl.replace("{server}", server)
                .replace("{port}", port)
                .replace("{schema}", schemaName);
        logger.info("dbUrl=" + dbUrl);

        return dbUrl;
    }

    @Bean
    public LocalSessionFactoryBean getSessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("jp.co.willwave.aca.model.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager getTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(getSessionFactory().getObject());
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    private Properties hibernateProperties() {
        return new Properties() {
            {
                setProperty("hibernate.hbm2ddl.auto", ddlAuto);
                setProperty("hibernate.connection.autocommit", "false");
                setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDB53Dialect");
                setProperty("hibernate.globally_quoted_identifiers", "true");
                setProperty("hibernate.show_sql", "false");
                setProperty("hibernate.c3p0.min_size", "3");
                setProperty("hibernate.c3p0.max_size", "5");
                setProperty("hibernate.c3p0.timeout", "1800");
                setProperty("hibernate.c3p0.idle_test_period", "100");
            }
        };
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(String maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
