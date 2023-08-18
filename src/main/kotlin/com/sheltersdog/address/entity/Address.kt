package com.sheltersdog.address.entity

import com.sheltersdog.address.model.AddressType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.log.LogMessage
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class Address(
    @Id
    val id: ObjectId? = null,

    @Indexed(unique = true)
    val regionCd: Long,
    val regionName: String,
    val type: AddressType = AddressType.SIDO,

    val sidoCd: String,
    val sidoName: String,
    val sggCd: String? = null,
    val sggName: String? = null,
    val umdCd: String? = null,
    val umdName: String? = null,
    val riCd: String? = null,
    val riName: String? = null,

    val createdDate: LocalDate? = null,
    val deletedDate: LocalDate? = null,
)

fun Address?.ifNullThrow(
    variables: Map<String, Any?>,
): Address {
    if (this != null) return this

    throw SheltersdogException(
        logMessage = LogMessage.NOT_FOUND_ADDRESS,
        variables = variables,
    )
}
