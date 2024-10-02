package com.morningstar.config;

import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
// 仅需指定仓库路径，无需禁用默认事务
@EnableNeo4jRepositories(basePackages = "com.morningstar.*.repository", transactionManagerRef = "neo4jTransactionManager")
public class TransactionConfig {

    @Bean(name = "transactionManager")
    @Primary // 标记为默认事务管理器，@Transactional 无指定时使用
    public PlatformTransactionManager mysqlTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // ========== Neo4j 事务管理器 ==========
    // 注入 DatabaseSelectionProvider，确保与 Neo4jClient 使用相同的数据库选择策略
    // 避免 "There is already an ongoing Spring transaction for the default database,
    // but you requested 'neo4j'" 的冲突（由 spring.data.neo4j.database 配置引起）
    @Bean(name = "neo4jTransactionManager")
    public Neo4jTransactionManager neo4jTransactionManager(Driver driver, DatabaseSelectionProvider databaseSelectionProvider) {
        return new Neo4jTransactionManager(driver, databaseSelectionProvider);
    }
}