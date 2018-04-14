package com.leafchild.scopequotas;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.leafchild.scopequotas.categories.CategoryActivity;
import com.leafchild.scopequotas.common.ExportUtils;
import com.leafchild.scopequotas.common.QuotaAdapter;
import com.leafchild.scopequotas.common.Utils;
import com.leafchild.scopequotas.data.DatabaseService;
import com.leafchild.scopequotas.data.Quota;
import com.leafchild.scopequotas.data.QuotaType;
import com.leafchild.scopequotas.details.DetailsActivity;
import com.leafchild.scopequotas.reports.ReportsActivity;
import com.leafchild.scopequotas.settings.SettingsActivity;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
	implements NavigationView.OnNavigationItemSelectedListener
{

	private final MainActivity self = this;
	private DatabaseService service;
	private TextView type;

	private ArrayAdapter<Quota> quotaAdapter;
	private QuotaType currentType = QuotaType.WEEKLY;
	private boolean showArchieved = false;
	private ActionBarDrawerToggle toggle;

	private final String[] exportHeaders = {"Id", "Name", "Description", "Min", "Max", "Last modified", "Category",
		"Logged", "Archived"};
	public static final int REQUEST_WRITE_STORAGE = 112;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nav);
		service = new DatabaseService(this);
		SharedPreferences sharedPref = Utils.getDefaultSharedPrefs(this);

		initComponents();

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		initFabMenu();

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
			if (!quotaAdapter.isEmpty()) { type.setVisibility(View.GONE); }
			else { type.setVisibility(View.VISIBLE); }
		});
		toggle = new ActionBarDrawerToggle(
			this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		boolean quotaBadges = sharedPref.getBoolean(SettingsActivity.QUOTA_BADGES, true);
		if (quotaBadges) initMenuBadges(navigationView);
	}

	@Override
	protected void onPostResume() {

		super.onPostResume();
		updateAppTitle();
		reloadData(currentType);
		toggle.syncState();
	}

	private void initMenuBadges(NavigationView navigationView) {

		TextView daily = (TextView) navigationView.getMenu().findItem(R.id.nav_daily).getActionView();
		daily.setGravity(Gravity.CENTER_VERTICAL);
		daily.setTypeface(null, Typeface.BOLD);
		daily.setText(Integer.toString(service.findQuotasByType(QuotaType.DAILY, showArchieved).size()));

		TextView weekly = (TextView) navigationView.getMenu().findItem(R.id.nav_weekly).getActionView();
		weekly.setGravity(Gravity.CENTER_VERTICAL);
		weekly.setTypeface(null, Typeface.BOLD);
		weekly.setText(Integer.toString(service.findQuotasByType(QuotaType.WEEKLY, showArchieved).size()));

		TextView monthly = (TextView) navigationView.getMenu().findItem(R.id.nav_monthly).getActionView();
		monthly.setGravity(Gravity.CENTER_VERTICAL);
		monthly.setTypeface(null, Typeface.BOLD);
		monthly.setText(Integer.toString(service.findQuotasByType(QuotaType.MONTHLY, showArchieved).size()));
	}

	private void initFabMenu() {

		FloatingActionButton newQuota = findViewById(R.id.new_quota);

		newQuota.setOnClickListener(v -> {
			Intent details = new Intent(self, DetailsActivity.class);
			details.putExtra(AppContants.TYPE, getIntent().getIntExtra(AppContants.TYPE, currentType.ordinal()));
			//Do not add any data
			startActivity(details);
		});
	}

	private void initComponents() {

		type = findViewById(R.id.no_quotas);
		ListView listView = findViewById(R.id.quotas_list);

		List<Quota> quotas = service.findQuotasByType(currentType, showArchieved);
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

		if (!quotaAdapter.isEmpty()) {
			type.setVisibility(View.GONE);
		}
	}

	private void reloadData(QuotaType type) {

		quotaAdapter.clear();
		quotaAdapter.addAll(service.findQuotasByType(type, showArchieved));
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
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_settings:
				Intent intent = new Intent(self, SettingsActivity.class);
				startActivity(intent);
				return true;
			case R.id.show_report:
				Intent showReport = new Intent(self, ReportsActivity.class);
				showReport.putExtra(AppContants.TYPE, currentType.getValue());
				startActivity(showReport);
				return true;
			case R.id.export_data:
				requestPermission(this);
				return true;
			case R.id.show_archieved:
				item.setChecked(!item.isChecked());
				showArchieved = item.isChecked();
				reloadData(currentType);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
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

		switch (id) {
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
				currentType = QuotaType.DAILY;
				reloadData(currentType);
				updateAppTitle();
				break;
			case R.id.nav_weekly:
				currentType = QuotaType.WEEKLY;
				reloadData(currentType);
				updateAppTitle();
				break;
			case R.id.nav_monthly:
				currentType = QuotaType.MONTHLY;
				reloadData(currentType);
				updateAppTitle();
				break;
			default:

		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);

		if (intent != null) { startActivity(intent); }
		else { quotaAdapter.notifyDataSetChanged(); }

		toggle.syncState();

		return true;
	}

	private void updateAppTitle() {

		setTitle("Your quotas - " + currentType.getValue());
	}

	private void requestPermission(Activity context) {

		boolean hasPermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
			== PackageManager.PERMISSION_GRANTED);
		if (!hasPermission) {
			ActivityCompat.requestPermissions(context,
				new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				REQUEST_WRITE_STORAGE);
		}
		else {
			exportQuotas();
		}
	}

	private void exportQuotas() {

		// You are allowed to write external storage:
		boolean exported = false;
		try {
			exported = ExportUtils.exportData(exportHeaders, service.findQuotasByType(currentType, true));
		}
		catch (IOException e) {
			Log.e("Main Activity", "Exporting Data");
		}
		String message = exported ? "Data has been seccessfully exported" : "Something wrong happended during "
			+ "exporting";

		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
		@NonNull int[] grantResults) {

		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {
			case REQUEST_WRITE_STORAGE: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "The app was allowed to write to your storage!", Toast.LENGTH_LONG).show();
					// Reload the activity with permission granted or use the features what required the permission
					exportQuotas();
				}
				else {
					Toast.makeText(this,
						"The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission",
						Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
