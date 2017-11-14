package com.leafchild.scopequotas.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import com.leafchild.scopequotas.common.NotificationsManager;
import com.leafchild.scopequotas.common.NotificationsReciever;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author leafchild
 *         Date 27/04/2017
 *         Project ScopeQuotas
 */

public class TimePreference extends DialogPreference {

	private TimePicker picker = null;
	private Calendar calendar;

	public TimePreference(Context ctxt, AttributeSet attrs) {

		super(ctxt, attrs);
		calendar = new GregorianCalendar();
	}

	@Override
	protected View onCreateDialogView() {

		picker = new TimePicker(getContext());
		picker.setIs24HourView(true);

		return (picker);
	}

	@Override
	protected void onBindDialogView(View v) {

		super.onBindDialogView(v);

		picker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
		picker.setMinute(calendar.get(Calendar.MINUTE));
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {

		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
			calendar.set(Calendar.MINUTE, picker.getMinute());

			if (callChangeListener(calendar.getTimeInMillis())) {
				persistLong(calendar.getTimeInMillis());
				notifyChanged();
			}

			setSummary(getSummary());
			NotificationsManager.getInstance().scheduleNotification(getContext(), NotificationsReciever.class, calendar
				.getTimeInMillis());
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {

		return (a.getString(index));
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

		if (restoreValue) {
			calendar.setTimeInMillis(getPersistedLong(System.currentTimeMillis()));
		}
		else {
			if (defaultValue == null) {
				calendar.setTimeInMillis(System.currentTimeMillis());
			}
			else {
				calendar.setTimeInMillis(Long.parseLong((String) defaultValue));
			}
		}
		setSummary(getSummary());
	}

	@Override
	public CharSequence getSummary() {

		if (calendar == null) return null;

		return DateFormat.getTimeFormat(getContext()).format(new Date(calendar.getTimeInMillis()));
	}

}