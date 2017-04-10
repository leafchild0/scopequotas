package com.leafchild.scopequotas.data;

import android.os.Build;
import android.support.annotation.RequiresApi;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.Objects;

/**
 * Created by: leafchild
 * Date: 03/04/2017
 * Project: ScopeQuotas
 */

@DatabaseTable(tableName = Quota.TABLE_NAME_QUOTA)
public class Quota {

    final static String TABLE_NAME_QUOTA = "quota";

    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(columnName = "name", canBeNull = false, unique = true)
    private String name;
    @DatabaseField(columnName = "quotaType", canBeNull = false)
    private QuotaType quotaType;
    @DatabaseField(columnName = "description")
    private String description;
    @DatabaseField(columnName = "createdDate")
    private Date createdDate;
    @DatabaseField(columnName = "modifiedDate")
    private Date modifiedDate;

    @ForeignCollectionField(columnName = "logged", eager = true)
    private ForeignCollection<Worklog> logged;

    public Quota() {
    }

    @Override
    public String toString() {
        return "Quota{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", quotaType=" + quotaType +
            '}';
    }

    public ForeignCollection<Worklog> getLogged() {
        return logged;
    }

    public void setLogged(ForeignCollection<Worklog> logged) {
        this.logged = logged;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QuotaType getQuotaType() {
        return quotaType;
    }

    public void setQuotaType(QuotaType quotaType) {
        this.quotaType = quotaType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Quota quota = (Quota) o;
        return Objects.equals(quota, this);

    }

    @Override
    public int hashCode() {
        return Objects.hash(name, quotaType, id);
    }

    public boolean isNew() {
        return id > -1;
    }

    public Double getWorklogAmount() {

        double sum = 0;

        for(Worklog w : logged) {
            sum += w.getAmount();
        }
        return sum;
    }

    public void addWorklog(Worklog worklog) {
        logged.add(worklog);
    }
}


