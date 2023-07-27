package com.sheltersdog.core.util

import com.sheltersdog.core.model.FileType
import org.apache.commons.io.FilenameUtils
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart

fun fileTypeCheck(type: FileType, file: FilePart): Boolean {
    return when (file.headers().contentType) {
        MediaType.IMAGE_JPEG,
        MediaType.IMAGE_PNG,
        -> type == FileType.IMAGE

        MediaType.valueOf("video/mp4")
        -> type == FileType.VIDEO

        else -> false
    }
}

fun getFileExtension(file: FilePart): String {
    val extension = FilenameUtils.getExtension(file.filename())
    if (extension.isNotBlank()) return extension

    return when (file.headers().contentType) {
        MediaType.IMAGE_JPEG -> "jpeg"
        MediaType.IMAGE_PNG -> "png"
        MediaType.valueOf("image/jpg") -> "jpg"
        MediaType.valueOf("video/mp4") -> "mp4"
        else -> ""
    }

}
