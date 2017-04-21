package com.leafchild.scopequotas.details;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.common.Utils;
import com.leafchild.scopequotas.data.DatabaseService;
import com.leafchild.scopequotas.data.Quota;
import com.leafchild.scopequotas.data.QuotaCategory;
import com.leafchild.scopequotas.data.QuotaType;
import com.leafchild.scopequotas.data.Worklog;

import java.util.ArrayList;
import java.util.List;

import static com.leafchild.scopequotas.AppContants.ACTIVE_QUOTA;
import static com.leafchild.scopequotas.AppContants.TYPE;

public class DetailsActivity extends AppCompatActivity {

    private EditText name;
    private EditText goal;
    private EditText min;
    private EditText max;
    private Spinner categories;
    private TextView worklogAmount;

    private DatabaseService service;
    private Quota editingBean;
    private String pickedCategory;
    private static final String HOURS = " hours";
    private ArrayAdapter<String> catAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        worklogAmount = (TextView) findViewById(R.id.quota_amount_value);
        LinearLayout worklogLayout = (LinearLayout) findViewById(R.id.quota_amount_name);
        name = (EditText) findViewById(R.id.quota_name);
        goal = (EditText) findViewById(R.id.quota_goal);
        min = (EditText) findViewById(R.id.quota_min);
        max = (EditText) findViewById(R.id.quota_max);

        final int type = getIntent().getIntExtra(TYPE, 1);

        service = new DatabaseService(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<String> existing = new ArrayList<>();
        List<QuotaCategory> categoryData = service.findAllCategories();
        for(QuotaCategory category : categoryData) {
            existing.add(category.getName());
        }

        categories = (Spinner) findViewById(R.id.category_list);
        catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, existing);
        categories.setAdapter(catAdapter);
        categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pickedCategory = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        categories.setPrompt("Select query");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if(editingBean == null) {
                editingBean = new Quota(
                    name.getText().toString(),
                    goal.getText().toString(),
                    QuotaType.fromOrdinal(type)
                );
                editingBean.setCategory(service.getCategoryByName(pickedCategory));
                editingBean.setMin(Integer.valueOf(min.getText().toString()));
                editingBean.setMax(Integer.valueOf(max.getText().toString()));
            } else {
                editingBean.setCategory(service.getCategoryByName(pickedCategory));
                editingBean.setMin(Integer.valueOf(min.getText().toString()));
                editingBean.setMax(Integer.valueOf(max.getText().toString()));
                editingBean.setDescription(goal.getText().toString());
            }
            service.persistQuota(editingBean);

            Toast.makeText(DetailsActivity.this, "Quota " + editingBean.getName() + " was saved", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(DetailsActivity.this::onBackPressed, 1000);
        });

        if(getQuotaId() != -1) {
            //Existing entity
            loadData();
            setTitle(editingBean.getName());
            initChart();
        }
        else {
            //New entity
            worklogLayout.setVisibility(View.GONE);
            setTitle("Add new Quota");
        }
    }

    private void loadData() {

        long existinQuotaId = getQuotaId();
        //Get item from DB
        editingBean = service.getQuota(existinQuotaId);

        //Perform any validations, etc
        //Preset values in the fields
        if(editingBean != null) {
            name.setText(editingBean.getName());
            //Prevent from editing quota name once it was saved
            name.setEnabled(false);

            min.setText(String.valueOf(editingBean.getMin()));
            max.setText(String.valueOf(editingBean.getMax()));

            categories.setSelection(catAdapter.getPosition(editingBean.getCategory().getName()));

            goal.setText(editingBean.getDescription());
            worklogAmount.setText(String.format(editingBean.getWorklogAmount().toString() + "%s", HOURS));
        }
    }

    private long getQuotaId() {
        return getIntent().getLongExtra(ACTIVE_QUOTA, -1);
    }

    private void initChart() {

        BarChart chart = (BarChart) findViewById(R.id.worklog_chart);

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int amount = 0;

        for(Worklog worklog : editingBean.getLogged()) {

            // turn your data into Entry objects
            entries.add(new BarEntry(amount++, worklog.getAmount().floatValue()));
            labels.add(Utils.getDayMonthFormatter().format(worklog.getCreatedDate()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        BarData lineData = new BarData(dataSet);
        chart.setData(lineData);

        XAxis xval = chart.getXAxis();
        xval.setDrawLabels(true);
        xval.setValueFormatter((value, axis) -> labels.get((int) value));
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
