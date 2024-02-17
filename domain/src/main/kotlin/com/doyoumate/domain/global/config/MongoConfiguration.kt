package com.doyoumate.domain.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@EnableReactiveMongoRepositories("com.doyoumate.domain")
@Configuration
class MongoConfiguration
