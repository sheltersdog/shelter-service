package com.sheltersdog.core.database

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.MongoDriverInformation
import com.mongodb.ServerAddress
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator


@Configuration
@EnableReactiveMongoRepositories
class MongoConfig @Autowired constructor(
    val mongoProperties: MongoProperties
): AbstractReactiveMongoConfiguration() {

    override fun getDatabaseName() = mongoProperties.database
    override fun reactiveMongoClient() = mongoClient()

    @Bean
    fun credential() = MongoCredential.createCredential(
        mongoProperties.username,
        mongoProperties.database,
        mongoProperties.password.toCharArray()
    )

    @Bean
    fun mongoClient() = MongoClients.create(
        MongoClientSettings.builder()
            .credential(credential())
            .applyToClusterSettings {
                it.hosts(
                    listOf(
                        ServerAddress(
                            mongoProperties.host,
                            mongoProperties.port
                        )
                    )
                )
            }
            .build(),
        MongoDriverInformation.builder()
            .build()
    )

    @Bean
    override fun reactiveMongoTemplate(
        databaseFactory: ReactiveMongoDatabaseFactory,
        mongoConverter: MappingMongoConverter
    ): ReactiveMongoTemplate = ReactiveMongoTemplate(mongoClient(), mongoProperties.database)


    @Bean
    fun transactionalOperator(reactiveMongoTransactionManager: ReactiveTransactionManager): TransactionalOperator? {
        return TransactionalOperator.create(reactiveMongoTransactionManager)
    }

    @Bean
    fun reactiveMongoTransactionManager(
        databaseFactory: ReactiveMongoDatabaseFactory,
    ): ReactiveMongoTransactionManager {
        return ReactiveMongoTransactionManager(databaseFactory)
    }
}