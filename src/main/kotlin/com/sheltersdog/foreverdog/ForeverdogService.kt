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

@Service
class ForeverdogService @Autowired constructor(
    val shelterRepository: ShelterRepository,
    val foreverdogRepository: ForeverdogRepository
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    suspend fun postForeverdog(requestBody: PostForeverdogRequest): ForeverdogDto {
        val userId = (SecurityContextHolder.getContext().authentication.principal as User).username

        val shelter = shelterRepository.findById(requestBody.shelterId)
        val hasAuthority = hasAuthority(
            shelterAdmins = shelter.sheltersAdmins,
            userId = userId,
            shelterAuthorities = listOf(ShelterAuthority.ADMIN, ShelterAuthority.DOG_MANAGE)
        )

        if (!hasAuthority) {
            log.debug("postForeverdog fail -> $userId is not have authority")
            throw SheltersdogException("$userId is not have authority(${ShelterAuthority.DOG_MANAGE})")
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
        return foreverdogToDto(foreverdog, true)
    }

    suspend fun getForeverdogs(requestParam: GetForeverdogsRequest): List<ForeverdogDto> {
        val foreverdogs = foreverdogRepository.getForeverdogs(
            keyword = requestParam.keyword ?: "",
            pageable = PageRequest.of(requestParam.page, requestParam.size),
            shelterId = requestParam.shelterId,
        )

        return foreverdogs.map { foreverdog -> foreverdogToDto(foreverdog) }
    }
}