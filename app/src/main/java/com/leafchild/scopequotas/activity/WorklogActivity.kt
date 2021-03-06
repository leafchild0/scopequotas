package com.leafchild.scopequotas.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.leafchild.scopequotas.AppContants
import com.leafchild.scopequotas.R
import com.leafchild.scopequotas.common.Utils
import com.leafchild.scopequotas.data.DatabaseService
import com.leafchild.scopequotas.data.Quota
import com.leafchild.scopequotas.data.Worklog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

class WorklogActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var worklogDate: Button

    private lateinit var service: DatabaseService
    private lateinit var calendar: Calendar
    private var picked: Quota? = null

    private val addedAmount: Double?
        get() {

            val amount = findViewById<EditText>(R.id.quota_amount)
            return amount.text.toString().toDouble()
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worklog)

        service = DatabaseService(this)

        worklogDate = findViewById(R.id.worklog_date)
        worklogDate.text = "Today"
    }

    fun addWorklog(view: View) {

        initQuota()
        service.addWorklog(Worklog(picked!!, addedAmount, getWorklogDate()))

        Toast.makeText(this@WorklogActivity, "Worklog was added", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ this@WorklogActivity.onBackPressed() }, 1000)
    }

    private fun getWorklogDate(): Date {

        if (worklogDate.text != "Today") {
            return calendar.time
        }
        return Date()
    }

    fun cancel(view: View?) {

        onBackPressed()
    }

    fun showWorklogDatePicker(view: View) {

        calendar = Calendar.getInstance()
        val pickerDialog = DatePickerDialog.newInstance(
                this@WorklogActivity,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        )

        pickerDialog.firstDayOfWeek = Calendar.MONDAY
        pickerDialog.accentColor = AppContants.ACCENT_COLOR
        pickerDialog.show(fragmentManager, "Change Date")
    }

    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        calendar.set(year, monthOfYear, dayOfMonth)
        worklogDate.text = Utils.getDayMonthYearFormatter().format(calendar.time)
    }

    private fun initQuota() {

        val activeQuota = intent.getLongExtra(AppContants.ACTIVE_QUOTA, -1)
        picked = service.getQuota(activeQuota)
    }
}
