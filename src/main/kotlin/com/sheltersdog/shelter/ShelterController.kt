package com.sheltersdog.shelter

import com.sheltersdog.shelter.dto.request.PostShelterRequest
import com.sheltersdog.shelter.dto.response.ShelterDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
            requestBody,
            (SecurityContextHolder.getContext().authentication.principal as User).username
        )
    }


}