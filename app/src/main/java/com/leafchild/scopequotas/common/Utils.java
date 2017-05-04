package com.leafchild.scopequotas.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.leafchild.scopequotas.data.Quota;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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

    public static List<Integer> getColorsForCharts() {

        ArrayList<Integer> colors = new ArrayList<>();

        for(int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        return colors;
    }

    public static SharedPreferences getDefaultSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isFieldEmpty(EditText field) {
        return field != null && field.getText().toString().isEmpty();
    }

    public static int calculateQuotaProgress(Quota quota) {
        return (int) (quota.getWorklogAmount() * 100 / quota.getMax());
    }
}
