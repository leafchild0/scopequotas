package com.leafchild.scopequotas.details;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.data.DatabaseService;
import com.leafchild.scopequotas.data.Quota;
import com.leafchild.scopequotas.data.QuotaType;

import java.util.Date;

import static com.leafchild.scopequotas.AppContants.ACTIVE_QUOTA;
import static com.leafchild.scopequotas.AppContants.TYPE;

public class DetailsActivity extends AppCompatActivity {

    private EditText name;
    private EditText goal;
    private TextView worklogAmount;
    private DatabaseService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        worklogAmount = (TextView) findViewById(R.id.quota_amount);
        name = (EditText) findViewById(R.id.quota_name);
        goal = (EditText) findViewById(R.id.quota_goal);

        final int type = getIntent().getIntExtra(TYPE, 1);

        service = new DatabaseService(this);

        if(getQuotaId() != -1) {
            //Existing entity
            //TODO: Load data
            loadData();
        } else {
            //New entity
            worklogAmount.setVisibility(View.GONE);
            setTitle("Add new Quota");

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Snackbar.make(view, "New quota has been added", Snackbar.LENGTH_LONG)
                    .setAction("Saved", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Quota newQuota = new Quota();
                            newQuota.setQuotaType(QuotaType.fromOrdinal(type));
                            newQuota.setName(name.getText().toString());
                            newQuota.setDescription(goal.getText().toString());
                            //TODO:  add datepickers and get user input
                            newQuota.setStartDate(new Date());
                            newQuota.setEndDate(new Date());

                            service.persistQuota(newQuota);
                        }
                    }).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadData() {

        int existinQuotaId = getQuotaId();
        //Get item from DB

        Quota quota = service.getQuota(existinQuotaId);

        //Perform any validations, etc
        //Preset values in the fields
        if(quota != null) {
            name.setText(quota.getName());
            goal.setText(quota.getDescription());
        }
    }

    private int getQuotaId() {
        return getIntent().getIntExtra(ACTIVE_QUOTA, -1);
    }
}
