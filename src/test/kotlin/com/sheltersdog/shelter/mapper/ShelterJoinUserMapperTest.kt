package com.sheltersdog.shelter.mapper

import com.sheltersdog.shelter.entity.ShelterJoinUser
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class ShelterJoinUserMapperTest {

    @Test
    fun shelterJoinUserToDtoTest() {
        val entity = ShelterJoinUser(
            name = "김보성",
            nickname = "유반",
            authorities = listOf(
                ShelterAuthority.ADMIN
            ),
            userId = ObjectId.get().toString(),
            email = "sheltersdog@gmail.com",
            profileImageUrl = "https://sheltersdog.com/image",
            status = true,
        )

        val dto = shelterJoinUserToDto(entity)
        assertEquals(entity.name, dto.name)
        assertEquals(entity.nickname, dto.nickname)
        assertEquals(entity.authorities, dto.authorities)
        assertEquals(entity.userId, dto.userId)
        assertEquals(entity.email, dto.email)
        assertEquals(entity.profileImageUrl, dto.profileImageUrl)
        assertEquals(entity.status, dto.status)
    }
}