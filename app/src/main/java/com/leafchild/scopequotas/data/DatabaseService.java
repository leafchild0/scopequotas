package com.leafchild.scopequotas.data;

import android.content.Context;

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
            e.printStackTrace();
        }

        return found;
    }

    public boolean deleteQuota(Long id) {

        if(id == null) {
            return false;
        }

        boolean result = false;

        try {
            result = dbManager.getQuotaDao().delete(getQuota(id)) == 1;
        } catch(SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    private boolean updateQuota(Quota quota) {

        boolean result = false;

        try {
            quota.setModifiedDate(new Date());
            result = dbManager.getQuotaDao().update(quota) == 1;
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Quota> findAllQuotas() {

        List<Quota> allQuotas = new ArrayList<>();

        try {
            allQuotas = dbManager.getQuotaDao().queryForAll();
        } catch(SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return byType;
    }

    public void addWorklog(Worklog worklog) {

        try {
            worklog.setCreatedDate(new Date());
            dbManager.getWorklogDao().create(worklog);
        } catch(SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return byQuota;
    }

}
