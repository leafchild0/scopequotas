package com.leafchild.scopequotas.data

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import com.leafchild.scopequotas.common.Utils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by: leafchild
 * Date: 03/04/2017
 * Project: ScopeQuotas
 */

@DatabaseTable(tableName = Quota.TABLE_NAME_QUOTA)
class Quota {

    @DatabaseField(generatedId = true)
    var id: Long = 0
    @DatabaseField(columnName = "name", canBeNull = false, unique = true)
    var name: String? = null
    @DatabaseField(columnName = "quotaType", canBeNull = false)
    var quotaType: QuotaType? = null
    @DatabaseField(columnName = "description")
    var description: String? = null
    @DatabaseField(columnName = "min")
    var min: Int? = null
    @DatabaseField(columnName = "max")
    var max: Int? = null
    @DatabaseField(columnName = "createdDate")
    var createdDate: Date? = null
    @DatabaseField(columnName = "modifiedDate")
    var modifiedDate: Date? = null
    @DatabaseField(dataType = DataType.BOOLEAN_OBJ, columnName = "archived", defaultValue = "false")
    var archived: Boolean? = null
    @DatabaseField(columnName = "category", foreign = true, foreignAutoRefresh = true)
    var category: QuotaCategory? = null

    @ForeignCollectionField(columnName = "logged", eager = true)
    var logged: ForeignCollection<Worklog>? = null

    val isNew: Boolean
        get() = id <= 0

    val allWorklogAmount: Float?
        get() {

            var sum = 0f

            for (w in logged!!) {
                sum += Utils.transformWorklog(w)!!.toFloat()
            }
            return java.lang.Float.valueOf(String.format(Locale.getDefault(), "%.2f", sum))
        }

    val workFlowByLastPeriod: Float?
        get() {

            val from = Calendar.getInstance(Locale.UK)
            val to = Calendar.getInstance(Locale.UK)

            when (this.quotaType) {
                QuotaType.DAILY -> from.set(Calendar.HOUR_OF_DAY, 0)
                QuotaType.WEEKLY -> {
                    from.set(Calendar.HOUR_OF_DAY, 0)
                    from.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    to.set(Calendar.HOUR_OF_DAY, 23)
                    to.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                }
                QuotaType.MONTHLY -> {
                    from.set(Calendar.HOUR_OF_DAY, 0)
                    from.set(Calendar.DAY_OF_MONTH, 0)
                }
                else -> {
                    from.set(Calendar.HOUR_OF_DAY, 0)
                    from.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    to.set(Calendar.HOUR_OF_DAY, 23)
                    to.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                }
            }

            return Utils.calculateAmount(logged!!, from.time, to.time)
        }

    constructor() {}

    constructor(name: String, description: String, type: QuotaType) {

        this.name = name
        this.description = description
        this.quotaType = type
    }

    override fun toString(): String {
        //Have to use name only for spinners
        return name!!
    }

    override fun equals(o: Any?): Boolean {

        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val quota = o as Quota?
        return quota!!.hashCode() == this.hashCode()

    }

    override fun hashCode(): Int {

        return Objects.hash(name, quotaType, id)
    }

    fun toExportString(): Array<String> {

        val forExport = ArrayList<String>()
        forExport.add(id.toString())
        forExport.add(name!!)
        forExport.add(description!!)
        forExport.add(min.toString())
        forExport.add(max.toString())
        forExport.add(SimpleDateFormat("dd-MM-yyyy", Locale.US).format(modifiedDate))
        forExport.add(category!!.name!!)
        forExport.add(allWorklogAmount.toString())
        forExport.add(archived!!.toString().toUpperCase())

        return forExport.toTypedArray()
    }

    companion object {

        internal const val TABLE_NAME_QUOTA = "quota"
    }
}


