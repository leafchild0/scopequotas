package com.leafchild.scopequotas.data;

/**
 * Created by: leafchild
 * Date: 03/04/2017
 * Project: ScopeQuotas
 */

public enum QuotaType {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly");

    QuotaType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public static QuotaType fromString(String text) {
        for (QuotaType b : QuotaType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public static QuotaType fromOrdinal(int n) {
        return values()[n];
    }
}
