package com.leafchild.scopequotas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.leafchild.scopequotas.data.DatabaseService;
import com.leafchild.scopequotas.data.Quota;
import com.leafchild.scopequotas.data.QuotaType;
import com.leafchild.scopequotas.details.DetailsActivity;
import com.leafchild.scopequotas.reports.ReportsActivity;
import com.leafchild.scopequotas.settings.SettingsActivity;

import java.util.List;

import static com.leafchild.scopequotas.AppContants.TYPE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final MainActivity self = this;
    private TextView type;
    private DatabaseService service;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        service = new DatabaseService(this);

        type = (TextView) findViewById(R.id.quotas_test);

        loadData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent details = new Intent(self, DetailsActivity.class);

                details.putExtra(TYPE, getIntent().getIntExtra(TYPE, 1));
                //Do not add any data
                startActivity(details);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadData() {
        int type = getIntent().getIntExtra(TYPE, 1);

        List<Quota> quotas = service.findQuotasByType(QuotaType.fromOrdinal(type));

        //TODO: Add list view, display elements there

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
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
            case R.id.reports:
                //TODO: Implement reports
                intent = new Intent(self, ReportsActivity.class);
                break;
            case R.id.main_settings:
                intent = new Intent(self, SettingsActivity.class);
                break;
            case R.id.about:
                intent = new Intent(self, AboutActivity.class);
                break;
            case R.id.nav_daily:
                //TODO: Re-do this and simply load data on such actions
                intent = new Intent(self, MainActivity.class);
                intent.putExtra(TYPE, QuotaType.DAILY.ordinal());
                break;
            case R.id.nav_weekly:
                getIntent().putExtra(TYPE, QuotaType.WEEKLY.ordinal());
                break;
            case R.id.nav_monthly:
                getIntent().putExtra(TYPE, QuotaType.MONTHLY.ordinal());
                break;
            default:

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if(intent != null) startActivity(intent);
        else recreate();

        return true;
    }

}
