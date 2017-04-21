package com.leafchild.scopequotas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.leafchild.scopequotas.categories.CategoryActivity;
import com.leafchild.scopequotas.common.QuotaAdapter;
import com.leafchild.scopequotas.data.DatabaseService;
import com.leafchild.scopequotas.data.Quota;
import com.leafchild.scopequotas.data.QuotaType;
import com.leafchild.scopequotas.details.DetailsActivity;
import com.leafchild.scopequotas.reports.ReportsActivity;
import com.leafchild.scopequotas.settings.SettingsActivity;
import com.leafchild.scopequotas.worklog.WorklogActivity;

import java.util.List;

import static com.leafchild.scopequotas.AppContants.TYPE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final MainActivity self = this;
    private DatabaseService service;
    private ListView listView;

    private ArrayAdapter<Quota> quotaAdapter;
    private QuotaType currentType = QuotaType.WEEKLY;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        service = new DatabaseService(this);

        initComponents();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFabMenu();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initFabMenu() {

        FloatingActionButton newQuota = (FloatingActionButton) findViewById(R.id.new_quota);
        FloatingActionButton logTime = (FloatingActionButton) findViewById(R.id.add_worglog);

        final FloatingActionMenu menuMultipleActions = (FloatingActionMenu) findViewById(R.id.multiple_actions);
        menuMultipleActions.setOnClickListener(view -> {

            menuMultipleActions.setEnabled(!menuMultipleActions.isEnabled());

            Intent details = new Intent(self, DetailsActivity.class);
            details.putExtra(TYPE, getIntent().getIntExtra(TYPE, 1));
            startActivity(details);
        });


        logTime.setOnClickListener((v) -> {
            Intent addWorklog = new Intent(self, WorklogActivity.class);
            addWorklog.putExtra(TYPE, getIntent().getIntExtra(TYPE, 1));
            //Do not add any data
            startActivity(addWorklog);
            menuMultipleActions.close(true);
        });

        newQuota.setOnClickListener((v) -> {
            Intent details = new Intent(self, DetailsActivity.class);
            details.putExtra(TYPE, getIntent().getIntExtra(TYPE, currentType.ordinal()));
            //Do not add any data
            startActivity(details);
            menuMultipleActions.close(true);
        });
    }

    private void initComponents() {

        TextView type = (TextView) findViewById(R.id.no_quotas);
        listView = (ListView) findViewById(R.id.quotas_list);

        List<Quota> quotas = loadData(getIntent().getIntExtra(TYPE, 1));
        quotaAdapter = new QuotaAdapter(this, quotas);

        // Assign adapter to ListView
        listView.setAdapter(quotaAdapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener((parent, view, position, id) -> {

            Quota itemValue = (Quota) listView.getItemAtPosition(position);

            Intent details = new Intent(self, DetailsActivity.class);
            details.putExtra(AppContants.ACTIVE_QUOTA, itemValue.getId());

            startActivity(details);
        });

        if(!quotas.isEmpty()) {
            type.setVisibility(View.GONE);
        }
    }

    private List<Quota> loadData(int type) {
        return service.findQuotasByType(QuotaType.fromOrdinal(type));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            quotaAdapter.notifyDataSetInvalidated();
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;

        switch(id) {
            case R.id.categories:
                intent = new Intent(self, CategoryActivity.class);
                break;
            case R.id.reports:
                intent = new Intent(self, ReportsActivity.class);
                break;
            case R.id.main_settings:
                intent = new Intent(self, SettingsActivity.class);
                break;
            case R.id.about:
                intent = new Intent(self, AboutActivity.class);
                break;
            case R.id.nav_daily:
                quotaAdapter.clear();
                quotaAdapter.addAll(loadData(QuotaType.DAILY.ordinal()));
                currentType = QuotaType.DAILY;
                break;
            case R.id.nav_weekly:
                quotaAdapter.clear();
                quotaAdapter.addAll(loadData(QuotaType.WEEKLY.ordinal()));
                currentType = QuotaType.WEEKLY;
                break;
            case R.id.nav_monthly:
                quotaAdapter.clear();
                quotaAdapter.addAll(loadData(QuotaType.MONTHLY.ordinal()));
                currentType = QuotaType.MONTHLY;
                break;
            default:

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if(intent != null) {
            startActivity(intent);
        }
        else {
            //recreate();
        }

        return true;
    }

}
