package com.sheltersdog.address.entity

import com.sheltersdog.address.model.AddressType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.log.LogMessage
import com.sheltersdog.core.log.exceptionMessage
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.http.HttpStatus
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
    logMessage: LogMessage = LogMessage.NOT_FOUND_ADDRESS,
    exceptionMessage: String? = null,
    variables: Map<String, Any?>,
): Address {
    if (this != null) return this

    val log = LoggerFactory.getLogger(Thread.currentThread().stackTrace[2].className)
    log.debug(logMessage.description, variables.toString())
    val message = exceptionMessage ?: logMessage.exceptionMessage().description
    throw SheltersdogException(
        message = message,
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    )
}
