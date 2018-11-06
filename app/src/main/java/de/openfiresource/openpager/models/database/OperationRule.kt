package de.openfiresource.openpager.models.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "operation_rule")
data class OperationRule(
        var title: String,
        @PrimaryKey(autoGenerate = true)
        var id: Long?,
        var searchText: String? = null,
        var startTime: String = "00:00",
        var stopTime: String = "23:59",

        var isActivate: Boolean = true,
        var isOwnNotification: Boolean = false,

        var priority: Int = 1
) {
    @Ignore
    constructor(title: String) : this(title, null)
}
