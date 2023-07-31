package com.sheltersdog.shelter

import com.sheltersdog.shelter.dto.request.PostShelterRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
@RequestMapping("/shelter")
class ShelterController @Autowired constructor(
    val shelterService: ShelterService
) {

    @PostMapping
    fun postShelter(
        @RequestBody requestBody: PostShelterRequest
    ): Mono<Void> {
        return shelterService.postShelter(
            requestBody,
            (SecurityContextHolder.getContext().authentication.principal as User).username
        )
    }


}