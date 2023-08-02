package com.sheltersdog.shelter.util

import com.sheltersdog.shelter.entity.ShelterJoinUser
import com.sheltersdog.shelter.entity.model.ShelterAuthority

fun hasAuthority(
    shelterAdmins: List<ShelterJoinUser>,
    userId: String,
    shelterAuthorities: List<ShelterAuthority>,
): Boolean {
    val shelterJoinUser = shelterAdmins.firstOrNull { admin -> admin.userId == userId }

    shelterAuthorities.forEach { shelterAuthority ->
        val result = shelterJoinUser?.authorities?.contains(shelterAuthority)
        if (result != null && result) return true
    }

    return false
}