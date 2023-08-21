package com.sheltersdog.foreverdog

import com.sheltersdog.core.event.EventBus
import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.util.ifUpdateFailThrow
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.foreverdog.dto.request.GetForeverdogsRequest
import com.sheltersdog.foreverdog.dto.request.PostForeverdogRequest
import com.sheltersdog.foreverdog.dto.response.ForeverdogDto
import com.sheltersdog.foreverdog.entity.Foreverdog
import com.sheltersdog.foreverdog.entity.model.ForeverdogStatus
import com.sheltersdog.foreverdog.mapper.toDto
import com.sheltersdog.foreverdog.repository.ForeverdogRepository
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import com.sheltersdog.shelter.event.SaveForeverdogEvent
import com.sheltersdog.shelter.repository.ShelterRepository
import com.sheltersdog.shelter.util.hasAuthority
import com.sheltersdog.shelter.util.hasAuthorityOrThrow
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class ForeverdogService @Autowired constructor(
    val shelterRepository: ShelterRepository,
    val foreverdogRepository: ForeverdogRepository,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    suspend fun postForeverdog(requestBody: PostForeverdogRequest): ForeverdogDto {
        val userId =
            (ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User).username
        val shelter = shelterRepository.findById(requestBody.shelterId)
            ?: throw SheltersdogException(
                type = ExceptionType.NOT_FOUND_SHELTER,
                variables = mapOf(
                    "shelterId" to requestBody.shelterId,
                    "userId" to userId,
                )
            )

        val hasAuthority = shelter.sheltersAdmins.hasAuthority(
            userId = userId,
            shelterAuthorities = listOf(
                ShelterAuthority.ADMIN,
                ShelterAuthority.DOG_MANAGE
            )
        )

        if (!hasAuthority) {
            throw SheltersdogException(
                type = ExceptionType.ACCESS_DENIED,
                variables = mapOf(
                    "userId" to userId,
                    "shelterId" to requestBody.shelterId,
                    "ShelterAuthority" to ShelterAuthority.DOG_MANAGE,
                ),
            )
        }

        val entity = Foreverdog(
            shelterId = requestBody.shelterId,
            status = requestBody.status,
            announcementId = requestBody.announcementId,
            profileImageUrl = requestBody.profileImageUrl,
            gender = requestBody.gender,
            isNeutering = requestBody.isNeutering,
            neuteringDate = requestBody.neuteringDate?.let { yyyyMMddToLocalDate(it) },
            protectedStartDate = requestBody.protectedStartDate?.let { yyyyMMddToLocalDate(it) },
            name = requestBody.name,
            birthYear = requestBody.birthYear,
            breed = requestBody.bread,
            weight = requestBody.weight,
            socializationLevel = requestBody.socializationLevel,
            content = requestBody.content,
            searchKeyword = "",
        )
        val foreverdog = foreverdogRepository.save(entity.copy(searchKeyword = entity.toString()))
        EventBus.publish(SaveForeverdogEvent(foreverdogId = foreverdog.id.toString()))
        return foreverdog.toDto(true)
    }

    suspend fun getForeverdogs(requestParam: GetForeverdogsRequest): List<ForeverdogDto> {
        val statuses = requestParam.statuses.map { status -> ForeverdogStatus.of(status) }

        val foreverdogs = foreverdogRepository.getForeverdogs(
            keyword = requestParam.keyword ?: "",
            pageable = PageRequest.of(requestParam.page, requestParam.size),
            shelterId = requestParam.shelterId,
            statuses = statuses,
        )

        return foreverdogs.map(Foreverdog::toDto)
    }

    suspend fun putForeverdogStatus(foreverdogId: String, status: ForeverdogStatus): ForeverdogDto {
        val entity = foreverdogRepository.findById(
            foreverdogId = foreverdogId,
            isContainShelter = true,
        ) ?: throw SheltersdogException(
            type = ExceptionType.NOT_FOUND_FOREVERDOG,
            variables = mapOf(
                "foreverdogId" to foreverdogId
            )
        )

        entity.shelter?.hasAuthorityOrThrow(
            shelterAuthorities = listOf(
                ShelterAuthority.ADMIN,
                ShelterAuthority.DOG_MANAGE
            )
        ) ?: throw SheltersdogException(
            type = ExceptionType.NOT_FOUND_SHELTER,
            variables = mapOf(
                "shelterId" to entity.shelterId,
                "foreverdogId" to foreverdogId,
            )
        )

        foreverdogRepository.updateById(
            id = foreverdogId,
            updateFields = mapOf(
                Pair(Foreverdog::status, status)
            )
        ).ifUpdateFailThrow(
            tableName = Foreverdog::class.java.name,
            variables = mapOf(
                "foreverdogId" to foreverdogId,
                "status" to status,
            )
        )

        return foreverdogRepository.findById(foreverdogId)?.toDto()
            ?: throw SheltersdogException(
                type = ExceptionType.NOT_FOUND_SHELTER,
                variables = mapOf(
                    "shelterId" to entity.shelterId,
                    "foreverdogId" to foreverdogId,
                )
            )
    }
}