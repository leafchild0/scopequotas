package com.leafchild.scopequotas.worklog;

import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.common.QuotaAdapter;
import com.leafchild.scopequotas.common.Utils;
import com.leafchild.scopequotas.data.DatabaseService;
import com.leafchild.scopequotas.data.Quota;
import com.leafchild.scopequotas.data.QuotaType;
import com.leafchild.scopequotas.data.Worklog;
import com.leafchild.scopequotas.data.WorklogType;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Calendar;
import java.util.List;

import static com.leafchild.scopequotas.AppContants.ACCENT_COLOR;
import static com.leafchild.scopequotas.AppContants.ACTIVE_QUOTA;
import static com.leafchild.scopequotas.AppContants.TYPE;

public class WorklogActivity extends AppCompatActivity
		implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

	private Button worklogDate;
	private Spinner inputType;

	private DatabaseService service;
	private Calendar calendar;
	private Quota picked = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_worklog);

		service = new DatabaseService(this);
		List<Quota> quotas = service.findQuotasByType(QuotaType.fromOrdinal(getIntent().getIntExtra(TYPE, 1)), false);

		Spinner dropdown = (Spinner) findViewById(R.id.quotas_list);
		dropdown.setAdapter(new QuotaAdapter(this,
				android.R.layout.simple_spinner_dropdown_item,
				quotas, false));
		dropdown.setOnItemSelectedListener(this);
		dropdown.setPrompt("Select query");
		dropdown.setSelection(quotas.indexOf(getQuotaPosition()));

		inputType = (Spinner) findViewById(R.id.worklog_amount_type);
		ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
				.createFromResource(this, R.array.worklog_array, android.R.layout.simple_spinner_item);
		inputType.setAdapter(staticAdapter);

		worklogDate = (Button) findViewById(R.id.worklog_date);
		worklogDate.setText("Today");
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		picked = (Quota) parent.getItemAtPosition(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	public void addWorklog(View view) {

		Worklog worklog = new Worklog(picked, getAddedAmount());
		worklog.setType(WorklogType.fromString(inputType.getSelectedItem().toString()));
		setWorklogDate(worklog);
		service.addWorklog(worklog);

		Toast.makeText(WorklogActivity.this, "Worklog was added", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(WorklogActivity.this::onBackPressed, 1000);
	}

	private Double getAddedAmount() {
		EditText amount = (EditText) findViewById(R.id.quota_amount);
		return Double.valueOf(amount.getText().toString());
	}

	private void setWorklogDate(Worklog worklog) {
		if (!worklogDate.getText().equals("Today")) {
			worklog.setCreatedDate(calendar.getTime());
		}
	}

	public void cancel(View view) {
		onBackPressed();
	}

	public void showWorklogDatePicker(View view) {
		calendar = Calendar.getInstance();
		DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
				WorklogActivity.this,
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH)
		);

		pickerDialog.setAccentColor(ACCENT_COLOR);
		pickerDialog.show(getFragmentManager(), "Change Date");
	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		calendar.set(year, monthOfYear, dayOfMonth);
		worklogDate.setText(Utils.getDayMonthYearFormatter().format(calendar.getTime()));
	}

	public Quota getQuotaPosition() {
		long activeQuota = getIntent().getLongExtra(ACTIVE_QUOTA, -1);

		return service.getQuota(activeQuota);
	}
}
