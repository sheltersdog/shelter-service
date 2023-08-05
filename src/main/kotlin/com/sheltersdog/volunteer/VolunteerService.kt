package com.sheltersdog.volunteer

import com.sheltersdog.core.event.EventBus
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import com.sheltersdog.shelter.event.SaveVolunteerEvent
import com.sheltersdog.shelter.repository.ShelterRepository
import com.sheltersdog.shelter.util.hasAuthority
import com.sheltersdog.volunteer.dto.request.GetVolunteerCategoriesRequest
import com.sheltersdog.volunteer.dto.request.GetVolunteersRequest
import com.sheltersdog.volunteer.dto.request.PostVolunteer
import com.sheltersdog.volunteer.dto.response.VolunteerDto
import com.sheltersdog.volunteer.entity.Volunteer
import com.sheltersdog.volunteer.entity.model.SourceType
import com.sheltersdog.volunteer.mapper.volunteerToDto
import com.sheltersdog.volunteer.repository.VolunteerRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class VolunteerService @Autowired constructor(
    val volunteerRepository: VolunteerRepository,
    val shelterRepository: ShelterRepository,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    suspend fun postVolunteer(requestBody: PostVolunteer): VolunteerDto {
        val shelter = if (requestBody.sourceType == SourceType.SERVICE) {
            shelterRepository.findById(requestBody.shelterId!!)
        } else null
        log.debug("postVolunteer start, shelter: $shelter")

        val startDate = requestBody.startDate?.let { yyyyMMddToLocalDate(it) }
        val endDate = requestBody.endDate?.let { yyyyMMddToLocalDate(it) }

        val entity = Volunteer(
            shelterName = requestBody.shelterName ?: "",
            sourceType = requestBody.sourceType,
            shelterId = requestBody.shelterId,
            isShort = requestBody.isShort,
            categories = requestBody.categories,
            regionCode = requestBody.regionCode,
            detailAddress = requestBody.detailAddress,
            isPrivateDetailAddress = requestBody.isPrivateDetailAddress,
            isAlwaysRecruiting = requestBody.isAlwaysRecruiting,
            startDate = startDate,
            endDate = endDate,
            startTime = requestBody.startTime,
            endTime = requestBody.endTime,
            days = requestBody.days,
            content = requestBody.content,
            url = requestBody.url,
            arriveRegionCode = requestBody.arriveRegionCode,
            arriveDetailAddress = requestBody.arriveDetailAddress,
            searchKeyword = requestBody.toString(),
        )

        val volunteer = saveVolunteer(shelter, entity)
        return volunteerToDto(volunteer, true)
    }

    private suspend fun saveVolunteer(
        shelter: Shelter?,
        entity: Volunteer
    ): Volunteer {
        val userId = (ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User).username
        val volunteer = if (shelter != null) {
            if (
                !hasAuthority(
                    shelterAdmins = shelter.sheltersAdmins,
                    userId = userId!!,
                    shelterAuthorities = listOf(ShelterAuthority.ADMIN, ShelterAuthority.VOLUNTEER_MANAGE)
                )
            ) {
                log.debug("postVolunteer -> $userId is not have authority")
                throw SheltersdogException("$userId is not have authority(${ShelterAuthority.VOLUNTEER_MANAGE})")
            }

            val saveEntity = volunteerRepository.save(
                entity.copy(
                    shelterName = shelter.name,
                    searchKeyword = shelter.toString(),
                )
            )
            EventBus.publish(SaveVolunteerEvent(volunteerId = saveEntity.id.toString()))
            saveEntity
        } else {
            volunteerRepository.save(entity)
        }
        return volunteer
    }

    suspend fun getVolunteers(requestBody: GetVolunteersRequest): List<VolunteerDto> {
        val pageable = PageRequest.of(
            requestBody.page,
            requestBody.size,
            Sort.by(Sort.Direction.DESC, "id")
        )

        return volunteerRepository.getVolunteers(
            keyword = requestBody.keyword,
            regionCode = requestBody.regionCode,
            categories = requestBody.categories,
            date = requestBody.date,
            pageable = pageable,
            loadAddresses = true,
        ).stream().map { volunteer ->
            volunteerToDto(
                volunteer,
                isIncludeAddress = true
            )
        }.toList()
    }

    suspend fun getVolunteerCategories(requestParam: GetVolunteerCategoriesRequest): Array<String> {
        val entities = volunteerRepository.getVolunteers(
            regionCode = requestParam.regionCode,
            date = requestParam.date,
        )

        val categories = mutableSetOf<String>()
        entities.forEach { entity -> entity.categories.forEach(categories::add) }
        return categories.toTypedArray()
    }
}