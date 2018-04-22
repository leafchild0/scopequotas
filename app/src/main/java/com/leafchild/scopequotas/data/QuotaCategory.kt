package com.leafchild.scopequotas.data

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable

import java.util.Date
import java.util.Objects

/**
 * @author leafchild
 * Date 20/04/2017
 * Project ScopeQuotas
 */

@DatabaseTable(tableName = QuotaCategory.TABLE_NAME_CATEGORY)
class QuotaCategory {

    @DatabaseField(generatedId = true)
    var id: Long = 0
    @DatabaseField(columnName = "name", canBeNull = false, unique = true)
    var name: String? = null
    @DatabaseField(columnName = "createdDate")
    var createdDate: Date? = Date()

    @ForeignCollectionField(columnName = "quotas", eager = true)
    var quotas: ForeignCollection<Quota>? = null

    constructor() {}

    constructor(name: String) {

        this.name = name
    }

    override fun toString(): String {

        return name!!
    }

    override fun equals(o: Any?): Boolean {

        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val that = o as QuotaCategory?
        return that == this

    }

    override fun hashCode(): Int {

        return Objects.hash(id, name)
    }

    companion object {

        internal const val TABLE_NAME_CATEGORY = "category"
    }
}
