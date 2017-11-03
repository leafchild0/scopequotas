package com.leafchild.scopequotas.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.Objects;

/**
 * Created by: leafchild
 * Date: 03/04/2017
 * Project: ScopeQuotas
 */

@DatabaseTable(tableName = Worklog.TABLE_NAME_WORKLOG)
public class Worklog {

	static final String TABLE_NAME_WORKLOG = "worklog";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(columnName = "createdDate")
	private Date createdDate;
	@DatabaseField(columnName = "amount", canBeNull = false)
	private Double amount;
	@DatabaseField(columnName = "amount_type", canBeNull = false)
	private WorklogType type;
	@DatabaseField(columnName = "quota", foreign = true, foreignAutoRefresh = true)
	private Quota quota;

	public Worklog() {}

	public Worklog(Quota quota, Double amount) {

		this.quota = quota;
		this.amount = amount;
	}

	public Long getId() {

		return id;
	}

	public void setId(Long id) {

		this.id = id;
	}

	public Date getCreatedDate() {

		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {

		this.createdDate = createdDate;
	}

	public Double getAmount() {

		return amount;
	}

	public void setAmount(Double amount) {

		this.amount = amount;
	}

	public Quota getQuota() {

		return quota;
	}

	public void setQuota(Quota quota) {

		this.quota = quota;
	}

	@Override
	public String toString() {

		return "Worklog{" +
			"id=" + id +
			", createdDate=" + createdDate +
			", amount=" + amount +
			", amountType=" + type +
			'}';
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Worklog worklog = (Worklog) o;
		return Objects.equals(worklog, this);

	}

	@Override
	public int hashCode() {

		return Objects.hash(amount, createdDate, id);
	}

	public WorklogType getType() {

		return type;
	}

	public void setType(WorklogType type) {

		this.type = type;
	}
}
