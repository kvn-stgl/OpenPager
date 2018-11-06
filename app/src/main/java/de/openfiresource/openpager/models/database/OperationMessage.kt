package de.openfiresource.openpager.models.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import android.util.Pair
import java.text.DateFormat
import java.util.*

@Entity(tableName = "operation_message")
data class OperationMessage(
        var key: String,

        @PrimaryKey(autoGenerate = true)
        var id: Long? = null,

        var title: String = "",
        var message: String = "",
        var latlng: String? = null,

        var timestamp: Date? = null,
        var timestampIncoming: Date? = null,

        var seen: Boolean = false,
        var isAlarm: Boolean = false,

        @ForeignKey(entity = OperationRule::class, parentColumns = ["id"], childColumns = ["operationRuleId"], onDelete = ForeignKey.SET_NULL)
        var operationRuleId: Long? = null
) {
    @get:Ignore
    val latLngPair: Pair<Double, Double>?
        get() {
            if (TextUtils.isEmpty(latlng)) {
                return null
            }

            val latlng = this.latlng!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val lat = java.lang.Double.parseDouble(latlng[0])
            val lng = java.lang.Double.parseDouble(latlng[1])

            return Pair(lat, lng)
        }

    @get:Ignore
    val operationTime: String
        get() = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(timestamp)

    @Ignore
    constructor() : this(UUID.randomUUID().toString()) {
    }

    /**
     * Do not use this to save the rule into the db.
     *
     */
    @Deprecated("")
    fun setOperationRule(operationRule: OperationRule) {
        this.rule = operationRule
    }

    /**
     * Important: Do not use this to get the current rule from the DB.
     * It's only for temporary saving in a attribute of the class.
     *
     * @return current rule
     */
    @Ignore
    @get:Deprecated("")
    var rule: OperationRule? = null
        private set

}
