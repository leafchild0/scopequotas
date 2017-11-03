package com.leafchild.scopequotas.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.Objects;

/**
 * @author leafchild
 *         Date 20/04/2017
 *         Project ScopeQuotas
 */

@DatabaseTable(tableName = QuotaCategory.TABLE_NAME_CATEGORY)
public class QuotaCategory {

	static final String TABLE_NAME_CATEGORY = "category";

	@DatabaseField(generatedId = true)
	private long id;
	@DatabaseField(columnName = "name", canBeNull = false, unique = true)
	private String name;
	@DatabaseField(columnName = "createdDate")
	private Date createdDate;

	@ForeignCollectionField(columnName = "quotas", eager = true)
	private ForeignCollection<Quota> quotas;

	public QuotaCategory() {}

	public QuotaCategory(String name) {

		this.name = name;
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

	public Date getCreatedDate() {

		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {

		this.createdDate = createdDate;
	}

	public ForeignCollection<Quota> getQuotas() {

		return quotas;
	}

	public void setQuotas(ForeignCollection<Quota> quotas) {

		this.quotas = quotas;
	}

	@Override
	public String toString() {

		return name;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		QuotaCategory that = (QuotaCategory) o;
		return Objects.equals(that, this);

	}

	@Override
	public int hashCode() {

		return Objects.hash(id, name);
	}
}
