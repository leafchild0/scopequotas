package com.leafchild.scopequotas.data;

import android.content.Context;
import android.util.Log;
import com.j256.ormlite.dao.ForeignCollection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.category;

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
            byQuota = dbManager.getWorklogDao().queryForEq("quota", quotaId);
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

    public QuotaCategory getCategoryByName(String pickedCategory) {
        QuotaCategory found = null;

        try {
            found = dbManager.getCategoryDao().queryForEq("name", pickedCategory).get(0);
        } catch(SQLException e) {
            Log.e("DB", e.getMessage());
        }

        return found;
    }

    public HashMap<String, Float> getLoggedDataByCategory(Date from, Date to) {

        HashMap<String, Float> grouped = new HashMap<>();
        List<Quota> allQuotas = findAllQuotas();

        for(Quota quota : allQuotas) {
            String categoryName = quota.getCategory().getName();

            if(!grouped.containsKey(categoryName)) {
                grouped.put(categoryName, calculateAmount(quota.getLogged(), from, to));
            }
            else {
                grouped.put(categoryName, grouped.get(categoryName) + quota.getWorklogAmount());
            }
        }

        return grouped;
    }

    private Float calculateAmount(ForeignCollection<Worklog> logged, Date from, Date to) {

        float result = 0f;

        for(Worklog worklog : logged) {
            if(worklog.getCreatedDate().before(to)
                && worklog.getCreatedDate().after(from)) {
                result += worklog.getAmount();
            }
        }

        return result;
    }

    public HashMap<String, Float> getLoggedDataByType(Date from, Date to) {
        HashMap<String, Float> byType = new HashMap<>();
        List<Quota> allQuotas = findAllQuotas();

        for(Quota quota : allQuotas) {
            String type = quota.getQuotaType().getValue();
            if(!byType.containsKey(type)) {
                byType.put(type, calculateAmount(quota.getLogged(), from, to));
            }
            else {
                byType.put(type, byType.get(type) + quota.getWorklogAmount());
            }
        }

        return byType;
    }

    public HashMap<String,Float> getLoggedDataByName(Date from, Date to) {

        HashMap<String, Float> byName = new HashMap<>();
        List<Quota> allQuotas = findAllQuotas();

        for(Quota quota : allQuotas) {
            String name = quota.getName();
            if(!byName.containsKey(name)) {
                byName.put(name, calculateAmount(quota.getLogged(), from, to));
            }
            else {
                byName.put(name, byName.get(name) + quota.getWorklogAmount());
            }
        }

        return byName;
    }
}
