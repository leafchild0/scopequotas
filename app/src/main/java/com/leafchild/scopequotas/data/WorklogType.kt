package com.leafchild.scopequotas.data

/**
 * Created by: leafchild
 * Date: 10/04/2017
 * Project: ScopeQuotas
 */

enum class WorklogType constructor(val value: String) {

    MINUTES("Minutes"),
    HOURS("Hours"),
    DAYS("Days");

    companion object {

        fun fromString(text: String): WorklogType? {

            for (b in WorklogType.values()) {
                if (b.value.equals(text, ignoreCase = true)) {
                    return b
                }
            }
            return null
        }

        fun fromOrdinal(n: Int): WorklogType {

            return values()[n]
        }
    }
}
