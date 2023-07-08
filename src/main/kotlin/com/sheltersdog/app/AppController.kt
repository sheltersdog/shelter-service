package com.sheltersdog.app

import com.sheltersdog.app.dto.request.GetAppScreenDataRequest
import com.sheltersdog.app.dto.request.PostAppScreenDataRequest
import com.sheltersdog.app.dto.response.AppScreenDataDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/app")
class AppController @Autowired constructor(val appService: AppService) {

    @GetMapping("/screen/data")
    fun getScreenData(requestMono: Mono<GetAppScreenDataRequest>): Flux<AppScreenDataDto> {
        return requestMono.flatMapMany {
            appService.getScreenData(it)
        }
    }

    @PostMapping("/screen/data")
    fun postScreenData(@RequestBody requestMono: Mono<PostAppScreenDataRequest>): Mono<AppScreenDataDto> {
        return requestMono.flatMap {
            appService.postScreenData(it)
        }
    }


}