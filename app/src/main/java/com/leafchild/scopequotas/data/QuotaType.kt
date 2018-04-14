package com.leafchild.scopequotas.data

/**
 * Created by: leafchild
 * Date: 03/04/2017
 * Project: ScopeQuotas
 */

enum class QuotaType constructor(val value: String) {

    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly");

    companion object {

        fun fromString(text: String): QuotaType? {

            for (b in QuotaType.values()) {
                if (b.value.equals(text, ignoreCase = true)) {
                    return b
                }
            }
            return null
        }

        fun fromOrdinal(n: Int): QuotaType {

            return values()[n]
        }
    }
}
