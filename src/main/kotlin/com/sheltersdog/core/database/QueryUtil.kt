package com.sheltersdog.core.database

import org.springframework.data.mongodb.core.query.Update
import kotlin.reflect.KProperty

fun updateQuery(updateFields: Map<KProperty<*>, Any?>): Update {
    var update = Update()
    updateFields.keys.map { key ->
        update = update.set(key.name, updateFields[key])
    }
    return update
}

