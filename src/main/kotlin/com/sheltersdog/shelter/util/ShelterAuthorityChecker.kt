package com.sheltersdog.shelter.util

import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.entity.ShelterJoinUser
import com.sheltersdog.shelter.entity.model.ShelterAuthority
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.User

suspend fun Shelter.hasAuthorityOrThrow(
    userId: String? = null,
    shelterAuthorities: List<ShelterAuthority>,
) {
    this.sheltersAdmins.hasAuthorityOrThrow(
        shelterId = this.id.toString(),
        userId = userId,
        shelterAuthorities = shelterAuthorities,
    )
}

suspend fun List<ShelterJoinUser>.hasAuthorityOrThrow(
    shelterId: String,
    userId: String? = null,
    shelterAuthorities: List<ShelterAuthority>,
) {
    val hasAuthority = this.hasAuthority(
        userId = userId,
        shelterAuthorities = shelterAuthorities,
    )

    if (hasAuthority) return

    val checkUserId = userId
        ?: (ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User).username

    throw SheltersdogException(
        exceptionType = ExceptionType.ACCESS_DENIED,
        variables = mapOf(
            "userId" to checkUserId,
            "shelterId" to shelterId,
            "shelterAuthority" to shelterAuthorities
        )
    )
}

suspend fun List<ShelterJoinUser>.hasAuthority(
    userId: String? = null,
    shelterAuthorities: List<ShelterAuthority>,
): Boolean {
    val checkUserId = userId
        ?: (ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.principal as User).username

    val shelterJoinUser = this.firstOrNull { admin ->
        admin.userId == checkUserId
    } ?: return false

    shelterAuthorities.firstOrNull { shelterAuthority ->
        shelterJoinUser.authorities.contains(shelterAuthority)
    } ?: return false

    return true
}