package com.leafchild.scopequotas.reports;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.common.QuotaAdapter;
import com.leafchild.scopequotas.common.QuotasWithDefaultAdapter;
import com.leafchild.scopequotas.common.Utils;
import com.leafchild.scopequotas.data.DatabaseService;
import com.leafchild.scopequotas.data.Quota;
import com.leafchild.scopequotas.data.QuotaType;
import com.leafchild.scopequotas.data.Worklog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.leafchild.scopequotas.AppContants.ACCENT_COLOR;
import static com.leafchild.scopequotas.AppContants.TYPE;
import static com.leafchild.scopequotas.common.Utils.getRandomColors;

public class ReportsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

	private DatePickerDialog fromD;
	private Spinner quotaName;
	private Spinner typeName;
	private Button fromDate;
	private Button toDate;

	private PieChart byCategory;
	private BarChart byName;
	private HorizontalBarChart byType;

	private String type = BY_CATEGORY;
	private Calendar from = Calendar.getInstance();
	private Calendar to = Calendar.getInstance();
	private QuotaType passedType;

	private DatabaseService service;

	private static final String BY_CATEGORY = "By Category";
	private static final String BY_NAME = "By Name";
	private static final String BY_TYPE = "By Type";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reports);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		service = new DatabaseService(this);

		Spinner reportBy = (Spinner) findViewById(R.id.report_by);
		ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
			.createFromResource(this, R.array.reports_category, android.R.layout.simple_spinner_item);
		reportBy.setAdapter(staticAdapter);
		reportBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				type = (String) parent.getItemAtPosition(position);
				refreshReports();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		toDate = (Button) findViewById(R.id.reports_to);
		toDate.setText(Utils.getDayMonthYearFormatter().format(to.getTime()));

		from.add(Calendar.WEEK_OF_MONTH, -1);
		fromDate = (Button) findViewById(R.id.reports_from);
		fromDate.setText(Utils.getDayMonthYearFormatter().format(from.getTime()));

		byCategory = (PieChart) findViewById(R.id.category_chart);
		byName = (BarChart) findViewById(R.id.name_chart);
		byType = (HorizontalBarChart) findViewById(R.id.type_chart);
		quotaName = (Spinner) findViewById(R.id.report_by_name);
		typeName = (Spinner) findViewById(R.id.report_by_type);

		getPassedType(reportBy);

		refreshReports();
	}

	private void getPassedType(Spinner reportBy) {

		String passed = getIntent().getStringExtra(TYPE);
		if (passed != null) {
			reportBy.setSelection(2);
			type = BY_TYPE;
			passedType = QuotaType.fromString(passed);
		}
	}

	public void showFromDatePicker(View view) {

		fromD = DatePickerDialog.newInstance(
			ReportsActivity.this,
			from.get(Calendar.YEAR),
			from.get(Calendar.MONTH),
			from.get(Calendar.DAY_OF_MONTH)
		);
		fromD.setAccentColor(ACCENT_COLOR);
		fromD.show(getFragmentManager(), "Choose From Date");
	}

	public void showToDatePicker(View view) {

		DatePickerDialog toD = DatePickerDialog.newInstance(
			ReportsActivity.this,
			to.get(Calendar.YEAR),
			to.get(Calendar.MONTH),
			to.get(Calendar.DAY_OF_MONTH)
		);

		toD.setAccentColor(ACCENT_COLOR);
		toD.show(getFragmentManager(), "Choose To Date");
	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int month, int dayOfMonth) {

		if (view.getTag().equals(fromD.getTag())) {
			//From date
			from.set(year, month, dayOfMonth);
			fromDate.setText(Utils.getDayMonthYearFormatter().format(from.getTime()));
		}
		else {
			//To date
			to.set(year, month, dayOfMonth);
			toDate.setText(Utils.getDayMonthYearFormatter().format(to.getTime()));
		}

		refreshReports();
	}

	private void refreshReports() {

		switch (type) {
			case BY_CATEGORY:
				hideCharts(byName, byType);
				hideQuotaSelector();
				hideTypeSelector();
				showDataByCategory();
				break;
			case BY_NAME:
				hideCharts(byCategory, byType);
				showDataByName();
				hideTypeSelector();
				break;
			case BY_TYPE:
				hideCharts(byCategory, byName);
				hideQuotaSelector();
				showDataByType();
				break;
			default:
				showDataByCategory();
		}
	}

	private void hideQuotaSelector() {

		if (quotaName != null) { quotaName.setVisibility(View.GONE); }
	}

	private void hideTypeSelector() {

		if (typeName != null) { typeName.setVisibility(View.GONE); }
	}

	private void showDataByName() {

		if (quotaName.getVisibility() == View.VISIBLE
			&& quotaName.getSelectedItem() != null) {
			refreshReportsWithQuota((Quota) quotaName.getSelectedItem());
		}
		else {
			initQueryAdapterForNameReports();

			byName.setDrawBarShadow(false);
			byName.setDrawValueAboveBar(true);
			byName.getDescription().setEnabled(false);
			// scaling can now only be done on x- and y-axis separately
			byName.setPinchZoom(false);
			byName.setDrawGridBackground(false);

			byName.getXAxis().setEnabled(false);

			YAxis yl = byName.getAxisLeft();
			yl.setDrawAxisLine(true);
			yl.setDrawGridLines(true);
			yl.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
			yl.setAxisMinimum(0f);

			YAxis yr = byName.getAxisRight();
			yr.setDrawAxisLine(true);
			yr.setDrawGridLines(false);
			yr.setAxisMinimum(0f);

			byName.setData(getChartData(service.getLoggedDataByName(from.getTime(), to.getTime())));

			byName.setFitBars(true);
			byName.animateY(1500);
			byName.setMaxVisibleValueCount(20);

			Legend l = byName.getLegend();
			l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
			l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
			l.setOrientation(Legend.LegendOrientation.VERTICAL);
			l.setDrawInside(false);
			l.setFormSize(12f);
			l.setXEntrySpace(4f);
		}

		byName.setVisibility(View.VISIBLE);
	}

	private void initQueryAdapterForNameReports() {

		QuotaAdapter adapter = new QuotaAdapter(this, android.R.layout.simple_spinner_dropdown_item,
			service.findAllQuotas(), false);
		quotaName.setAdapter(new QuotasWithDefaultAdapter(adapter, R.layout.spinner_row_nothing_selected, this));

		quotaName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				Quota selected = (Quota) parent.getItemAtPosition(position);
				if (selected != null) {
					byName.removeAllViews();
					refreshReportsWithQuota(selected);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

				quotaName.setSelection(-1);
			}
		});

		quotaName.setVisibility(View.VISIBLE);
	}

	private void initTypeAdapterForNameReports() {

		ArrayAdapter<QuotaType> staticAdapter = new ArrayAdapter<>(this, android.R.layout
			.simple_spinner_dropdown_item);
		staticAdapter.add(QuotaType.DAILY);
		staticAdapter.add(QuotaType.WEEKLY);
		staticAdapter.add(QuotaType.MONTHLY);
		typeName.setAdapter(staticAdapter);

		if(passedType != null) typeName.setSelection(passedType.ordinal());

		typeName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				QuotaType selected = (QuotaType) parent.getItemAtPosition(position);
				if (selected != null) {
					byType.removeAllViews();
					refreshReportsForType(selected);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

				typeName.setSelection(-1);
			}
		});

		typeName.setVisibility(View.VISIBLE);
	}

	private void refreshReportsForType(QuotaType selected) {

		if (selected == null) { return; }
		byType.setData(getChartData(service.getLoggedDataByType(selected, from.getTime(), to.getTime())));
		byName.refreshDrawableState();

	}

	private void refreshReportsWithQuota(Quota selected) {

		if (selected == null) { return; }

		List<BarEntry> entries = new ArrayList<>();
		int amount = 0;

		for (Worklog worklog : service.getLoggedDataByQuota(selected, from.getTime(), to.getTime())) {
			// turn your data into Entry objects
			entries.add(new BarEntry(amount++, worklog.getAmount().floatValue()));
		}

		BarDataSet dataSet = new BarDataSet(entries, "");
		byName.setData(new BarData(dataSet));

		byName.refreshDrawableState();
	}

	private void hideCharts(Chart... charts) {

		for (Chart chart : charts) {
			chart.setVisibility(View.GONE);
		}
	}

	private void showDataByType() {

		if (typeName.getVisibility() == View.VISIBLE
			&& typeName.getSelectedItem() != null) {
			refreshReportsForType((QuotaType) typeName.getSelectedItem());
		}
		else {

			initTypeAdapterForNameReports();

			byType.setVisibility(View.VISIBLE);

			byType.setDrawBarShadow(false);
			byType.setDrawValueAboveBar(true);
			byType.getDescription().setEnabled(false);
			// scaling can now only be done on x- and y-axis separately
			byType.setPinchZoom(false);
			byType.setDrawGridBackground(false);

			byType.getXAxis().setEnabled(false);

			YAxis yl = byType.getAxisLeft();
			yl.setDrawAxisLine(true);
			yl.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
			yl.setDrawGridLines(true);
			yl.setAxisMinimum(0f);

			YAxis yr = byType.getAxisRight();
			yr.setDrawAxisLine(true);
			yr.setDrawGridLines(false);
			yr.setAxisMinimum(0f);

			byType.setData(getChartData(service.getAllLoggedDataByType(from.getTime(), to.getTime())));

			byType.setFitBars(true);
			byType.animateY(1500);

			Legend l = byType.getLegend();
			l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
			l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
			l.setOrientation(Legend.LegendOrientation.VERTICAL);
			l.setDrawInside(false);
			l.setFormSize(12f);
			l.setXEntrySpace(4f);
		}
	}

	private void showDataByCategory() {

		byCategory.setVisibility(View.VISIBLE);

		byCategory.setUsePercentValues(true);
		byCategory.getDescription().setEnabled(false);
		byCategory.setExtraOffsets(5, 10, 5, 5);
		byCategory.setDragDecelerationFrictionCoef(0.95f);

		byCategory.setDrawHoleEnabled(true);
		byCategory.setHoleColor(Color.WHITE);
		byCategory.setTransparentCircleColor(Color.WHITE);
		byCategory.setTransparentCircleAlpha(50);
		byCategory.setHoleRadius(10f);
		byCategory.setTransparentCircleRadius(5f);
		byCategory.setDrawCenterText(true);
		byCategory.setRotationAngle(0);
		// enable rotation of the chart by touch
		byCategory.setRotationEnabled(true);
		byCategory.setHighlightPerTapEnabled(true);

		byCategory.setData(getCategoryChartData());
		byCategory.highlightValues(null);
		byCategory.invalidate();

		byCategory.animateY(1500, Easing.EasingOption.EaseInOutQuad);

		Legend l = byCategory.getLegend();
		l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
		l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
		l.setOrientation(Legend.LegendOrientation.VERTICAL);
		l.setDrawInside(false);
		l.setXEntrySpace(7f);
		l.setYEntrySpace(0f);
		l.setYOffset(0f);

		byCategory.setEntryLabelColor(Color.BLACK);
		byCategory.setEntryLabelTextSize(12f);
	}

	private PieData getCategoryChartData() {

		ArrayList<PieEntry> entries = new ArrayList<>();
		PieDataSet dataSet = new PieDataSet(entries, "Logged by category");
		PieData data = new PieData(dataSet);

		Map<String, Float> categories = service.getLoggedDataByCategory(from.getTime(), to.getTime());

		for (String category : categories.keySet()) {
			if (categories.get(category) > 0.0) {
				entries.add(new PieEntry(categories.get(category), category));
			}
		}

		dataSet.setDrawIcons(false);

		dataSet.setSliceSpace(3f);
		dataSet.setIconsOffset(new MPPointF(0, 40));
		dataSet.setSelectionShift(5f);

		dataSet.setColors(getRandomColors());

		data.setValueFormatter(new PercentFormatter());
		data.setValueTextSize(11f);
		data.setValueTextColor(Color.BLACK);

		return data;
	}

	private BarData getChartData(Map<String, Float> values) {

		ArrayList<BarEntry> entries;
		BarData data = new BarData();
		int i = 0;
		float spaceForBar = 10f;

		for (String type : values.keySet()) {

			if (values.get(type) > 0.0) {
				entries = new ArrayList<>();
				entries.add(new BarEntry(i * spaceForBar, values.get(type)));
				BarDataSet d = new BarDataSet(entries, type);
				d.setDrawIcons(false);
				d.setColor(getRandomColors().get(i));
				data.addDataSet(d);
				i++;
			}
		}

		data.setValueTextSize(spaceForBar);
		data.setBarWidth(9f);

		return data;
	}
}
