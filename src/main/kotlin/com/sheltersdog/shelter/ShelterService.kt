package com.sheltersdog.shelter

import com.sheltersdog.address.repository.AddressRepository
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.shelter.dto.request.PostShelterRequest
import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.entity.ShelterJoinUser
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import com.sheltersdog.shelter.mapper.shelterToDto
import com.sheltersdog.shelter.repository.ShelterRepository
import com.sheltersdog.user.entity.User
import com.sheltersdog.user.entity.model.UserStatus
import com.sheltersdog.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDate

@Service
class ShelterService @Autowired constructor(
    val shelterRepository: ShelterRepository,
    val userRepository: UserRepository,
    val addressRepository: AddressRepository,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun postShelter(requestBody: PostShelterRequest, userId: String): Mono<ShelterDto> {
        var admin: User? = null
        return userRepository.findById(userId)
            .flatMap { user ->
                if (user.status != UserStatus.ACTIVE) return@flatMap Mono.empty()
                admin = user
                addressRepository.getAddressByRegionCode(requestBody.regionCode)
            }.flatMap { address ->
                val shelter = Shelter(
                    name = requestBody.name,
                    profileImageUrl = requestBody.profileImageUrl,
                    contactNumber = requestBody.contactNumber,
                    isPrivateContact = requestBody.isPrivateContact,
                    address = address,
                    detailAddress = requestBody.detailAddress,
                    x = requestBody.x,
                    y = requestBody.x,
                    isPrivateDetailAddress = requestBody.isPrivateDetailAddress,
                    shelterSns = requestBody.shelterSns,
                    representativeSns = requestBody.representativeSns,
                    donationPath = requestBody.donationPath,
                    donationUsageHistoryLink = requestBody.donationUsageHistoryLink,
                    isDonationPossible = requestBody.donationPath != null,
                    sheltersAdmins = listOf(
                        ShelterJoinUser(
                            userId = admin!!.id.toString(),
                            name = admin!!.name,
                            nickname = admin!!.nickname,
                            email = admin!!.email,
                            authorities = listOf(ShelterAuthority.ADMIN),
                            profileImageUrl = admin!!.profileImageUrl,
                        )
                    ),
                    createdDate = LocalDate.now(),
                    modifyDate = LocalDate.now(),
                    searchKeyword = ""
                )
                shelterRepository.save(shelter.copy(searchKeyword = shelter.toString()))
            }.map { shelter -> shelterToDto(shelter, isIncludeAddress = true) }
            .switchIfEmpty {
                Mono.defer {
                    Mono.error { throw SheltersdogException("Shelter 등록에 실패했습니다.") }
                }
            }.doOnError { error ->
                log.error("post shelter error, requestBody: $requestBody & userId: $userId", error)
            }
    }


}