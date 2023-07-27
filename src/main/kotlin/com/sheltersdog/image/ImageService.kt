package com.sheltersdog.image

import com.sheltersdog.core.aws.AwsProperties
import com.sheltersdog.core.aws.S3MetadataGenerator
import com.sheltersdog.core.aws.S3Uploader
import com.sheltersdog.core.util.resizeImage
import com.sheltersdog.image.entity.Image
import com.sheltersdog.image.entity.model.ImageStatus
import com.sheltersdog.image.entity.model.ImageType
import com.sheltersdog.image.repository.ImageRepository
import org.apache.commons.imaging.Imaging
import org.apache.commons.io.FilenameUtils
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.*

@Service
class ImageService @Autowired constructor(
    val s3Uploader: S3Uploader,
    val s3MetadataGenerator: S3MetadataGenerator,
    val imageRepository: ImageRepository,
    val awsProperties: AwsProperties,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun upload(filePart: FilePart): Mono<String> {
        var key: ObjectId = ObjectId.get()

        val extension = FilenameUtils.getExtension(filePart.filename())
        val filename = FilenameUtils.getBaseName(filePart.filename())

        val uuid = UUID.randomUUID().toString()
        val file = Files.createTempFile(
            uuid.substring(0, 8), ".${extension}"
        ).toFile()
        val resizeFile = Files.createTempFile(
            uuid.substring(0, 9), ".${extension}"
        ).toFile()

        return imageRepository.save(
            Image(
                type = ImageType.USER_PROFILE,
                status = ImageStatus.NOT_CONNECTED
            )
        ).onErrorResume { error ->
            Mono.defer {
                log.error("ImageRepository Save Error", error)
                Mono.empty()
            }
        }.flatMap { image ->
            key = image.id!!
            filePart.transferTo(file).onErrorResume { error ->
                Mono.defer {
                    log.error("FilePart to File Error!!", error)
                    imageRepository.deleteById(key).mapNotNull { null }
                }
            }.thenReturn(true)
        }.flatMap {
            imageSaveS3(file, filename, extension, key)
        }.flatMap {
            imageResizeAndSaveS3(file, resizeFile, extension, filename, key)
        }.flatMap { putObjectResponse ->
            val thumbFilename = if (putObjectResponse.bucketKeyEnabled() == null) {
                "thumb_${filename}.${extension}"
            } else {
                ""
            }

            updateImageEntity(file, key, filename, extension, thumbFilename)
        }.doFinally {
            file.delete()
            resizeFile.delete()
        }
    }

    private fun imageSaveS3(
        file: File,
        filename: String,
        extension: String,
        key: ObjectId
    ): Mono<PutObjectResponse> {
        val metadata = s3MetadataGenerator.generateImageMetadata(
            file, "${filename}.${extension}"
        )
        return s3Uploader.uploadObject(
            file = file,
            filename = "${filename}.${extension}",
            key = key.toString(),
            metadata = metadata
        ).onErrorResume { error ->
            Mono.defer {
                log.error("S3 fileUpload Fail...", error)
                imageRepository.deleteById(key).mapNotNull { null }
            }
        }
    }

    private fun imageResizeAndSaveS3(
        file: File,
        resizeFile: File,
        extension: String,
        filename: String,
        key: ObjectId
    ): Mono<PutObjectResponse> {
        resizeImage(
            file = file,
            resizeFile = resizeFile,
            extension = extension,
        )
        val metadata = s3MetadataGenerator.generateImageMetadata(
            resizeFile, "thumb_${filename}.${extension}"
        )
        return s3Uploader.uploadObject(
            file = file,
            filename = "thumb_${filename}.${extension}",
            key = key.toString(),
            metadata = metadata
        ).onErrorResume { error ->
            log.info("S3 resize image fileUpload Fail Error!!")
            Mono.defer {
                log.error("S3 resize image fileUpload Fail...", error)
                Mono.just(PutObjectResponse.builder().build())
            }
        }
    }

    private fun updateImageEntity(
        file: File,
        key: ObjectId,
        filename: String,
        extension: String,
        thumbFilename: String
    ): Mono<String> {
        val imageInfo = Imaging.getImageInfo(file)
        return imageRepository.save(
            Image(
                id = key,
                type = ImageType.USER_PROFILE,
                url = "${awsProperties.cloudFrontUrl}${key}/${filename}.${extension}",
                filename = "${filename}.${extension}",
                resizeFilename = thumbFilename,
                status = ImageStatus.ACTIVE,
                width = imageInfo.width,
                height = imageInfo.height,
                size = file.length(),
                regDate = LocalDateTime.now()
            )
        ).onErrorResume { error ->
            Mono.defer {
                log.error("update file info fail..", error)
                imageRepository.deleteById(key).mapNotNull { null }
            }
        }.map { image -> image.id.toString() }
    }

}