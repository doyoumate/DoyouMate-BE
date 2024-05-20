package com.doyoumate.batch.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class H2Configuration {
    @Bean
    fun dataSource(): DataSource =
        EmbeddedDatabaseBuilder()
            .apply { setType(EmbeddedDatabaseType.H2) }
            .build()

    @Bean
    fun h2TransactionManager(dataSource: DataSource): PlatformTransactionManager =
        DataSourceTransactionManager(dataSource)
}
