package com.leafchild.scopequotas.data;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        if(quota.isNew()) {
            createQuota(quota);
        }
        else {
            updateQuota(quota);
        }
    }

    public Quota getQuota(Long id) {

        if(id == null) {
            return null;
        }
        Quota found = null;

        try {
            found = dbManager.getQuotaDao().queryForId(id);
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }

        return found;
    }

    //TODO: Add delete operation
    public boolean deleteQuota(Long id) {

        if(id == null) {
            return false;
        }

        boolean result = false;

        try {
            result = dbManager.getQuotaDao().delete(getQuota(id)) == 1;
        } catch(SQLException e) {
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
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }
    }

    private boolean updateQuota(Quota quota) {

        boolean result = false;

        try {
            quota.setModifiedDate(new Date());
            result = dbManager.getQuotaDao().update(quota) == 1;
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }

        return result;
    }

    public List<Quota> findAllQuotas() {

        List<Quota> allQuotas = new ArrayList<>();

        try {
            allQuotas = dbManager.getQuotaDao().queryForAll();
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }

        return allQuotas;
    }

    public List<Quota> findQuotasByType(QuotaType type) {

        List<Quota> byType = new ArrayList<>();
        if(type == null) {
            return byType;
        }

        try {
            byType = dbManager.getQuotaDao().queryForEq("quotaType", type);
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }

        return byType;
    }

    public void addWorklog(Worklog worklog) {

        try {
            worklog.setCreatedDate(new Date());
            dbManager.getWorklogDao().create(worklog);
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }
    }

    public List<Worklog> getByQuotaId(Long quotaId) {

        List<Worklog> byQuota = new ArrayList<>();
        if(quotaId == null) {
            return byQuota;
        }

        try {
            dbManager.getWorklogDao().queryForEq("quota", quotaId);
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }

        return byQuota;
    }

    public void createCategory(QuotaCategory category) {

        try {
            Date date = new Date();
            category.setCreatedDate(date);

            dbManager.getCategoryDao().create(category);
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }
    }

    //TODO: Add delete operation
    public boolean deleteCategory(Long id) {

        if(id == null) {
            return false;
        }
        boolean result = false;

        try {
            result = dbManager.getCategoryDao().delete(getCategory(id)) == 1;
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }

        return result;
    }

    public QuotaCategory getCategory(Long id) {

        if(id == null) {
            return null;
        }
        QuotaCategory found = null;

        try {
            found = dbManager.getCategoryDao().queryForId(id);
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }

        return found;
    }

    public List<QuotaCategory> findAllCategories() {

        List<QuotaCategory> allCategories = new ArrayList<>();

        try {
            allCategories = dbManager.getCategoryDao().queryForAll();
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }

        return allCategories;
    }

}
