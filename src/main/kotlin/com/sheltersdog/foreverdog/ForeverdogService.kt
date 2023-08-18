package com.sheltersdog.foreverdog

import com.sheltersdog.core.event.EventBus
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.log.LogMessage
import com.sheltersdog.core.log.loggingAndException
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.foreverdog.dto.request.GetForeverdogsRequest
import com.sheltersdog.foreverdog.dto.request.PostForeverdogRequest
import com.sheltersdog.foreverdog.dto.response.ForeverdogDto
import com.sheltersdog.foreverdog.entity.Foreverdog
import com.sheltersdog.foreverdog.entity.model.ForeverdogStatus
import com.sheltersdog.foreverdog.mapper.foreverdogToDto
import com.sheltersdog.foreverdog.repository.ForeverdogRepository
import com.sheltersdog.shelter.entity.ifNullThrow
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import com.sheltersdog.shelter.event.SaveForeverdogEvent
import com.sheltersdog.shelter.repository.ShelterRepository
import com.sheltersdog.shelter.util.hasAuthority
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
        val shelter = shelterRepository.findById(requestBody.shelterId).ifNullThrow(
            variables = mapOf(
                "shelterId" to requestBody.shelterId,
                "userId" to userId,
            )
        )

        val hasAuthority = hasAuthority(
            shelterAdmins = shelter.sheltersAdmins,
            userId = userId,
            shelterAuthorities = listOf(ShelterAuthority.ADMIN, ShelterAuthority.DOG_MANAGE)
        )

        if (!hasAuthority) {
            throw LogMessage.ACCESS_DENIED.loggingAndException(
                staceTraceElement = Thread.currentThread().stackTrace[1],
                variables = mapOf("userId" to userId)
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
        return foreverdogToDto(foreverdog, true)
    }

    suspend fun getForeverdogs(requestParam: GetForeverdogsRequest): List<ForeverdogDto> {
        val statuses = requestParam.statuses.map { status -> ForeverdogStatus.of(status) }

        val foreverdogs = foreverdogRepository.getForeverdogs(
            keyword = requestParam.keyword ?: "",
            pageable = PageRequest.of(requestParam.page, requestParam.size),
            shelterId = requestParam.shelterId,
            statuses = statuses,
        )

        return foreverdogs.map { foreverdog -> foreverdogToDto(foreverdog) }
    }

    suspend fun putForeverdogStatus(foreverdogId: String, status: ForeverdogStatus): ForeverdogDto {
        val entity = foreverdogRepository.findById(foreverdogId)
        if (entity == null) {
            log.debug("putForeverdogStatus :: 존재하지 않는 데이터입니다. foreverdogId: $foreverdogId")
            throw SheltersdogException("상태 변경에 실패하였습니다.")
        }

        val hasAuthority = entity.shelter?.sheltersAdmins?.let { shelterAdmins ->
            hasAuthority(
                shelterAdmins = shelterAdmins,
                shelterAuthorities = listOf(ShelterAuthority.ADMIN, ShelterAuthority.DOG_MANAGE)
            )
        } ?: false

        if (!hasAuthority) {
            val userId = (ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User).username
            throw LogMessage.ACCESS_DENIED.loggingAndException(
                staceTraceElement = Thread.currentThread().stackTrace[1],
                variables = mapOf("userId" to userId, "foreverdogId" to foreverdogId)
            )
        }

        val result = foreverdogRepository.updateById(
            id = foreverdogId,
            updateFields = mapOf(
                Pair(Foreverdog::status, status)
            )
        )

        if (!result.wasAcknowledged()) {
            log.debug("putForeverdogStatus :: 강아지 상태 변경에 실패하였습니다. foreverdogId: $foreverdogId, status: $status")
            throw SheltersdogException("상태 변경에 실패하였습니다.")
        }
        return foreverdogToDto(
            entity = foreverdogRepository.findById(foreverdogId)!!
        )
    }
}