package com.leafchild.scopequotas.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.leafchild.scopequotas.AppContants
import com.leafchild.scopequotas.R
import com.leafchild.scopequotas.common.Utils
import com.leafchild.scopequotas.data.DatabaseService
import com.leafchild.scopequotas.data.Quota
import com.leafchild.scopequotas.data.QuotaType

import java.util.ArrayList
import java.util.stream.Collectors

class DetailsActivity : AppCompatActivity() {

    private var name: EditText? = null
    private var goal: EditText? = null
    private var min: EditText? = null
    private var max: EditText? = null
    private var categories: Spinner? = null
    private var worklogAmount: TextView? = null
    private var chart: BarChart? = null

    private var service: DatabaseService? = null
    private var editingBean: Quota? = null
    private var pickedCategory: String? = null
    private var type: Int = 0
    private var catAdapter: ArrayAdapter<String>? = null

    private val isQuotaValid: Boolean
        get() {

            var isMinMax = false
            val isAllFilled = ((name!!.visibility == View.VISIBLE) or Utils.isFieldEmpty(name)
                    && !Utils.isFieldEmpty(goal)
                    && !Utils.isFieldEmpty(min)
                    && !Utils.isFieldEmpty(max))

            if (isAllFilled) {
                val minValue = Integer.valueOf(min!!.text.toString())
                val maxValue = Integer.valueOf(max!!.text.toString())

                isMinMax = Integer.compare(minValue, maxValue) < 0

                if (!isMinMax) {
                    Toast.makeText(this@DetailsActivity,
                            "Min needs to be less then Max", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@DetailsActivity, "All fields are required", Toast.LENGTH_SHORT).show()
            }

            return isAllFilled && isMinMax
        }

    private val quotaId: Long
        get() = intent.getLongExtra(AppContants.ACTIVE_QUOTA, -1)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        worklogAmount = findViewById(R.id.quota_amount_value)
        val worklogLayout = findViewById<LinearLayout>(R.id.quota_amount_name)
        val deleteButton = findViewById<Button>(R.id.button_delete)
        name = findViewById(R.id.quota_name)
        goal = findViewById(R.id.quota_goal)
        min = findViewById(R.id.quota_min)
        max = findViewById(R.id.quota_max)
        chart = findViewById(R.id.worklog_chart)

        type = intent.getIntExtra(AppContants.TYPE, 1)

        service = DatabaseService(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val logTime = findViewById<FloatingActionButton>(R.id.add_worklog)
        if (quotaId.toInt() == -1) {
            logTime.visibility = View.GONE
        }

        logTime.setOnClickListener {
            val addWorklog = Intent(this@DetailsActivity, WorklogActivity::class.java)
            addWorklog.putExtra(AppContants.TYPE, intent.getIntExtra(AppContants.TYPE, 1))
            addWorklog.putExtra(AppContants.ACTIVE_QUOTA, quotaId)
            //Do not add any data
            startActivity(addWorklog)
        }

        val categoryData = service!!.findAllCategories()
        val existing = categoryData.stream().map { it.name!! }.collect(Collectors.toList())

        categories = findViewById(R.id.category_list)
        catAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, existing)
        categories!!.adapter = catAdapter
        categories!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                pickedCategory = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do not do anything
            }
        }
        categories!!.prompt = "Select query"

        if (isExistingQuota()) {
            //Existing entity
            loadData()
            title = editingBean!!.name
            initChart()
        } else {
            //New entity
            worklogLayout.visibility = View.GONE
            deleteButton.visibility = View.GONE
            chart!!.visibility = View.GONE
            title = "Add new Quota"
        }
    }

    private fun loadData() {

        val existinQuotaId = quotaId
        //Get item from DB
        editingBean = service!!.getQuota(existinQuotaId)

        //Perform any validations, etc
        //Preset values in the fields
        if (editingBean != null) {
            //Prevent from editing quota name once it was saved
            name!!.visibility = View.GONE

            min!!.setText(editingBean!!.min.toString())
            max!!.setText(editingBean!!.max.toString())

            categories!!.setSelection(catAdapter!!.getPosition(editingBean!!.category!!.name))

            goal!!.setText(editingBean!!.description)
            worklogAmount!!.text = String.format(editingBean!!.workFlowByLastPeriod!!.toString() + "%s", HOURS)
        }
    }

    private fun initChart() {

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        var amount = 0

        if (editingBean!!.isNew || editingBean!!.logged!!.isEmpty()) {
            chart!!.visibility = View.GONE
        } else {

            for (worklog in editingBean!!.logged!!) {
                // turn your data into Entry objects
                entries.add(BarEntry(amount++.toFloat(), worklog.amount!!.toFloat()))
                labels.add(Utils.getDayMonthFormatter().format(worklog.createdDate))
            }

            val dataSet = BarDataSet(entries, "")
            val lineData = BarData(dataSet)
            chart!!.data = lineData
            chart!!.description.isEnabled = false
            chart!!.legend.isEnabled = false

            val xl = chart!!.xAxis
            xl.position = XAxis.XAxisPosition.BOTTOM
            xl.setDrawAxisLine(true)
            xl.setDrawGridLines(false)
            xl.isGranularityEnabled = true
            xl.granularity = 1f
            xl.setDrawLabels(true)
            xl.valueFormatter = IndexAxisValueFormatter(labels)

            val yl = chart!!.axisLeft
            yl.setDrawAxisLine(true)
            yl.setDrawGridLines(true)
            yl.axisMinimum = 0f

            val yr = chart!!.axisRight
            yr.setDrawAxisLine(true)
            yr.setDrawGridLines(false)
            yr.axisMinimum = 0f
        }
    }

    override fun onBackPressed() {

        super.onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    fun saveQuota(view: View) {

        if (isQuotaValid) {
            if (editingBean == null) {
                editingBean = Quota(
                        name!!.text.toString(),
                        goal!!.text.toString(),
                        QuotaType.fromOrdinal(type)
                )
                editingBean!!.category = service!!.getCategoryByName(pickedCategory!!)
                editingBean!!.min = Integer.valueOf(min!!.text.toString())
                editingBean!!.max = Integer.valueOf(max!!.text.toString())
            } else {
                editingBean!!.category = service!!.getCategoryByName(pickedCategory!!)
                editingBean!!.min = Integer.valueOf(min!!.text.toString())
                editingBean!!.max = Integer.valueOf(max!!.text.toString())
                editingBean!!.description = goal!!.text.toString()
            }
            service!!.persistQuota(editingBean!!)

            Toast.makeText(this@DetailsActivity, "Quota " + editingBean!!.name + " was saved", Toast.LENGTH_SHORT)
                    .show()
            Handler().postDelayed({ this@DetailsActivity.onBackPressed() }, 1000)
        }
    }

    fun archiveQuota(view: View) {

        if (isExistingQuota()) {
            AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you really want to archive this quota?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        //Then remove
                        val result = service!!.archiveQuota(quotaId)
                        if (result) {
                            Toast.makeText(this@DetailsActivity, "Quota " + editingBean!!.name + " was successfully "
                                    + "archived",
                                    Toast.LENGTH_SHORT).show()
                            Handler().postDelayed({ this@DetailsActivity.onBackPressed() }, 1000)
                        } else {
                            Toast.makeText(this@DetailsActivity, "Something went wrong. Cannot remove quota",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton(android.R.string.no, null).show()
        }
    }

    private fun isExistingQuota() = quotaId.toInt() != -1

    companion object {
        private const val HOURS = " hours"
    }
}
