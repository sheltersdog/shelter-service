package com.sheltersdog.image

import com.sheltersdog.core.aws.S3MetadataGenerator
import com.sheltersdog.core.aws.S3Uploader
import com.sheltersdog.core.util.resizeImage
import com.sheltersdog.image.entity.Image
import com.sheltersdog.image.entity.model.ImageStatus
import com.sheltersdog.image.entity.model.ImageType
import com.sheltersdog.image.repository.ImageRepository
import org.apache.commons.io.FilenameUtils
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime

@Service
class ImageService @Autowired constructor(
    val s3Uploader: S3Uploader,
    val s3MetadataGenerator: S3MetadataGenerator,
    val imageRepository: ImageRepository
) {
    fun upload(filePart: FilePart): Mono<String> {
        val extension = FilenameUtils.getExtension(filePart.filename())
        val filename = FilenameUtils.getBaseName(filePart.filename())
        var key: ObjectId? = null

        val file = tempFile(filename, ".${extension}")
        val resizeFile = tempFile("thumb_${filename}", ".${extension}")

        return filePart.transferTo(file).thenReturn(0)
            .flatMap {
                imageRepository.save(
                    Image(
                        type = ImageType.USER_PROFILE,
                        filename = file.name,
                        resizeFilename = resizeFile.name,
                        status = ImageStatus.NOT_CONNECTED,
                        regDate = LocalDateTime.now()
                    )
                )
            }
            .flatMap {
                key = it.id
                val metadata = s3MetadataGenerator.generateImageMetadata(
                    file, filename
                )
                s3Uploader.uploadObject(file, key.toString(), metadata)
            }.flatMap {
                resizeImage(
                    file, resizeFile, extension
                )
                val metadata = s3MetadataGenerator.generateImageMetadata(
                    resizeFile, filename
                )
                s3Uploader.uploadObject(resizeFile, key.toString(), metadata)
            }.map {
                "Upload Success: $key"
            }.doFinally {
                file.delete()
                resizeFile.delete()
            }
    }

    fun tempFile(filename: String, extension: String): File {
        return Files.createTempFile(filename, extension).toFile()
    }

}