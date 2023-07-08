package com.sheltersdog.image

import com.sheltersdog.core.model.FileType
import com.sheltersdog.core.util.fileTypeCheck
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/image")
class ImageController @Autowired constructor(
    val imageService: ImageService,
) {
    @PostMapping
    fun upload(
        @RequestPart("file") requestPart: Mono<FilePart>
    ): Mono<String> {
        return requestPart
            .flatMap {
                if (!fileTypeCheck(FileType.IMAGE, it)) {
                    return@flatMap Mono.just("Error: ${it.filename()}")
                }
                imageService.upload(it)
            }
    }

    @PostMapping("/list")
    fun uploadAll(@RequestPart("files") requestPart: Flux<FilePart>): Flux<String> {
        return requestPart
            .flatMap {
                if (!fileTypeCheck(FileType.IMAGE, it)) {
                    return@flatMap Flux.just("Error: ${it.filename()}")
                }
                imageService.upload(it)
            }
    }
}