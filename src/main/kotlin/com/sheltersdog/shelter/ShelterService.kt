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
import com.sheltersdog.user.entity.model.UserStatus
import com.sheltersdog.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ShelterService @Autowired constructor(
    val shelterRepository: ShelterRepository,
    val userRepository: UserRepository,
    val addressRepository: AddressRepository,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    suspend fun postShelter(requestBody: PostShelterRequest): ShelterDto {
        val userId = (SecurityContextHolder.getContext().authentication.principal as User).username
        val user = userRepository.findById(userId)
        if (user == null || user.status != UserStatus.ACTIVE) {
            log.debug("postShelter -> user's status is not ${UserStatus.ACTIVE}: $userId")
            throw SheltersdogException("$userId is not ${UserStatus.ACTIVE} user")
        }

        val address = addressRepository.getAddressByRegionCode(requestBody.regionCode)
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
                    userId = user.id.toString(),
                    name = user.name,
                    nickname = user.nickname,
                    email = user.email,
                    authorities = listOf(ShelterAuthority.ADMIN),
                    profileImageUrl = user.profileImageUrl,
                )
            ),
            createdDate = LocalDate.now(),
            modifyDate = LocalDate.now(),
            searchKeyword = ""
        )

        val copyShelter = shelterRepository.save(shelter.copy(searchKeyword = shelter.toString()))
        return shelterToDto(copyShelter, isIncludeAddress = true)
    }

    suspend fun getShelter(id: String): ShelterDto {
        val shelter = shelterRepository.findById(id)
        if (shelter == null) {
            log.debug("shelterId: $id is not exist")
            throw SheltersdogException("존재하지 않는 보호소입니다.")
        }
        return shelterToDto(shelter, isIncludeAddress = true)
    }


}