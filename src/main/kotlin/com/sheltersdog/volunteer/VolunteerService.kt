package com.sheltersdog.volunteer

import com.sheltersdog.core.event.EventBus
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.model.SheltersdogStatus
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import com.sheltersdog.shelter.event.SaveVolunteerEvent
import com.sheltersdog.shelter.repository.ShelterRepository
import com.sheltersdog.shelter.util.hasAuthority
import com.sheltersdog.volunteer.dto.request.GetVolunteerCategoriesRequest
import com.sheltersdog.volunteer.dto.request.GetVolunteersRequest
import com.sheltersdog.volunteer.dto.request.PostVolunteer
import com.sheltersdog.volunteer.dto.request.PutVolunteer
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
            startDate = requestBody.startDate?.let { yyyyMMddToLocalDate(it) },
            endDate = requestBody.endDate?.let { yyyyMMddToLocalDate(it) },
            startTime = requestBody.startTime,
            endTime = requestBody.endTime,
            exposeStartDate = requestBody.exposeStartDate?.let { yyyyMMddToLocalDate(it) },
            exposeEndDate = requestBody.exposeEndDate?.let { yyyyMMddToLocalDate(it) },
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

    suspend fun putVolunteer(requestBody: PutVolunteer): VolunteerDto? {
        val volunteer = volunteerRepository.findById(requestBody.id)
        if (volunteer == null) {
            log.debug("putVolunteer :: 봉사 id값이 다릅니다. requestBody: $requestBody")
            throw SheltersdogException("존재하지 않는 봉사 정보입니다.")
        }

        volunteer.shelterId?.let { shelterId ->
            val shelter = shelterRepository.findById(shelterId)

            if (shelter == null) {
                log.warn("봉사정보와 쉼터 정보가 일치하지 않습니다. volunteerId: ${requestBody.id}, shelterId: $shelterId")
                return@let
            }

            val userId =
                (ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User).username
            if (
                !hasAuthority(
                    shelterAdmins = shelter.sheltersAdmins,
                    userId = userId!!,
                    shelterAuthorities = listOf(ShelterAuthority.ADMIN, ShelterAuthority.VOLUNTEER_MANAGE)
                )
            ) {
                log.debug("putVolunteer :: $userId 는 봉사 정보를 수정할 권한이 없습니다. :: shelterId = $shelterId, ${ShelterAuthority.VOLUNTEER_MANAGE}")
                throw SheltersdogException("$userId 는 봉사 정보를 업데이트할 권한이 없습니다.")
            }
        }


        val startDate = requestBody.startDate?.let { yyyyMMddToLocalDate(it) }
        val endDate = requestBody.endDate?.let { yyyyMMddToLocalDate(it) }
        val exposeStartDate = requestBody.exposeStartDate?.let { yyyyMMddToLocalDate(it) }
        val exposeEndDate = requestBody.exposeEndDate?.let { yyyyMMddToLocalDate(it) }

        val updateResult = volunteerRepository.updateById(
            id = requestBody.id,
            updateFields = mapOf(
                Pair(Volunteer::isShort, requestBody.isShort),
                Pair(Volunteer::categories, requestBody.categories),
                Pair(Volunteer::startDate, startDate),
                Pair(Volunteer::endDate, endDate),
                Pair(Volunteer::days, requestBody.days),
                Pair(Volunteer::startTime, requestBody.startTime),
                Pair(Volunteer::endTime, requestBody.endTime),
                Pair(Volunteer::isAlwaysRecruiting, requestBody.isAlwaysRecruiting),
                Pair(Volunteer::regionCode, requestBody.regionCode),
                Pair(Volunteer::detailAddress, requestBody.detailAddress),
                Pair(Volunteer::arriveRegionCode, requestBody.arriveRegionCode),
                Pair(Volunteer::arriveDetailAddress, requestBody.arriveDetailAddress),
                Pair(Volunteer::isPrivateDetailAddress, requestBody.isPrivateDetailAddress),
                Pair(Volunteer::content, requestBody.content),
                Pair(Volunteer::url, requestBody.url),
                Pair(Volunteer::exposeStartDate, exposeStartDate),
                Pair(Volunteer::exposeEndDate, exposeEndDate),
            )
        )

        if (!updateResult.wasAcknowledged()) {
            log.debug("putVolunteer :: 봉사 정보 업데이트에 실패했습니다. $requestBody")
            throw SheltersdogException("봉사 정보 업데이트에 실패했습니다.")
        }

        val updateVolunteer = volunteerRepository.findById(updateResult.upsertedId.toString())
        return updateVolunteer?.let {
            volunteerToDto(updateVolunteer, true)
        }
    }

    private suspend fun saveVolunteer(
        shelter: Shelter?,
        entity: Volunteer,
    ): Volunteer {
        val userId =
            (ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User).username
        val volunteer = if (shelter != null) {
            if (
                !hasAuthority(
                    shelterAdmins = shelter.sheltersAdmins,
                    userId = userId!!,
                    shelterAuthorities = listOf(ShelterAuthority.ADMIN, ShelterAuthority.VOLUNTEER_MANAGE)
                )
            ) {
                log.debug("postVolunteer -> $userId 는 봉사 정보를 수정할 권한이 없습니다. :: shelterId = ${shelter.id}, ${ShelterAuthority.VOLUNTEER_MANAGE}")
                throw SheltersdogException("회원에게 쉼터의 봉사 정보를 업데이트할 권한이 없습니다.")
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
            statuses = requestBody.statuses.map { status -> SheltersdogStatus.valueOf(status) },
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

    suspend fun putAllVolunteerStatusByShelterId(shelterId: String) {
        val shelter = shelterRepository.findById(shelterId)
        if (shelter == null) {
            log.debug("존재하지 않는 쉼터입니다. shelterId: $shelterId")
            throw SheltersdogException("존재하지 않는 쉼터입니다.")
        }

        val userId =
            (ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User).username
        if (
            !hasAuthority(
                shelterAdmins = shelter.sheltersAdmins,
                userId = userId!!,
                shelterAuthorities = listOf(ShelterAuthority.ADMIN, ShelterAuthority.VOLUNTEER_MANAGE)
            )
        ) {
            log.debug("putAllVolunteerStatusByShelterId :: $userId 는 봉사 정보를 수정할 권한이 없습니다. :: shelterId = $shelterId, ${ShelterAuthority.VOLUNTEER_MANAGE}")
            throw SheltersdogException("회원에게 쉼터의 봉사 정보를 업데이트할 권한이 없습니다.")
        }

        val updateResult = volunteerRepository.updateAllByShelterId(
            shelterId = shelterId,
            updateFields = mapOf(Pair(Volunteer::status, SheltersdogStatus.INACTIVE))
        )

        if (!updateResult.wasAcknowledged()) {
            log.debug("putAllVolunteerStatusByShelterId :: 모든 봉사 비공개를 실패했습니다. $shelterId")
            throw SheltersdogException("봉사 정보 업데이트에 실패했습니다.")
        }
    }
}