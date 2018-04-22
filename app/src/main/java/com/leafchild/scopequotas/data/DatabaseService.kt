package com.leafchild.scopequotas.data

import android.content.Context
import android.util.Log
import com.leafchild.scopequotas.common.Utils
import java.sql.SQLException
import java.util.*
import java.util.stream.Collectors

/**
 * Created by: leafchild
 * Date: 08/04/2017
 * Project: ScopeQuotas
 */

class DatabaseService(context: Context) {

    private val dbManager: DBManager = DBManager(context)

    fun persistQuota(quota: Quota) {

        if (quota.isNew) createQuota(quota)
        else updateQuota(quota)
    }

    fun getQuota(id: Long?): Quota? {

        if (id == null) return null

        var found: Quota? = null

        try {
            found = dbManager.getQuotaDao()!!.queryForId(id)
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

        return found
    }

    fun archiveQuota(id: Long?): Boolean {

        if (id == null) return false
        var result = false

        try {
            val q = getQuota(id)
            q!!.archived = true
            result = dbManager.getQuotaDao()!!.update(q) == 1
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

        return result
    }

    private fun createQuota(quota: Quota) {

        try {
            val date = Date()
            quota.createdDate = date
            quota.modifiedDate = date

            dbManager.getQuotaDao()!!.create(quota)
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

    }

    private fun updateQuota(quota: Quota) {

        try {
            quota.modifiedDate = Date()
            dbManager.getQuotaDao()!!.update(quota)
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

    }

    fun findAllQuotas(): List<Quota> {

        var allQuotas: List<Quota> = ArrayList()

        try {
            allQuotas = dbManager.getQuotaDao()!!.queryForAll()
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

        return allQuotas
    }

    fun findQuotasByType(type: QuotaType?, showArchieved: Boolean): List<Quota> {

        var byType: List<Quota> = ArrayList()
        if (type == null) return byType

        try {
            byType = if (showArchieved) {
                dbManager.getQuotaDao()!!.queryForEq("quotaType", type)
            } else {
                dbManager.getQuotaDao()!!.queryBuilder().where().eq("quotaType", type)
                        .and().eq("archived", false).query()
            }
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

        return byType
    }

    fun addWorklog(worklog: Worklog) {

        try {
            dbManager.getWorklogDao()!!.create(worklog)
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

    }

    fun addWorklogs(worklogs: List<Worklog>) {

        try {
            dbManager.getWorklogDao()!!.create(worklogs)
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

    }

    fun createCategory(category: QuotaCategory) {

        try {
            val date = Date()
            category.createdDate = date

            dbManager.getCategoryDao()!!.create(category)
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

    }

    fun deleteCategory(id: Long?): Boolean {

        if (id == null) return false
        var result = false

        try {
            result = dbManager.getCategoryDao()!!.delete(getCategory(id)) == 1
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

        return result
    }

    private fun getCategory(id: Long?): QuotaCategory? {

        if (id == null) return null

        var found: QuotaCategory? = null

        try {
            found = dbManager.getCategoryDao()!!.queryForId(id)
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

        return found
    }

    fun findAllCategories(): List<QuotaCategory> {

        var allCategories: List<QuotaCategory> = ArrayList()

        try {
            allCategories = dbManager.getCategoryDao()!!.queryForAll()
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

        return allCategories
    }

    fun getCategoryByName(pickedCategory: String): QuotaCategory? {

        var found: QuotaCategory? = null

        try {
            found = dbManager.getCategoryDao()!!.queryForEq("name", pickedCategory)[0]
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

        return found
    }

    fun getLoggedDataByCategory(from: Date, to: Date): Map<String, Float> {

        val grouped = HashMap<String, Float>()
        val allQuotas = findAllQuotas()

        for (quota in allQuotas) {
            val categoryName = quota.category!!.name

            if (!grouped.containsKey(categoryName)) {
                grouped[categoryName!!] = Utils.calculateAmount(quota.logged!!, from, to)
            } else {
                grouped[categoryName!!] = grouped[categoryName]!! + quota.allWorklogAmount!!
            }
        }

        return grouped
    }

    fun getAllLoggedDataByType(from: Date, to: Date): Map<String, Float> {

        val byType = HashMap<String, Float>()
        val allQuotas = findAllQuotas()

        for (quota in allQuotas) {
            val type = quota.quotaType!!.value

            if (!byType.containsKey(type)) {
                byType[type] = Utils.calculateAmount(quota.logged!!, from, to)
            } else {
                byType[type] = byType[type]!! + quota.allWorklogAmount!!
            }
        }

        return byType
    }

    fun getLoggedDataByType(type: QuotaType, from: Date, to: Date): Map<String, Float> {

        val byType = HashMap<String, Float>()
        val allQuotas = findQuotasByType(type, true)

        allQuotas.forEach {
            byType[it.name!!] = Utils.calculateAmount(it.logged!!, from, to)
        }

        return byType
    }

    fun getLoggedDataByName(from: Date, to: Date): Map<String, Float> {

        val byName = HashMap<String, Float>()
        val allQuotas = findAllQuotas()

        for (quota in allQuotas) {
            val name = quota.name
            if (!byName.containsKey(name)) {
                byName[name!!] = Utils.calculateAmount(quota.logged!!, from, to)
            } else {
                byName[name!!] = byName[name]!! + quota.allWorklogAmount!!
            }
        }

        return byName
    }

    fun getLoggedDataByQuota(quota: Quota, from: Date, to: Date): List<Worklog> {

        return quota.logged!!.stream()
                .filter({ it.createdDate!!.before(to) && it.createdDate!!.after(from) })
                .collect(Collectors.toList())

    }
}
