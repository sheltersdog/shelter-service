package com.sheltersdog.shelter.mapper

import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.entity.ShelterSns
import com.sheltersdog.shelter.entity.model.ShelterStatus
import com.sheltersdog.shelter.entity.model.Sns
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ShelterMapperTest {

    @Test
    fun shelterToDtoTest() {
        val entity = Shelter(
            id = ObjectId.get(),
            name = "유반",
            profileImageUrl = "https://sheltersdog.com",
            contactNumber = null,
            isPrivateContact = true,
            address = null,
            detailAddress = "detailAddress",
            x = null,
            y = null,
            isPrivateDetailAddress = true,
            shelterSns = listOf(
                ShelterSns(
                    site = Sns.INSTAGRAM,
                    url = "siteUrl",
                )
            ),
            representativeSns = ShelterSns(
                site = Sns.INSTAGRAM,
                url = "siteUrl",
            ),
            donationPath = null,
            donationUsageHistoryLink = null,
            status = ShelterStatus.ACTIVE,
            searchKeyword = toString(),
        )

        val dto = shelterToDto(entity)
        assertEquals(entity.id.toString(), dto.id)
        assertEquals(entity.name, dto.name)
        assertEquals(entity.profileImageUrl, dto.profileImageUrl)
        assertEquals(entity.contactNumber, dto.contactNumber)
        assertEquals(entity.isPrivateContact, dto.isPrivateContact)
        assertEquals(entity.x, dto.x)
        assertEquals(entity.y, dto.y)
        assertEquals(entity.isPrivateDetailAddress, dto.isPrivateDetailAddress)
        assertEquals(entity.shelterSns, dto.shelterSns)
        assertEquals(entity.representativeSns, dto.representativeSns)
        assertEquals(entity.donationPath, dto.donationPath)
        assertEquals(entity.donationUsageHistoryLink, dto.donationUsageHistoryLink)
        assertEquals(entity.status, dto.status)

        if (dto.isPrivateDetailAddress) {
            assertNull(dto.detailAddress)
        } else {
            assertEquals(entity.detailAddress, dto.detailAddress)
        }

    }
}