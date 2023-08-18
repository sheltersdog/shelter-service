package com.sheltersdog.core.util

import com.mongodb.client.result.UpdateResult
import com.sheltersdog.core.log.LogMessage
import com.sheltersdog.core.log.loggingAndException

fun UpdateResult.updateCheck(
    logMessage: LogMessage = LogMessage.DB_UPDATE_FAIL,
    exceptionMessage: String? = null,
    variables: Map<String, Any?>
) {
    if (this.wasAcknowledged()) return

    throw logMessage.loggingAndException(
        exceptionMessage = exceptionMessage,
        variables = variables,
    )

}