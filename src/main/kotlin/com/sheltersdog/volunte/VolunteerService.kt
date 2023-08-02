package com.sheltersdog.volunte

import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.util.HHmmToLocalTime
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.repository.ShelterRepository
import com.sheltersdog.volunte.dto.request.GetVolunteerCategoriesRequest
import com.sheltersdog.volunte.dto.request.GetVolunteersRequest
import com.sheltersdog.volunte.dto.request.PostVolunteer
import com.sheltersdog.volunte.dto.response.VolunteerDto
import com.sheltersdog.volunte.entity.Volunteer
import com.sheltersdog.volunte.entity.model.SourceType
import com.sheltersdog.volunte.mapper.volunteerToDto
import com.sheltersdog.volunte.repository.VolunteerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class VolunteerService @Autowired constructor(
    val volunteerRepository: VolunteerRepository,
    val shelterRepository: ShelterRepository,
) {

    fun postVolunteer(requestBody: PostVolunteer): Mono<VolunteerDto> {
        val startDate = requestBody.startDate?.let { yyyyMMddToLocalDate(it) }
        val endDate = requestBody.endDate?.let { yyyyMMddToLocalDate(it) }

        val e = Volunteer(
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

        val mono = if (requestBody.sourceType == SourceType.SERVICE) {
            shelterRepository.findById(requestBody.shelterId!!)
        } else Mono.just(-1)

        return mono.flatMap { any ->
            volunteerRepository.save(
                if (any is Shelter) {
                    e.copy(
                        shelterName = any.name,
                        searchKeyword = any.toString(),
                    )
                } else e
            )
        }.map { volunteer ->
            volunteerToDto(volunteer, true)
        }.switchIfEmpty {
            Mono.defer { Mono.error { throw SheltersdogException("등록에 실패했습니다.") } }
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