package com.sheltersdog.volunte

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.address.model.getPropertyCode
import com.sheltersdog.address.model.getPropertyName
import com.sheltersdog.address.repository.AddressRepository
import com.sheltersdog.core.util.localDateToKoreanFormat
import com.sheltersdog.core.util.yyyyMMddToLocalDate
import com.sheltersdog.volunte.dto.request.GetVolunteerCategoriesRequest
import com.sheltersdog.volunte.dto.request.GetVolunteersRequest
import com.sheltersdog.volunte.dto.request.PostCrawlingVolunteer
import com.sheltersdog.volunte.dto.response.VolunteeerDto
import com.sheltersdog.volunte.entity.CrawlingVolunteer
import com.sheltersdog.volunte.repository.CrawlingVolunteerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class VolunteerService @Autowired constructor(
    val crawlingVolunteerRepository: CrawlingVolunteerRepository,
    val addressRepository: AddressRepository,
) {

    @Transactional
    fun getVolunteers(requestBody: GetVolunteersRequest): Mono<List<VolunteeerDto>> {
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
                VolunteeerDto(
                    id = volunteer.id.toString(),
                    name = volunteer.name,
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
                    day = volunteer.day,
                    time = volunteer.time,
                    content = volunteer.content,
                    url = volunteer.url
                )
            }.toList()
        }
    }

    @Transactional
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
}