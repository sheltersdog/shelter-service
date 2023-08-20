package com.sheltersdog.shelter

import com.sheltersdog.address.repository.AddressRepository
import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.shelter.dto.request.GetShelterListRequest
import com.sheltersdog.shelter.dto.request.PostShelterRequest
import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.entity.ShelterJoinUser
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import com.sheltersdog.shelter.mapper.toDto
import com.sheltersdog.shelter.repository.ShelterRepository
import com.sheltersdog.user.entity.ifNullOrNotActiveThrow
import com.sheltersdog.user.repository.UserRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.ReactiveSecurityContextHolder
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
        val userId =
            (ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User).username
        val user = userRepository.findById(userId)
            .ifNullOrNotActiveThrow(
                variables = mapOf("userId" to userId)
            )

        val address = addressRepository.getAddressByRegionCode(requestBody.regionCode)
        val shelter = Shelter(
            name = requestBody.name,
            profileImageUrl = requestBody.profileImageUrl,
            contactNumber = requestBody.contactNumber,
            isPrivateContact = requestBody.isPrivateContact,
            address = address,
            detailAddress = requestBody.detailAddress,
            regionCode = requestBody.regionCode,
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
        return copyShelter.toDto(isIncludeAddress = true)
    }

    suspend fun getShelter(id: String): ShelterDto {
        val shelter = shelterRepository.findById(id)
            ?: throw SheltersdogException(
                exceptionType = ExceptionType.NOT_FOUND_SHELTER,
                variables = mapOf("shelterId" to id)
            )
        return shelter.toDto(isIncludeAddress = true)
    }

    suspend fun getShelterList(
        requestParam: GetShelterListRequest,
    ): List<ShelterDto> {
        val pageable = PageRequest.of(
            requestParam.page,
            requestParam.size,
            Sort.by(Sort.Direction.DESC, "id")
        )

        return shelterRepository.getShelterList(
            pageable = pageable,
            keyword = requestParam.keyword,
            regionCode = requestParam.regionCode,
            isVolunteerRecruiting = requestParam.isVolunteerRecruiting,
            isDonationPossible = requestParam.isDonationPossible,
            loadAddresses = true,
        ).map(Shelter::toDto)
    }


}