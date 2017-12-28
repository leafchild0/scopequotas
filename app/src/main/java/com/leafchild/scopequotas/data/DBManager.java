package com.leafchild.scopequotas.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.AndroidRuntimeException;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by: leafchild
 * Date: 03/04/2017
 * Project: ScopeQuotas
 */

class DBManager extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "scope.quotas.db";
	private static final int DATABASE_VERSION = 8;
	private Dao<Quota, Long> quotaDao = null;
	private Dao<Worklog, Long> worklogDao = null;
	private Dao<QuotaCategory, Long> categoryDao = null;

	DBManager(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

		try {
			TableUtils.createTable(connectionSource, Quota.class);
			TableUtils.createTable(connectionSource, Worklog.class);
			TableUtils.createTable(connectionSource, QuotaCategory.class);
			addUncategorizedCategory();
		}
		catch (SQLException e) {
			throw new AndroidRuntimeException(e);
		}
	}

	private void addUncategorizedCategory() {

		try {
			QuotaCategory uncategorized = new QuotaCategory("Uncategorized");
			getCategoryDao().create(uncategorized);
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
		int oldVersion, int newVersion) {

		try {
			TableUtils.dropTable(connectionSource, Quota.class, true);
			TableUtils.dropTable(connectionSource, Worklog.class, true);
			TableUtils.dropTable(connectionSource, QuotaCategory.class, true);
			onCreate(db, connectionSource);
		}
		catch (SQLException e) {
			throw new AndroidRuntimeException(e);
		}
	}

	Dao<Quota, Long> getQuotaDao() {

		if (quotaDao == null) {
			try {
				quotaDao = getDao(Quota.class);
			}
			catch (SQLException e) {
				Log.e("DB", e.getMessage());
			}
		}

		return quotaDao;
	}

	Dao<Worklog, Long> getWorklogDao() {

		if (worklogDao == null) {
			try {
				worklogDao = getDao(Worklog.class);
			}
			catch (SQLException e) {
				Log.e("DB", e.getMessage());
			}
		}

		return worklogDao;
	}

	Dao<QuotaCategory, Long> getCategoryDao() {

		if (categoryDao == null) {
			try {
				categoryDao = getDao(QuotaCategory.class);
			}
			catch (SQLException e) {
				Log.e("DB", e.getMessage());
			}
		}

		return categoryDao;
	}

	@Override
	public void close() {

		quotaDao = null;
		worklogDao = null;
		categoryDao = null;

		super.close();
	}

}
