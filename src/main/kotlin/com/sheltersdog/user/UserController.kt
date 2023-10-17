package com.sheltersdog.user

import com.sheltersdog.core.dto.JwtDto
import com.sheltersdog.user.dto.request.PutUserProfileRequest
import com.sheltersdog.user.dto.request.UserJoinRequest
import com.sheltersdog.user.dto.request.UserLoginRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController @Autowired constructor(
    val userService: UserService
) {

    @PostMapping("/public")
    suspend fun postUser(@RequestBody requestBody: UserJoinRequest): JwtDto {
        return userService.postUser(requestBody)
    }

    @PostMapping("/login/public")
    suspend fun login(@RequestBody requestBody: UserLoginRequest): JwtDto {
        return userService.login(requestBody)
    }

    @PutMapping("/profile")
    suspend fun putProfile(@RequestBody requestBody: PutUserProfileRequest) {
        userService.putProfile(requestBody)
    }


}