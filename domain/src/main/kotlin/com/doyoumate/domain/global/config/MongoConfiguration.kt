package com.doyoumate.domain.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories("com.doyoumate.domain")
@Configuration
class MongoConfiguration
