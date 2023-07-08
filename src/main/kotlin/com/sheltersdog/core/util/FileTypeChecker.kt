package com.sheltersdog.core.util

import com.sheltersdog.core.model.FileType
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
