package com.sheltersdog.image

import com.sheltersdog.core.aws.AwsProperties
import com.sheltersdog.core.aws.S3MetadataGenerator
import com.sheltersdog.core.aws.S3Uploader
import com.sheltersdog.core.util.resizeImage
import com.sheltersdog.image.entity.Image
import com.sheltersdog.image.entity.model.ImageStatus
import com.sheltersdog.image.entity.model.ImageType
import com.sheltersdog.image.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.apache.commons.imaging.Imaging
import org.apache.commons.io.FilenameUtils
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.io.File
import java.nio.file.Files
import java.util.*

@Service
class ImageService @Autowired constructor(
    val s3Uploader: S3Uploader,
    val s3MetadataGenerator: S3MetadataGenerator,
    val imageRepository: ImageRepository,
    val awsProperties: AwsProperties,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    suspend fun upload(filePart: FilePart): String {
        val extension = FilenameUtils.getExtension(filePart.filename())
        val originFilename = FilenameUtils.getBaseName(filePart.filename())

        val uuid = UUID.randomUUID().toString()

        val image = imageRepository.save(
            Image(
                type = ImageType.WEB,
                status = ImageStatus.ACTIVE,
            )
        ).awaitLast()

        val file = withContext(Dispatchers.IO) {
            Files.createTempFile(
                uuid.substring(0, 8), ".${extension}"
            )
        }.toFile()
        filePart.transferTo(file).awaitFirstOrNull()
        imageSaveS3(
            file = file,
            filename = uuid,
            originFilename = originFilename,
            extension = extension,
            key = image.id!!,
        )

        val resizeFile = withContext(Dispatchers.IO) {
            Files.createTempFile(
                uuid.substring(0, 9), ".${extension}"
            )
        }.toFile()
        var thumbFilename: String? = null
        try {
            val putObjectResponse = imageResizeAndSaveS3(
                file = file,
                resizeFile = resizeFile,
                extension = extension,
                filename = uuid,
                originFilename = originFilename,
                key = image.id,
            )
            thumbFilename = if (putObjectResponse.bucketKeyEnabled() == null) {
                "thumb_${uuid}.${extension}"
            } else {
                ""
            }
        } catch (e: Exception) { /* do nothing */
        } finally {
            resizeFile.delete()
        }

        val updatedImage = updateImageEntity(
            file = file,
            key = image.id,
            filename = "${uuid}.${extension}",
            thumbFilename = thumbFilename
        )
        file.delete()

        return updatedImage.url
    }

    private suspend fun imageSaveS3(
        file: File,
        filename: String,
        originFilename: String,
        extension: String,
        key: ObjectId,
    ): PutObjectResponse {
        val metadata = s3MetadataGenerator.generateImageMetadata(
            file = file,
            filename = "${filename}.${extension}",
            originFilename = originFilename,
        )
        return s3Uploader.uploadObject(
            file = file,
            filename = "${filename}.${extension}",
            key = key.toString(),
            metadata = metadata
        ).awaitSingle()
    }

    private suspend fun imageResizeAndSaveS3(
        file: File,
        resizeFile: File,
        extension: String,
        filename: String,
        originFilename: String,
        key: ObjectId,
    ): PutObjectResponse {
        resizeImage(
            file = file,
            resizeFile = resizeFile,
            extension = extension,
        )
        val metadata = s3MetadataGenerator.generateImageMetadata(
            file = resizeFile,
            filename = "thumb_${filename}.${extension}",
            originFilename = originFilename,
        )
        return s3Uploader.uploadObject(
            file = file,
            filename = "thumb_${filename}.${extension}",
            key = key.toString(),
            metadata = metadata
        ).awaitSingle()
    }

    private suspend fun updateImageEntity(
        file: File,
        key: ObjectId,
        filename: String,
        thumbFilename: String? = null,
    ): Image {
        val imageInfo = Imaging.getImageInfo(file)
        val image = Image(
            id = key,
            type = ImageType.USER_PROFILE,
            url = "${awsProperties.cloudFrontUrl}${key}/${thumbFilename ?: filename}",
            filename = filename,
            resizeFilename = thumbFilename,
            status = ImageStatus.ACTIVE,
            width = imageInfo.width,
            height = imageInfo.height,
            size = file.length(),
        )
        return imageRepository.save(image).awaitSingle()
    }

}