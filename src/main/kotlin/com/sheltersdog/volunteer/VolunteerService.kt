package com.sheltersdog.volunteer

import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.entity.model.ShelterAuthority
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
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class VolunteerService @Autowired constructor(
    val volunteerRepository: VolunteerRepository,
    val shelterRepository: ShelterRepository,
) {
    val log = LoggerFactory.getLogger(this::class.java)

    fun postVolunteer(requestBody: PostVolunteer): Mono<VolunteerDto> {
        val mono = if (requestBody.sourceType == SourceType.SERVICE) {
            shelterRepository.findById(requestBody.shelterId!!)
        } else Mono.just(false)

        return mono.flatMap { any ->
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

            if (any is Shelter) {
                val userId = (SecurityContextHolder.getContext().authentication.principal as User).username
                if (
                    !hasAuthority(
                        shelterAdmins = any.sheltersAdmins,
                        userId = userId,
                        shelterAuthorities = listOf(ShelterAuthority.ADMIN, ShelterAuthority.DOG_MANAGE)
                    )
                ) return@flatMap Mono.empty()

                volunteerRepository.save(
                    entity.copy(
                        shelterName = any.name,
                        searchKeyword = any.toString(),
                    )
                )
            } else {
                volunteerRepository.save(entity)
            }
        }.map { volunteer ->
            volunteerToDto(volunteer, true)
        }.switchIfEmpty {
            Mono.defer { Mono.error { throw SheltersdogException("등록에 실패했습니다.") } }
        }.doOnError { error ->
            log.error("fail", error)
        }
    }

    fun getVolunteers(requestBody: GetVolunteersRequest): Mono<List<VolunteerDto>> {
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
        ).map { volunteers ->
            volunteers.stream()
                .map { volunteer ->
                    volunteerToDto(
                        volunteer,
                        isIncludeAddress = true
                    )
                }
                .toList()
        }
    }

    fun getVolunteerCategories(requestParam: GetVolunteerCategoriesRequest): Mono<Array<String>> {
        return volunteerRepository.getVolunteers(
            regionCode = requestParam.regionCode,
            date = requestParam.date,
        ).map { entities ->
            val categories = mutableSetOf<String>()
            entities.forEach { entity -> entity.categories.forEach(categories::add) }
            categories.toTypedArray()
        }
    }
}