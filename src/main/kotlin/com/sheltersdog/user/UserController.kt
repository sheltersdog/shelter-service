package com.sheltersdog.user

import com.sheltersdog.core.dto.JwtDto
import com.sheltersdog.user.dto.request.UserJoinRequest
import com.sheltersdog.user.dto.request.UserLoginRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/public/user")
class UserController @Autowired constructor(
    val userService: UserService
) {

    @PostMapping
    fun postUser(@RequestBody requestBody: UserJoinRequest): Mono<JwtDto> {
        return userService.postUser(requestBody)
    }

    @PostMapping("/login")
    fun login(@RequestBody requestBody: UserLoginRequest): Mono<JwtDto> {
        return userService.login(requestBody)
    }


}