package com.leafchild.scopequotas.common

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.EditText
import com.github.mikephil.charting.utils.ColorTemplate
import com.j256.ormlite.dao.ForeignCollection
import com.leafchild.scopequotas.data.Worklog
import com.leafchild.scopequotas.data.WorklogType
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

/**
 * Created by: leafchild
 * Date: 19/04/2017
 * Project: ScopeQuotas
 */

object Utils {

    private val dayMonthFormatter = SimpleDateFormat("dd-MM", Locale.US)
    private val dayMonthYearFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private val exportHeaders = arrayOf("Id", "Name", "Description", "Min", "Max", "Last modified", "Category", "Logged", "Archived")

    fun getDayMonthFormatter(): SimpleDateFormat {

        return dayMonthFormatter
    }

    fun getDayMonthYearFormatter(): SimpleDateFormat {

        return dayMonthYearFormatter
    }

    fun getExportHeaders(): Array<String> {

        return exportHeaders
    }

    val randomColors: List<Int>
        get() {

            val colors = ArrayList<Int>()

            colors.addAll(ColorTemplate.VORDIPLOM_COLORS.asList())
            colors.addAll(ColorTemplate.JOYFUL_COLORS.asList())
            colors.addAll(ColorTemplate.COLORFUL_COLORS.asList())
            colors.addAll(ColorTemplate.LIBERTY_COLORS.asList())
            colors.addAll(ColorTemplate.PASTEL_COLORS.asList())
            colors.add(ColorTemplate.getHoloBlue())

            return colors.stream().distinct().collect(Collectors.toList())
        }

    fun getDefaultSharedPrefs(context: Context): SharedPreferences {

        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun isFieldEmpty(field: EditText?): Boolean {

        return field != null && field.text.toString().isEmpty()
    }

    fun calculateQuotaProgress(amount: Float?, max: Int?): Int {

        return (amount!! * 100 / max!!).toInt()
    }

    fun calculateAmount(logged: ForeignCollection<Worklog>, from: Date, to: Date): Float {

        var result = 0f
        for (worklog in logged) {
            if (worklog.createdDate!!.before(to) && worklog.createdDate!!.after(from)) {
                result += Utils.transformWorklog(worklog)!!.toFloat()
            }
        }
        return result
    }

    fun transformWorklog(w: Worklog): Double? {

        var tempAmount = w.amount!!
        when (w.type) {
            WorklogType.DAYS -> tempAmount *= 24
            WorklogType.HOURS -> {
            }
            WorklogType.MINUTES -> tempAmount /= 60
        }
        return tempAmount
    }

    // Weekly at Sat, 11 AM
    // Because of the locale
    val weeklyReminderTime: Long
        get() {

            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, 11)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.DAY_OF_WEEK, 7)

            return c.timeInMillis
        }
}
