package com.sheltersdog.foreverdog

import com.sheltersdog.foreverdog.dto.request.GetForeverdogsRequest
import com.sheltersdog.foreverdog.dto.request.PostForeverdogRequest
import com.sheltersdog.foreverdog.dto.response.ForeverdogDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/foreverdog")
class ForeverdogController @Autowired constructor(
    private val foreverdogService: ForeverdogService,
){

    @PostMapping
    fun postForeverdog(@RequestBody requestBody: Mono<PostForeverdogRequest>): Mono<ForeverdogDto> {
        return requestBody.flatMap(foreverdogService::postForeverdog)
    }

    @GetMapping("/list")
    fun getForeverdogs(requestParam: Mono<GetForeverdogsRequest>): Mono<List<ForeverdogDto>> {
        return requestParam.flatMap(foreverdogService::getForeverdogs)
    }






}