package com.sheltersdog.core.database

import com.mongodb.*
import com.mongodb.reactivestreams.client.MongoClients
import com.sheltersdog.core.properties.ActiveProperties
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
import java.util.concurrent.TimeUnit


@Configuration
@EnableReactiveMongoRepositories
class MongoConfig @Autowired constructor(
    val mongoProperties: MongoProperties,
    val activeProperties: ActiveProperties,
) : AbstractReactiveMongoConfiguration() {

    override fun getDatabaseName() = mongoProperties.database
    override fun reactiveMongoClient() = mongoClient()

    @Bean
    fun credential() = MongoCredential.createCredential(
        mongoProperties.username,
        mongoProperties.database,
        mongoProperties.password.toCharArray()
    ).withMechanism(AuthenticationMechanism.SCRAM_SHA_1)

    @Bean
    fun mongoClient() = MongoClients.create(
        MongoClientSettings.builder()
            .credential(credential())
            .retryWrites(true)
            .readConcern(ReadConcern.DEFAULT)
            .writeConcern(WriteConcern.MAJORITY)
            .readPreference(ReadPreference.primary())
            .serverApi(
                ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build()
            )
            .applyToSslSettings { it.enabled(true) }
            .applyToConnectionPoolSettings { it.maxConnectionIdleTime(1, TimeUnit.MINUTES) }
            .applyToClusterSettings { it.applyConnectionString(ConnectionString(mongoProperties.uri)) }
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