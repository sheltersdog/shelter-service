package com.sheltersdog.shelter

import com.sheltersdog.shelter.dto.request.GetShelterListRequest
import com.sheltersdog.shelter.dto.request.PostShelterRequest
import com.sheltersdog.shelter.dto.request.PutShelterRequest
import com.sheltersdog.shelter.dto.response.ShelterDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/shelter")
class ShelterController @Autowired constructor(
    val shelterService: ShelterService
) {

    @PostMapping
    suspend fun postShelter(
        @RequestBody requestBody: PostShelterRequest
    ): ShelterDto {
        return shelterService.postShelter(
            requestBody
        )
    }

    @GetMapping
    suspend fun getShelter(
        @RequestParam id: String): ShelterDto {
        return shelterService.getShelter(id)
    }

    @GetMapping("/list")
    suspend fun getShelterList(
        requestParam: GetShelterListRequest,
    ): List<ShelterDto> {
        return shelterService.getShelterList(requestParam)
    }

    @PutMapping
    suspend fun putShelter(
        @RequestBody requestBody: PutShelterRequest
    ): ShelterDto? {
        return shelterService.putShelter(requestBody)
    }


}