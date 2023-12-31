package com.sheltersdog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
class ShelterServiceApplication

//https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongodb-template-query.criteria
fun main(args: Array<String>) {
    runApplication<ShelterServiceApplication>(*args)
}
