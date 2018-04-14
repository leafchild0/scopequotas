package com.leafchild.scopequotas.data

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

import java.util.Date
import java.util.Objects

/**
 * Created by: leafchild
 * Date: 03/04/2017
 * Project: ScopeQuotas
 */

@DatabaseTable(tableName = Worklog.TABLE_NAME_WORKLOG)
class Worklog {

    @DatabaseField(generatedId = true)
    var id: Long? = null
    @DatabaseField(columnName = "createdDate")
    var createdDate: Date? = null
    @DatabaseField(columnName = "amount", canBeNull = false)
    var amount: Double? = null
    @DatabaseField(columnName = "amount_type", canBeNull = false)
    var type: WorklogType? = null
    @DatabaseField(columnName = "quota", foreign = true, foreignAutoRefresh = true)
    var quota: Quota? = null

    constructor() {}

    constructor(quota: Quota, amount: Double?) {

        this.quota = quota
        this.amount = amount
    }

    override fun toString(): String {

        return "Worklog{" +
                "id=" + id +
                ", createdDate=" + createdDate +
                ", amount=" + amount +
                ", amountType=" + type +
                '}'.toString()
    }

    override fun equals(o: Any?): Boolean {

        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val worklog = o as Worklog?
        return worklog == this

    }

    override fun hashCode(): Int {

        return Objects.hash(amount, createdDate, id)
    }

    companion object {

        internal const val TABLE_NAME_WORKLOG = "worklog"
    }
}
