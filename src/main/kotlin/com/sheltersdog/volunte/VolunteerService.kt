package com.sheltersdog.volunte

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.address.model.getPropertyCode
import com.sheltersdog.address.model.getPropertyName
import com.sheltersdog.address.repository.AddressRepository
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.util.HHmmToLocalTime
import com.sheltersdog.core.util.localDateToKoreanFormat
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.shelter.repository.ShelterRepository
import com.sheltersdog.volunte.dto.request.GetVolunteerCategoriesRequest
import com.sheltersdog.volunte.dto.request.GetVolunteersRequest
import com.sheltersdog.volunte.dto.request.PostCrawlingVolunteer
import com.sheltersdog.volunte.dto.request.PostVolunteer
import com.sheltersdog.volunte.dto.response.VolunteerDto
import com.sheltersdog.volunte.entity.CrawlingVolunteer
import com.sheltersdog.volunte.entity.Volunteer
import com.sheltersdog.volunte.mapper.volunteerToDto
import com.sheltersdog.volunte.repository.CrawlingVolunteerRepository
import com.sheltersdog.volunte.repository.VolunteerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class VolunteerService @Autowired constructor(
    val crawlingVolunteerRepository: CrawlingVolunteerRepository,
    val addressRepository: AddressRepository,
    val volunteerRepository: VolunteerRepository,
    val shelterRepository: ShelterRepository,
) {

    fun getVolunteers(requestBody: GetVolunteersRequest): Mono<List<VolunteerDto>> {
        val pageable = PageRequest.of(
            requestBody.page,
            requestBody.size,
            Sort.by(Sort.Direction.DESC, "id")
        )

        return crawlingVolunteerRepository.getCrawlingVolunteers(
            keyword = requestBody.keyword,
            regionCode = requestBody.regionCode,
            categories = requestBody.categories,
            date = requestBody.date,
            pageable = pageable,
            loadAddresses = true,
        ).map { volunteers ->
            volunteers.stream().map { volunteer ->
                VolunteerDto(
                    id = volunteer.id.toString(),
                    shelterName = volunteer.shelterName,
                    isShort = volunteer.isShort,
                    categories = volunteer.categories,
                    address = volunteer.address?.let { address ->
                        AddressDto(
                            id = address.id.toString(),
                            type = address.type,
                            regionName = address.regionName,
                            regionCode = address.regionCd,
                            name = address.type.getPropertyName().get(address).toString(),
                            code = address.type.getPropertyCode().get(address).toString()
                        )
                    },
                    detailAddress = volunteer.detailAddress,
                    startDate = localDateToKoreanFormat(volunteer.startDate),
                    endDate = localDateToKoreanFormat(volunteer.endDate),
                    days = volunteer.day,
                    time = volunteer.time,
                    content = volunteer.content,
                    url = volunteer.url
                )
            }.toList()
        }
    }

    fun postCrawlingVolunteer(requestBody: PostCrawlingVolunteer): Mono<CrawlingVolunteer> {
        return addressRepository.getAddressByRegionCode(requestBody.addressRegionCode)
            .flatMap { address ->
                val searchKeyword =
                    "${requestBody.name} ${requestBody.shelterName} ${requestBody.categories} ${requestBody.time} ${requestBody.content} ${requestBody.detailAddress} ${address.regionName}"
                crawlingVolunteerRepository.saveCrawlingVolunteer(
                    CrawlingVolunteer(
                        name = requestBody.name,
                        shelterName = requestBody.shelterName,
                        isShort = requestBody.isShort,
                        categories = requestBody.categories,
                        addressRegionCode = address.regionCd,
                        detailAddress = requestBody.detailAddress,
                        startDate = yyyyMMddToLocalDate(requestBody.startDate),
                        endDate = yyyyMMddToLocalDate(requestBody.endDate),
                        day = requestBody.day,
                        time = requestBody.time,
                        content = requestBody.content,
                        url = requestBody.url,
                        searchKeyword = searchKeyword,
                    )
                )
            }
    }

    fun getVolunteerCategories(requestParam: GetVolunteerCategoriesRequest): Mono<Array<String>> {
        return crawlingVolunteerRepository.getCrawlingVolunteers(
            regionCode = requestParam.regionCode,
            date = requestParam.date,
        ).map { entities ->
            val categories = mutableSetOf<String>()
            entities.forEach { entity -> entity.categories.forEach(categories::add) }
            categories.toTypedArray()
        }
    }

    fun postVolunteer(requestBody: PostVolunteer): Mono<VolunteerDto> {
        return shelterRepository.findById(requestBody.shelterId).flatMap { shelter ->
            volunteerRepository.save(
                Volunteer(
                    shelterName = shelter.name,
                    isShort = requestBody.isShort,
                    categories = requestBody.categories,
                    regionCode = requestBody.regionCode,
                    detailAddress = requestBody.detailAddress,
                    isPrivateDetailAddress = shelter.isPrivateDetailAddress,
                    startDate = yyyyMMddToLocalDate(requestBody.startDate),
                    endDate = yyyyMMddToLocalDate(requestBody.endDate),
                    startTime = HHmmToLocalTime(requestBody.startTime),
                    endTime = HHmmToLocalTime(requestBody.endTime),
                    days = requestBody.days,
                    content = requestBody.content,
                    arriveRegionCode = requestBody.arriveRegionCode,
                    arriveDetailAddress = requestBody.arriveDetailAddress,
                )
            )
        }.map { volunteer ->
            volunteerToDto(volunteer, true)
        }.switchIfEmpty {
            Mono.defer { Mono.error { throw SheltersdogException("등록에 실패했습니다.") } }
        }
    }
}