package com.sheltersdog.core.aws

import org.apache.commons.imaging.Imaging
import org.springframework.stereotype.Component
import java.io.File

@Component
class S3MetadataGenerator {

    fun generateImageMetadata(
        file: File,
        filename: String): Map<String, String> {
        val imageInfo = Imaging.getImageInfo(file)

        return mapOf(
            Pair("filename", filename),
            Pair("width", imageInfo.width.toString()),
            Pair("height", imageInfo.height.toString()),
        )
    }
}