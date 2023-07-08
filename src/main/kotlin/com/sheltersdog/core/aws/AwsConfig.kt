package com.sheltersdog.core.aws

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI
import java.time.Duration

@Configuration
class AwsConfig @Autowired constructor(val awsProperties: AwsProperties) {

    @Bean
    fun s3AsyncClient(awsCredentialsProvider: AwsCredentialsProvider?): S3AsyncClient? {
        val s3AsyncClientBuilder = S3AsyncClient.builder()
            .httpClient(sdkAsyncHttpClient())
            .region(Region.of(awsProperties.region))
            .credentialsProvider(awsCredentialsProvider)
            .forcePathStyle(true)
            .serviceConfiguration(s3Configuration())

//        if (awsProperties.endPoint.isNotBlank()) {
//            s3AsyncClientBuilder.endpointOverride(
//                URI.create(awsProperties.endPoint)
//            )
//        }

        return s3AsyncClientBuilder.build()
    }

    private fun sdkAsyncHttpClient(): SdkAsyncHttpClient? {
        return NettyNioAsyncHttpClient.builder()
            .writeTimeout(Duration.ZERO)
            .maxConcurrency(64)
            .build()
    }

    private fun s3Configuration(): S3Configuration? {
        return S3Configuration.builder()
            .checksumValidationEnabled(false)
            .chunkedEncodingEnabled(true)
            .build()
    }

    @Bean
    fun awsCredentialsProvider(): AwsCredentialsProvider? {
        return AwsCredentialsProvider {
            AwsBasicCredentials.create(
                awsProperties.accessKey,
                awsProperties.secretKey
            )
        }
    }
}