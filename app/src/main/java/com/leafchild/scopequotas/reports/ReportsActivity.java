package com.leafchild.scopequotas.reports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.common.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public class ReportsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private DatePickerDialog fromD;
    private DatePickerDialog toD;
    private Spinner reportBy;
    private Button fromDate;
    private Button toDate;

    private Calendar calendar;
    private String type;
    private Calendar from = Calendar.getInstance();
    private Calendar to = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar = Calendar.getInstance();
        reportBy = (Spinner) findViewById(R.id.report_by);
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
        toDate.setText(Utils.getDayMonthYearFormatter().format(calendar.getTime()));

        calendar.add(Calendar.MONTH, -1);
        fromDate = (Button) findViewById(R.id.reports_from);
        fromDate.setText(Utils.getDayMonthYearFormatter().format(calendar.getTime()));

        refreshReports();
    }

    public void showFromDatePicker(View view) {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        fromD = DatePickerDialog.newInstance(
            ReportsActivity.this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        fromD.show(getFragmentManager(), "Choose From Date");
    }

    public void showToDatePicker(View view) {
        calendar = Calendar.getInstance();
        toD = DatePickerDialog.newInstance(
            ReportsActivity.this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        //TODO: Update color of datepicker to same as whole app uses
        toD.show(getFragmentManager(), "Choose To Date");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int dayOfMonth) {
        if(view.getTag().equals(fromD.getTag())) {
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
        //TODO: Refresh reports with updated data
    }
}
