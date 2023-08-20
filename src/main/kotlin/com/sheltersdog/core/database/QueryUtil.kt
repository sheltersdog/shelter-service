package com.sheltersdog.core.database

import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.where
import kotlin.reflect.KProperty

fun updateQuery(updateFields: Map<KProperty<*>, Any?>): Update {
    var update = Update()
    updateFields.keys.map { key ->
        update = update.set(key.name, updateFields[key])
    }
    return update
}

fun Query.whereRegionCode(regionCode: Long, key: KProperty<*>) {
    if (regionCode == 0L) return

    val sidoCode = (regionCode / 1000_000_00) * 1000_000_00
    val sggCode = (regionCode / 1000_00) * 1000_00
    val umdCode = (regionCode / 100) * 100

    if (umdCode != sggCode) {
        this.addCriteria(
            where(key).gte(umdCode).lt(umdCode + 100)
        )
    } else if (sggCode != sidoCode) {
        this.addCriteria(
            where(key).gte(sggCode).lt(sggCode + 1000_00)
        )
    } else {
        this.addCriteria(
            where(key).gte(sidoCode).lt(sidoCode + 1000_000_00)
        )
    }
}
