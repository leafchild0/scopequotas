package com.leafchild.scopequotas.common;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by: leafchild
 * Date: 19/04/2017
 * Project: ScopeQuotas
 */

public class Utils {
    private static SimpleDateFormat dayMonthFormatter = new SimpleDateFormat("dd-MM", Locale.US);
    private static SimpleDateFormat dayMonthYearFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    public static SimpleDateFormat getDayMonthFormatter() {
        return dayMonthFormatter;
    }

    public static SimpleDateFormat getDayMonthYearFormatter() {
        return dayMonthYearFormatter;
    }
}
