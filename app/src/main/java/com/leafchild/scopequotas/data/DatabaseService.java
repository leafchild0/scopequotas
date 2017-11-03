package com.leafchild.scopequotas.data;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.leafchild.scopequotas.common.Utils.calculateAmount;

/**
 * Created by: leafchild
 * Date: 08/04/2017
 * Project: ScopeQuotas
 */

public class DatabaseService {

	private DBManager dbManager;

	public DatabaseService(Context context) {

		dbManager = new DBManager(context);
	}

	public void persistQuota(Quota quota) {

		if (quota.isNew()) { createQuota(quota); }
		else { updateQuota(quota); }
	}

	public Quota getQuota(Long id) {

		if (id == null) {
			return null;
		}
		Quota found = null;

		try {
			found = dbManager.getQuotaDao().queryForId(id);
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}

		return found;
	}

	public boolean archiveQuota(Long id) {

		if (id == null) return false;
		boolean result = false;

		try {
			Quota q = getQuota(id);
			q.setArchieved(true);
			result = dbManager.getQuotaDao().update(q) == 1;
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}

		return result;
	}

	private void createQuota(Quota quota) {

		try {
			Date date = new Date();
			quota.setCreatedDate(date);
			quota.setModifiedDate(date);

			dbManager.getQuotaDao().create(quota);
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}
	}

	private boolean updateQuota(Quota quota) {

		boolean result = false;

		try {
			quota.setModifiedDate(new Date());
			result = dbManager.getQuotaDao().update(quota) == 1;
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}

		return result;
	}

	public List<Quota> findAllQuotas() {

		List<Quota> allQuotas = new ArrayList<>();

		try {
			allQuotas = dbManager.getQuotaDao().queryForAll();
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}

		return allQuotas;
	}

	public List<Quota> findQuotasByType(QuotaType type, boolean showArchieved) {

		List<Quota> byType = new ArrayList<>();
		if (type == null) return byType;

		try {
			if (showArchieved) {
				byType = dbManager.getQuotaDao().queryForEq("quotaType", type);
			}
			else {
				byType = dbManager.getQuotaDao().queryBuilder().where().eq("quotaType", type)
								  .and().eq("archieved", false).query();
			}
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}

		return byType;
	}

	public void addWorklog(Worklog worklog) {

		try {
			if (worklog.getCreatedDate() == null) { worklog.setCreatedDate(new Date()); }
			dbManager.getWorklogDao().create(worklog);
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}
	}

	public List<Worklog> getByQuotaId(Long quotaId) {

		List<Worklog> byQuota = new ArrayList<>();
		if (quotaId == null) {
			return byQuota;
		}

		try {
			byQuota = dbManager.getWorklogDao().queryForEq("quota", quotaId);
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}

		return byQuota;
	}

	public void createCategory(QuotaCategory category) {

		try {
			Date date = new Date();
			category.setCreatedDate(date);

			dbManager.getCategoryDao().create(category);
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}
	}

	public boolean deleteCategory(Long id) {

		if (id == null) return false;
		boolean result = false;

		try {
			result = dbManager.getCategoryDao().delete(getCategory(id)) == 1;
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}

		return result;
	}

	public QuotaCategory getCategory(Long id) {

		if (id == null) {
			return null;
		}
		QuotaCategory found = null;

		try {
			found = dbManager.getCategoryDao().queryForId(id);
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}

		return found;
	}

	public List<QuotaCategory> findAllCategories() {

		List<QuotaCategory> allCategories = new ArrayList<>();

		try {
			allCategories = dbManager.getCategoryDao().queryForAll();
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}

		return allCategories;
	}

	public QuotaCategory getCategoryByName(String pickedCategory) {

		QuotaCategory found = null;

		try {
			found = dbManager.getCategoryDao().queryForEq("name", pickedCategory).get(0);
		}
		catch (SQLException e) {
			Log.e("DB", e.getMessage());
		}

		return found;
	}

	public Map<String, Float> getLoggedDataByCategory(Date from, Date to) {

		HashMap<String, Float> grouped = new HashMap<>();
		List<Quota> allQuotas = findAllQuotas();

		for (Quota quota : allQuotas) {
			String categoryName = quota.getCategory().getName();

			if (!grouped.containsKey(categoryName)) {
				grouped.put(categoryName, calculateAmount(quota.getLogged(), from, to));
			}
			else {
				grouped.put(categoryName, grouped.get(categoryName) + quota.getAllWorklogAmount());
			}
		}

		return grouped;
	}

	public Map<String, Float> getLoggedDataByType(Date from, Date to) {

		HashMap<String, Float> byType = new HashMap<>();
		List<Quota> allQuotas = findAllQuotas();

		for (Quota quota : allQuotas) {
			String type = quota.getQuotaType().getValue();
			if (!byType.containsKey(type)) {
				byType.put(type, calculateAmount(quota.getLogged(), from, to));
			}
			else {
				byType.put(type, byType.get(type) + quota.getAllWorklogAmount());
			}
		}

		return byType;
	}

	public Map<String, Float> getLoggedDataByName(Date from, Date to) {

		HashMap<String, Float> byName = new HashMap<>();
		List<Quota> allQuotas = findAllQuotas();

		for (Quota quota : allQuotas) {
			String name = quota.getName();
			if (!byName.containsKey(name)) {
				byName.put(name, calculateAmount(quota.getLogged(), from, to));
			}
			else {
				byName.put(name, byName.get(name) + quota.getAllWorklogAmount());
			}
		}

		return byName;
	}

	public List<Worklog> getLoggedDataByQuota(Quota quota, Date from, Date to) {

		List<Worklog> result = new ArrayList<>();

		for (Worklog worklog : quota.getLogged()) {
			if (worklog.getCreatedDate().before(to) && worklog.getCreatedDate().after(from)) {
				result.add(worklog);
			}
		}

		return result;
	}
}
