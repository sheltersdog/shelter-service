package com.sheltersdog.core.util

import com.mongodb.client.result.UpdateResult
import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException

fun UpdateResult.ifUpdateFailThrow(
    variables: Map<String, Any?>,
    tableName: String,
): UpdateResult {
    if (this.wasAcknowledged()) return this

    val copyVariables = variables.toMutableMap()
    copyVariables["TableName"] = tableName
    val va = variables
    throw SheltersdogException(
        type = ExceptionType.DB_UPDATE_FAIL,
        variables = copyVariables,
    )

}