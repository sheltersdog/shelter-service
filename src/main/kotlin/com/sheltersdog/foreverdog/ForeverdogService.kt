package com.sheltersdog.foreverdog

import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.foreverdog.dto.request.GetForeverdogsRequest
import com.sheltersdog.foreverdog.dto.request.PostForeverdogRequest
import com.sheltersdog.foreverdog.dto.response.ForeverdogDto
import com.sheltersdog.foreverdog.entity.Foreverdog
import com.sheltersdog.foreverdog.mapper.foreverdogToDto
import com.sheltersdog.foreverdog.repository.ForeverdogRepository
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import com.sheltersdog.shelter.repository.ShelterRepository
import com.sheltersdog.shelter.util.hasAuthority
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class ForeverdogService @Autowired constructor(
    val shelterRepository: ShelterRepository,
    val foreverdogRepository: ForeverdogRepository
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun postForeverdog(requestBody: PostForeverdogRequest): Mono<ForeverdogDto> {
        val userId = (SecurityContextHolder.getContext().authentication.principal as User).username
        return shelterRepository.findById(requestBody.shelterId).flatMap { shelter ->
            val hasAuthority = hasAuthority(
                shelterAdmins = shelter.sheltersAdmins,
                userId = userId,
                shelterAuthorities = listOf(ShelterAuthority.ADMIN, ShelterAuthority.DOG_MANAGE)
            )

            if (!hasAuthority) return@flatMap Mono.empty()
            Mono.just(true)
        }.flatMap { _ ->
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
            foreverdogRepository.save(entity.copy(searchKeyword = entity.toString()))
        }.map { foreverdog -> foreverdogToDto(foreverdog, true) }
            .switchIfEmpty {
                Mono.defer { Mono.error { throw SheltersdogException("등록에 실패하였습니다.") } }
            }.doOnError { error ->
                log.error("등록에 실패했습니다. requestBody: $requestBody", error)
            }
    }

    fun getForeverdogs(requestParam: GetForeverdogsRequest): Mono<List<ForeverdogDto>> {
        return foreverdogRepository.getForeverdogs(
            keyword = requestParam.keyword ?: "",
            pageable = PageRequest.of(requestParam.page, requestParam.size),
            shelterId = requestParam.shelterId,
        ).map { foreverdogs ->
            foreverdogs.map { foreverdog -> foreverdogToDto(foreverdog) }
        }.switchIfEmpty { Mono.just(listOf()) }.doOnError { error ->
            log.error("조회에 실패했습니다. requestParam: $requestParam", error)
        }
    }
}