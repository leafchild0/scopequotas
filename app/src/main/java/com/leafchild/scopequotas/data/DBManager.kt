package com.leafchild.scopequotas.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.AndroidRuntimeException
import android.util.Log
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils

import java.sql.SQLException

/**
 * Created by: leafchild
 * Date: 03/04/2017
 * Project: ScopeQuotas
 */

internal class DBManager(context: Context) : OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private var quotaDao: Dao<Quota, Long>? = null
    private var worklogDao: Dao<Worklog, Long>? = null
    private var categoryDao: Dao<QuotaCategory, Long>? = null

    override fun onCreate(db: SQLiteDatabase, connectionSource: ConnectionSource) {

        try {
            TableUtils.createTable(connectionSource, Quota::class.java)
            TableUtils.createTable(connectionSource, Worklog::class.java)
            TableUtils.createTable(connectionSource, QuotaCategory::class.java)
            addUncategorizedCategory()
        } catch (e: SQLException) {
            throw AndroidRuntimeException(e)
        }

    }

    private fun addUncategorizedCategory() {

        try {
            val uncategorized = QuotaCategory("Uncategorized")
            getCategoryDao()!!.create(uncategorized)
        } catch (e: SQLException) {
            Log.e("DB", e.message)
        }

    }

    override fun onUpgrade(db: SQLiteDatabase, connectionSource: ConnectionSource,
                           oldVersion: Int, newVersion: Int) {

        try {
            TableUtils.dropTable<Quota, Any>(connectionSource, Quota::class.java, true)
            TableUtils.dropTable<Worklog, Any>(connectionSource, Worklog::class.java, true)
            TableUtils.dropTable<QuotaCategory, Any>(connectionSource, QuotaCategory::class.java, true)
            onCreate(db, connectionSource)
        } catch (e: SQLException) {
            throw AndroidRuntimeException(e)
        }

    }

    fun getQuotaDao(): Dao<Quota, Long>? {

        if (quotaDao == null) {
            try {
                quotaDao = getDao(Quota::class.java)
            } catch (e: SQLException) {
                Log.e("DB", e.message)
            }

        }

        return quotaDao
    }

    fun getWorklogDao(): Dao<Worklog, Long>? {

        if (worklogDao == null) {
            try {
                worklogDao = getDao(Worklog::class.java)
            } catch (e: SQLException) {
                Log.e("DB", e.message)
            }

        }

        return worklogDao
    }

    fun getCategoryDao(): Dao<QuotaCategory, Long>? {

        if (categoryDao == null) {
            try {
                categoryDao = getDao(QuotaCategory::class.java)
            } catch (e: SQLException) {
                Log.e("DB", e.message)
            }

        }

        return categoryDao
    }

    override fun close() {

        quotaDao = null
        worklogDao = null
        categoryDao = null

        super.close()
    }

    companion object {

        private const val DATABASE_NAME = "scope.quotas.db"
        private const val DATABASE_VERSION = 8
    }

}
