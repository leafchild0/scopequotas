package com.leafchild.scopequotas.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.j256.ormlite.dao.ForeignCollection;
import com.leafchild.scopequotas.data.Worklog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by: leafchild
 * Date: 19/04/2017
 * Project: ScopeQuotas
 */

public class Utils {

	private static final Utils INSTANCE = new Utils();
	private SimpleDateFormat dayMonthFormatter = new SimpleDateFormat("dd-MM", Locale.US);
	private SimpleDateFormat dayMonthYearFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

	private Utils() {}

	public static SimpleDateFormat getDayMonthFormatter() {

		return INSTANCE.dayMonthFormatter;
	}

	public static SimpleDateFormat getDayMonthYearFormatter() {

		return INSTANCE.dayMonthYearFormatter;
	}

	public static List<Integer> getRandomColors() {

		ArrayList<Integer> colors = new ArrayList<>();

		for (int c : ColorTemplate.VORDIPLOM_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.JOYFUL_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.COLORFUL_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.LIBERTY_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.PASTEL_COLORS)
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

	public static int calculateQuotaProgress(Float amount, Integer max) {

		return (int) (amount * 100 / max);
	}

	public static Float calculateAmount(ForeignCollection<Worklog> logged, Date from, Date to) {

		float result = 0f;
		for (Worklog worklog : logged) {
			if (worklog.getCreatedDate().before(to)
				&& worklog.getCreatedDate().after(from)) {
				result += Utils.transformWorklog(worklog);
			}
		}
		return result;
	}

	public static Double transformWorklog(Worklog w) {

		double tempAmount = w.getAmount();
		switch (w.getType()) {
			case DAYS:
				tempAmount = tempAmount * 24;
				break;
			case HOURS:
				break;
			case MINUTES:
				tempAmount = tempAmount / 60;
				break;
			default:
		}
		return tempAmount;
	}
}
