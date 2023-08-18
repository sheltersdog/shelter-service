package com.sheltersdog.foreverdog

import com.sheltersdog.core.util.notStringThrow
import com.sheltersdog.foreverdog.dto.request.GetForeverdogsRequest
import com.sheltersdog.foreverdog.dto.request.PostForeverdogRequest
import com.sheltersdog.foreverdog.dto.response.ForeverdogDto
import com.sheltersdog.foreverdog.entity.model.ForeverdogStatus
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

    @PutMapping("/status")
    suspend fun putForeverdogStatus(@RequestBody requestBody: Map<String, Any>): ForeverdogDto {
        val foreverdogId = requestBody.notStringThrow("foreverdogId")
        val status = ForeverdogStatus.of(
            requestBody.notStringThrow("status")
        )

        return foreverdogService.putForeverdogStatus(
            foreverdogId = foreverdogId,
            status = status,
        )
    }


}