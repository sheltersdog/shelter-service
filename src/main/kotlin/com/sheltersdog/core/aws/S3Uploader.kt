package com.sheltersdog.core.aws

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.io.File
import java.util.*

@Component
class S3Uploader @Autowired constructor(
    val awsProperties: AwsProperties,
    val s3AsyncClient: S3AsyncClient
) {

    fun uploadObject(
        file: File,
        filename: String = "",
        key: String,
        metadata: Map<String, String> = mapOf(),
    ): Mono<PutObjectResponse> {

        return uploadObject(
            file = file,
            filename = filename,
            key = key,
            bucket = awsProperties.bucket,
            metadata = metadata)
    }

    fun uploadObject(
        file: File,
        filename: String = "",
        key: String,
        bucket: String,
        metadata: Map<String, String> = mapOf(),
    ): Mono<PutObjectResponse> {
        val key = if (filename.isNotBlank()) {
            "$key/${filename}"
        } else {
            "$key/${file.name}"
        }

        return s3AsyncClient.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .contentLength(file.length())
                .key(key)
                .metadata(metadata)
                .build(),
            AsyncRequestBody.fromFile(file)
        ).toMono()
    }


}