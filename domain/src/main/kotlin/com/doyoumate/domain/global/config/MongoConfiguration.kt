package com.doyoumate.domain.global.config

import com.mongodb.ReadPreference
import com.mongodb.TransactionOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories("com.doyoumate.domain")
@Configuration
class MongoConfiguration {
    @Bean
    fun mongoTransactionManager(reactiveMongoDatabaseFactory: ReactiveMongoDatabaseFactory): ReactiveMongoTransactionManager =
        ReactiveMongoTransactionManager(reactiveMongoDatabaseFactory)
            .apply {
                setOptions(
                    TransactionOptions.builder()
                        .readPreference(ReadPreference.primary())
                        .build()
                )
            }
}
