package com.sheltersdog.shelter.util

import com.sheltersdog.shelter.entity.ShelterJoinUser
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.User

suspend fun hasAuthority(
    shelterAdmins: List<ShelterJoinUser>,
    userId: String? = null,
    shelterAuthorities: List<ShelterAuthority>,
): Boolean {
    val checkUserId = userId
        ?: (ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User).username

    val shelterJoinUser = shelterAdmins.firstOrNull { admin ->
        admin.userId == checkUserId
    } ?: return false

    shelterAuthorities.firstOrNull { shelterAuthority ->
        shelterJoinUser.authorities.contains(shelterAuthority)
    } ?: return false

    return true
}