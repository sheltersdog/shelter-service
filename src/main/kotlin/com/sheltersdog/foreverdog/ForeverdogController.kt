package com.sheltersdog.foreverdog

import com.sheltersdog.foreverdog.dto.request.GetForeverdogsRequest
import com.sheltersdog.foreverdog.dto.request.PostForeverdogRequest
import com.sheltersdog.foreverdog.dto.response.ForeverdogDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/foreverdog")
class ForeverdogController @Autowired constructor(
    private val foreverdogService: ForeverdogService,
) {

    @PostMapping
    suspend fun postForeverdog(@RequestBody requestBody: PostForeverdogRequest): ForeverdogDto {
        return foreverdogService.postForeverdog(requestBody)
    }

    @GetMapping("/list")
    suspend fun getForeverdogs(requestParam: GetForeverdogsRequest): List<ForeverdogDto> {
        return foreverdogService.getForeverdogs(requestParam)
    }


}