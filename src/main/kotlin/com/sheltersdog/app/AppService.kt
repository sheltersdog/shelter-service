package com.sheltersdog.app

import com.sheltersdog.app.dto.request.GetAppScreenDataRequest
import com.sheltersdog.app.dto.request.PostAppScreenDataRequest
import com.sheltersdog.app.dto.response.AppScreenDataDto
import com.sheltersdog.app.entity.AppScreenData
import com.sheltersdog.app.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AppService @Autowired constructor(
    val appRepository: AppRepository
) {

    suspend fun getScreenData(requestBody: GetAppScreenDataRequest): Flow<AppScreenDataDto> {
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

    suspend fun postScreenData(requestBody: PostAppScreenDataRequest): AppScreenDataDto {
        val entity = AppScreenData(
            title = requestBody.title,
            page = requestBody.page,
            content = requestBody.content,
            platformTypes = requestBody.platformTypes,
            dataType = requestBody.dataType,
        )

        val screenData = appRepository.postScreenData(entity)
        return AppScreenDataDto(
            id = screenData.id.toString(),
            title = screenData.title,
            page = screenData.page,
            content = screenData.content,
            platformTypes = requestBody.platformTypes,
            dataType = requestBody.dataType,
        )
    }
}