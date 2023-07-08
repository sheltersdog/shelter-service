package com.sheltersdog.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController @Autowired constructor(
    val userService: UserService
) {

}