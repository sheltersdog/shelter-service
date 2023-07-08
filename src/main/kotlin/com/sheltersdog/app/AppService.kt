package com.sheltersdog.app

import com.sheltersdog.app.dto.request.GetAppScreenDataRequest
import com.sheltersdog.app.dto.request.PostAppScreenDataRequest
import com.sheltersdog.app.dto.response.AppScreenDataDto
import com.sheltersdog.app.entity.AppScreenData
import com.sheltersdog.app.repository.AppRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AppService @Autowired constructor(
    val appRepository: AppRepository
) {

    fun getScreenData(requestBody: GetAppScreenDataRequest): Flux<AppScreenDataDto> {
        return appRepository.getScreenData(requestBody).map {
            AppScreenDataDto(
                id = it.id.toString(),
                title = it.title,
                page = it.page,
                content = it.content,
                platformTypes = it.platformTypes,
                dataType = it.dataType,
            )
        }
    }

    fun postScreenData(requestBody: PostAppScreenDataRequest): Mono<AppScreenDataDto> {
        val entity = AppScreenData(
            title = requestBody.title,
            page = requestBody.page,
            content = requestBody.content,
            platformTypes = requestBody.platformTypes,
            dataType = requestBody.dataType,
        )

        return appRepository.postScreenData(entity).map {
            AppScreenDataDto(
                id = it.id.toString(),
                title = it.title,
                page = it.page,
                content = it.content,
                platformTypes = requestBody.platformTypes,
                dataType = requestBody.dataType,
            )
        }
    }
}