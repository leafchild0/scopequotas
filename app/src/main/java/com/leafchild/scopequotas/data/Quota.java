package com.leafchild.scopequotas.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.leafchild.scopequotas.common.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.R.attr.type;
import static com.leafchild.scopequotas.data.QuotaType.DAILY;
import static com.leafchild.scopequotas.data.QuotaType.MONTHLY;
import static com.leafchild.scopequotas.data.QuotaType.WEEKLY;

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
    @DatabaseField(columnName = "min")
    private Integer min;
    @DatabaseField(columnName = "max")
    private Integer max;
    @DatabaseField(columnName = "createdDate")
    private Date createdDate;
    @DatabaseField(columnName = "modifiedDate")
    private Date modifiedDate;
    @DatabaseField(columnName = "category", foreign = true, foreignAutoRefresh = true)
    private QuotaCategory category;

    @ForeignCollectionField(columnName = "logged", eager = true)
    private ForeignCollection<Worklog> logged;

    public Quota() {
    }

    public Quota(String name, String description, QuotaType type) {
        this.name = name;
        this.description = description;
        this.quotaType = type;
    }

    @Override
    public String toString() {
        //Have to use name only for spinners
        return name;
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
        return id <= 0;
    }

    public Float getAllWorklogAmount() {

        float sum = 0;

        for(Worklog w : logged) {
            sum += Utils.transformWorklog(w);
        }
        return Float.valueOf(String.format(Locale.getDefault(), "%.2f", sum));
    }

    public Float getWorkFlowByLastPeriod() {
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        switch(this.getQuotaType()) {
            case DAILY:
                from.set(Calendar.HOUR_OF_DAY, 0);
                break;
            case WEEKLY:
                from.set(Calendar.HOUR_OF_DAY, 0);
                from.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case MONTHLY:
                from.set(Calendar.HOUR_OF_DAY, 0);
                from.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                from.set(Calendar.MONTH, from.get(Calendar.MONTH));
                break;
            default:
                return 0f;
        }

        return Utils.calculateAmount(logged, from.getTime(), to.getTime());
    }

    public void addWorklog(Worklog worklog) {
        logged.add(worklog);
    }

    public QuotaCategory getCategory() {
        return category;
    }

    public void setCategory(QuotaCategory category) {
        this.category = category;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }
}


