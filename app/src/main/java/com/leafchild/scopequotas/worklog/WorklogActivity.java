package com.leafchild.scopequotas.worklog;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.common.QuotaAdapter;
import com.leafchild.scopequotas.data.DatabaseService;
import com.leafchild.scopequotas.data.Quota;
import com.leafchild.scopequotas.data.Worklog;
import com.leafchild.scopequotas.data.WorklogType;

public class WorklogActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseService service;
    private Quota picked = null;
    private  Spinner inputType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worklog);

        service = new DatabaseService(this);

        Spinner dropdown = (Spinner) findViewById(R.id.quotas_list);
        dropdown.setAdapter(new QuotaAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            service.findAllQuotas()));
        dropdown.setOnItemSelectedListener(this);
        dropdown.setPrompt("Select query");

        inputType = (Spinner) findViewById(R.id.worklog_amount_type);
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
            .createFromResource(this, R.array.worklog_array, android.R.layout.simple_spinner_item);
        inputType.setAdapter(staticAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        picked = (Quota) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addWorklog(View view) {

        EditText amount = (EditText) findViewById(R.id.quota_amount);

        Worklog worklog = new Worklog(picked, amount.getText().toString());
        worklog.setType(WorklogType.fromString(inputType.getSelectedItem().toString()));
        service.addWorklog(worklog);

        Toast.makeText(WorklogActivity.this, "Worklog was added", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(WorklogActivity.this::onBackPressed, 1000);
    }

    public void cancel(View view) {
        onBackPressed();
    }

}
