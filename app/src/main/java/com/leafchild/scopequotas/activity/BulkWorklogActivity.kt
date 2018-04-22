package com.leafchild.scopequotas.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.leafchild.scopequotas.AppContants
import com.leafchild.scopequotas.R
import com.leafchild.scopequotas.common.Utils
import com.leafchild.scopequotas.common.WorklogAdapter
import com.leafchild.scopequotas.data.DatabaseService
import com.leafchild.scopequotas.data.Quota
import com.leafchild.scopequotas.data.QuotaType
import com.leafchild.scopequotas.data.Worklog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

class BulkWorklogActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var type: QuotaType
    private lateinit var service: DatabaseService
    private lateinit var adapter: WorklogAdapter
    private var calendar: Calendar? = null

    private lateinit var worklogDate: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bulk_worklog)
        service = DatabaseService(this)

        // Get type
        populateType()

        // Init add button
        worklogDate = findViewById(R.id.worklog_date)
        worklogDate.text = "Today"

        // Build quotas list
        val listView = findViewById<ListView>(R.id.quotas_list)
        adapter = WorklogAdapter(this, getQuotasByType())
        listView.adapter = adapter

    }

    private fun getQuotasByType(): List<Quota> {
        return service.findQuotasByType(type, false)
    }

    private fun populateType() {

        val passed = intent.getStringExtra(AppContants.TYPE)
        if (passed != null) {
            type = QuotaType.fromString(passed)!!
        }
    }

    fun showDatePicker(view: View) {

        calendar = Calendar.getInstance()
        val pickerDialog = DatePickerDialog.newInstance(
                this@BulkWorklogActivity,
                calendar!!.get(Calendar.YEAR),
                calendar!!.get(Calendar.MONTH),
                calendar!!.get(Calendar.DAY_OF_MONTH)
        )

        pickerDialog.firstDayOfWeek = Calendar.MONDAY
        pickerDialog.accentColor = AppContants.ACCENT_COLOR
        pickerDialog.show(fragmentManager, "Change Date")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        calendar!!.set(year, monthOfYear, dayOfMonth)
        worklogDate.text = Utils.getDayMonthYearFormatter().format(calendar!!.time)
    }

    fun cancel(view: View?) {

        onBackPressed()
    }

    fun addBulkWorklog(view: View) {

        service.addWorklogs(getWorklogsFromView())

        Toast.makeText(this@BulkWorklogActivity, "All Worklogs were added", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ this@BulkWorklogActivity.onBackPressed() }, 1000)
    }

    private fun getWorklogsFromView(): List<Worklog> {
        return adapter.getFilledWorklogs().map {
            Worklog(it.key, it.value.toDouble(), calendar!!.time)
        }
    }
}
