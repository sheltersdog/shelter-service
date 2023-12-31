package com.sheltersdog.image

import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.model.FileType
import com.sheltersdog.core.util.typeCheck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/image")
class ImageController @Autowired constructor(
    val imageService: ImageService,
) {
    @PostMapping
    suspend fun upload(
        @RequestPart("file") requestPart: FilePart
    ): String {
        if (!requestPart.typeCheck(FileType.IMAGE)) {
            throw SheltersdogException(
                type = ExceptionType.FILE_TYPE_WRONG,
                variables = mapOf(
                    "filename" to requestPart.filename(),
                    "EnableType" to "JPG, JPEG, PNG",
                ),
            )
        }
        return imageService.upload(requestPart)
    }

    @PostMapping("/list")
    suspend fun uploadAll(@RequestPart("files") requestParts: Flow<FilePart>): Flow<String> {
        return requestParts.map { file ->
            if (!file.typeCheck(FileType.IMAGE)) {
                throw SheltersdogException(
                    type = ExceptionType.FILE_TYPE_WRONG,
                    variables = mapOf(
                        "filename" to file.filename(),
                        "EnableType" to "JPG, JPEG, PNG",
                    ),
                )
            }
            imageService.upload(file)
        }
    }
}