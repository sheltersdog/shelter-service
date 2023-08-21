package com.sheltersdog.shelter

import com.sheltersdog.address.repository.AddressRepository
import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.mail.SheltersdogMailSender
import com.sheltersdog.core.mail.SheltersdogMailType
import com.sheltersdog.core.util.ifUpdateFailThrow
import com.sheltersdog.shelter.dto.request.GetShelterListRequest
import com.sheltersdog.shelter.dto.request.PostShelterAdminInviteRequest
import com.sheltersdog.shelter.dto.request.PostShelterRequest
import com.sheltersdog.shelter.dto.request.PutShelterRequest
import com.sheltersdog.shelter.dto.response.ShelterDto
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.entity.ShelterAdminInvite
import com.sheltersdog.shelter.entity.ShelterJoinUser
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import com.sheltersdog.shelter.mapper.toDto
import com.sheltersdog.shelter.repository.ShelterRepository
import com.sheltersdog.shelter.util.hasAuthorityOrThrow
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
import java.time.LocalDateTime
import kotlin.reflect.KProperty

@Service
class ShelterService @Autowired constructor(
    val shelterRepository: ShelterRepository,
    val userRepository: UserRepository,
    val addressRepository: AddressRepository,
    val mailSender: SheltersdogMailSender,
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
                type = ExceptionType.NOT_FOUND_SHELTER,
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
        ).map { shelter -> shelter.toDto(isIncludeAddress = true) }
    }

    suspend fun putShelter(requestBody: PutShelterRequest): ShelterDto {
        val userId =
            (ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User).username

        val shelter = shelterRepository.findById(requestBody.id)
            ?: throw SheltersdogException(
                type = ExceptionType.NOT_FOUND_SHELTER,
                variables = mapOf("shelterId" to requestBody.id)
            )

        shelter.hasAuthorityOrThrow(
            userId = userId,
            shelterAuthorities = listOf(
                ShelterAuthority.ADMIN, ShelterAuthority.SHELTER_DETAIL_MANAGE
            )
        )

        val updateFields = mutableMapOf<KProperty<*>, Any?>(
            Shelter::name to requestBody.name,
            Shelter::profileImageUrl to requestBody.profileImageUrl,
            Shelter::contactNumber to requestBody.contactNumber,
            Shelter::detailAddress to requestBody.detailAddress,
            Shelter::x to requestBody.x,
            Shelter::y to requestBody.y,
            Shelter::shelterSns to requestBody.shelterSns,
            Shelter::representativeSns to requestBody.representativeSns,
            Shelter::donationPath to requestBody.donationPath,
            Shelter::donationUsageHistoryLink to requestBody.donationUsageHistoryLink,
            Shelter::isDonationPossible to (requestBody.donationPath != null),
            Shelter::isPrivateDetailAddress to requestBody.isPrivateDetailAddress,
        )

        if (shelter.regionCode != requestBody.regionCode) {
            val address = addressRepository.getAddressByRegionCode(requestBody.regionCode)
            updateFields[Shelter::regionCode] = requestBody.regionCode
            updateFields[Shelter::address] = address
        }

        shelterRepository.updateById(
            id = requestBody.id,
            updateFields = updateFields,
        ).ifUpdateFailThrow(
            tableName = Shelter::class.java.name,
            variables = updateFields.mapKeys { (key) -> key.name },
        )

        /**
         * 위에서 업데이트에 성공했다는건 id가 확실하게 존재한다는 의미인데, 이를 아래와 같이 null인 경우를 확인해야하는지 생각할 필요가 있음.
         * 만약 동시에 다른 사람이 쉼터를 삭제하더라도....쉼터의 상태만 변경되는건데...
         * TODO 업데이트 직후 쉼터가 갑자기 삭제되는 경우가 발생할 수 있을지 생각해볼것.
         */
        return shelterRepository.findById(requestBody.id)?.toDto(
            isIncludeAddress = true,
            isIncludeSheltersAdmin = true
        ) ?: throw SheltersdogException(
            type = ExceptionType.NOT_FOUND_SHELTER,
            variables = mapOf("shelterId" to requestBody.id)
        )
    }

    suspend fun inviteShelterAdmin(requestBody: PostShelterAdminInviteRequest) {
        val shelter = shelterRepository.findById(requestBody.shelterId)
            ?: throw SheltersdogException(
                type = ExceptionType.NOT_FOUND_SHELTER,
                variables = mapOf("shelterId" to requestBody.shelterId)
            )

        shelter.hasAuthorityOrThrow(
            shelterAuthorities = listOf(
                ShelterAuthority.ADMIN, ShelterAuthority.ADMIN_MANAGE
            )
        )

        val invites = shelter.shelterAdminInvites
        invites.firstOrNull { e -> e.email == requestBody.email }?.let {
            if (LocalDateTime.now().isBefore(it.expiredDate)) return
        }

        // TODO change inviteUrl
        val isSuccess = mailSender.sendMail(
            type = SheltersdogMailType.SHELTER_ADMIN_INVITE,
            email = requestBody.email,
            params = mapOf(
                "{shelterName}" to shelter.name,
                "{inviteUrl}" to "https://sheltersdog.com"
            )
        )

        if (!isSuccess) {
            throw SheltersdogException(
                type = ExceptionType.SHELTER_ADMIN_INVITE_FAIL,
                variables = mapOf(
                    "email" to requestBody.email,
                    "shelterId" to requestBody.shelterId,
                )
            )
        }

        val invite = ShelterAdminInvite(
            email = requestBody.email,
            authorities = requestBody.authorities,
        )
        val copyInvites = shelter.shelterAdminInvites.toMutableList()
        copyInvites.add(invite)

        shelterRepository.updateById(
            id = requestBody.shelterId,
            updateFields = mapOf(Shelter::shelterAdminInvites to copyInvites)
        )
    }


}