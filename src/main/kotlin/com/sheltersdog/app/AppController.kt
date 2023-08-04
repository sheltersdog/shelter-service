package com.sheltersdog.app

import com.sheltersdog.app.dto.request.GetAppScreenDataRequest
import com.sheltersdog.app.dto.request.PostAppScreenDataRequest
import com.sheltersdog.app.dto.response.AppScreenDataDto
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/app")
class AppController @Autowired constructor(
    val appService: AppService,
) {

    @GetMapping("/screen/data")
    suspend fun getScreenData(requestParam: GetAppScreenDataRequest): Flow<AppScreenDataDto> {
        return appService.getScreenData(requestParam)
    }

    @PostMapping("/screen/data")
    suspend fun postScreenData(@RequestBody requestBody: PostAppScreenDataRequest): AppScreenDataDto {
        return appService.postScreenData(requestBody)
    }

//    @GetMapping("/event/test")
//    fun eventTest(@RequestParam name: String, @RequestParam content: String) {
//
//    }


}