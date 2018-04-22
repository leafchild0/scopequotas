package com.leafchild.scopequotas.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.leafchild.scopequotas.AppContants
import com.leafchild.scopequotas.R
import com.leafchild.scopequotas.common.ExportUtils
import com.leafchild.scopequotas.common.QuotaAdapter
import com.leafchild.scopequotas.common.Utils
import com.leafchild.scopequotas.data.DatabaseService
import com.leafchild.scopequotas.data.Quota
import com.leafchild.scopequotas.data.QuotaType
import com.leafchild.scopequotas.settings.SettingsActivity
import java.io.IOException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val self = this
    private var service: DatabaseService? = null
    private var type: TextView? = null

    private var quotaAdapter: ArrayAdapter<Quota>? = null
    private var currentType = QuotaType.WEEKLY
    private var showArchieved = false
    private var toggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)
        service = DatabaseService(this)
        val sharedPref = Utils.getDefaultSharedPrefs(this)

        initComponents()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        initFabMenu()

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (!quotaAdapter!!.isEmpty) type!!.visibility = View.GONE
            else type!!.visibility = View.VISIBLE
        }
        toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle!!)
        toggle!!.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val quotaBadges = sharedPref.getBoolean(SettingsActivity.QUOTA_BADGES, true)
        if (quotaBadges) initMenuBadges(navigationView)
    }

    override fun onPostResume() {

        super.onPostResume()
        updateAppTitle()
        reloadData(currentType)
        toggle!!.syncState()
    }

    private fun initMenuBadges(navigationView: NavigationView) {

        val daily = navigationView.menu.findItem(R.id.nav_daily).actionView as TextView
        daily.gravity = Gravity.CENTER_VERTICAL
        daily.setTypeface(null, Typeface.BOLD)
        daily.text = Integer.toString(service!!.findQuotasByType(QuotaType.DAILY, showArchieved).size)

        val weekly = navigationView.menu.findItem(R.id.nav_weekly).actionView as TextView
        weekly.gravity = Gravity.CENTER_VERTICAL
        weekly.setTypeface(null, Typeface.BOLD)
        weekly.text = Integer.toString(service!!.findQuotasByType(QuotaType.WEEKLY, showArchieved).size)

        val monthly = navigationView.menu.findItem(R.id.nav_monthly).actionView as TextView
        monthly.gravity = Gravity.CENTER_VERTICAL
        monthly.setTypeface(null, Typeface.BOLD)
        monthly.text = Integer.toString(service!!.findQuotasByType(QuotaType.MONTHLY, showArchieved).size)
    }

    private fun initFabMenu() {

        val newQuota = findViewById<FloatingActionButton>(R.id.new_quota)

        newQuota.setOnClickListener {
            val details = Intent(self, DetailsActivity::class.java)
            details.putExtra(AppContants.TYPE, intent.getIntExtra(AppContants.TYPE, currentType.ordinal))
            //Do not add any data
            startActivity(details)
        }
    }

    private fun initComponents() {

        type = findViewById(R.id.no_quotas)
        val listView = findViewById<ListView>(R.id.quotas_list)

        val quotas = service!!.findQuotasByType(currentType, showArchieved)
        quotaAdapter = QuotaAdapter(this, quotas)

        // Assign adapter to ListView
        listView.adapter = quotaAdapter

        // ListView Item Click Listener
        listView.setOnItemClickListener { _, _, position, _ ->
            val itemValue = listView.getItemAtPosition(position) as Quota

            val details = Intent(self, DetailsActivity::class.java)
            details.putExtra(AppContants.ACTIVE_QUOTA, itemValue.id)

            startActivity(details)
        }

        if (!quotaAdapter!!.isEmpty) {
            type!!.visibility = View.GONE
        }
    }

    private fun reloadData(type: QuotaType) {

        quotaAdapter!!.clear()
        quotaAdapter!!.addAll(service!!.findQuotasByType(type, showArchieved))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            R.id.show_report -> {
                val showReport = Intent(self, ReportsActivity::class.java)
                showReport.putExtra(AppContants.TYPE, currentType.value)
                startActivity(showReport)
                return true
            }
            R.id.bulk_worklog -> {
                val intent = Intent(self, BulkWorklogActivity::class.java)
                intent.putExtra(AppContants.TYPE, currentType.value)
                startActivity(intent)
                return true
            }
            R.id.export_data -> {
                requestPermission(this)
                return true
            }
            R.id.show_archieved -> {
                item.isChecked = !item.isChecked
                showArchieved = item.isChecked
                reloadData(currentType)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            quotaAdapter!!.notifyDataSetInvalidated()
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Handle navigation view item clicks here.
        val id = item.itemId
        var intent: Intent? = null

        when (id) {
            R.id.categories -> intent = Intent(self, CategoryActivity::class.java)
            R.id.reports -> intent = Intent(self, ReportsActivity::class.java)
            R.id.main_settings -> intent = Intent(self, SettingsActivity::class.java)
            R.id.about -> intent = Intent(self, AboutActivity::class.java)
            R.id.nav_daily -> {
                currentType = QuotaType.DAILY
                reloadData(currentType)
                updateAppTitle()
            }
            R.id.nav_weekly -> {
                currentType = QuotaType.WEEKLY
                reloadData(currentType)
                updateAppTitle()
            }
            R.id.nav_monthly -> {
                currentType = QuotaType.MONTHLY
                reloadData(currentType)
                updateAppTitle()
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)

        if (intent != null) startActivity(intent)
        else quotaAdapter!!.notifyDataSetChanged()
        toggle!!.syncState()

        return true
    }

    private fun updateAppTitle() {

        title = "Your quotas - " + currentType.value
    }

    private fun requestPermission(context: Activity) {

        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_STORAGE)
        } else {
            exportQuotas()
        }
    }

    private fun exportQuotas() {

        // You are allowed to write external storage:
        var message = "Data has been seccessfully exported"
        try {
            ExportUtils.exportData(Utils.getExportHeaders(), service!!.findQuotasByType(currentType, true))
        } catch (e: IOException) {
            Log.e("Main Activity", "Exporting Data")
            message = "Something wrong happended during exporting"
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_WRITE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "The app was allowed to write to your storage!", Toast.LENGTH_LONG).show()
                    // Reload the activity with permission granted or use the features what required the permission
                    exportQuotas()
                } else {
                    Toast.makeText(this,
                            "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission",
                            Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        const val REQUEST_WRITE_STORAGE = 112
    }
}
