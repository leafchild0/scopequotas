package com.leafchild.scopequotas.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.leafchild.scopequotas.AppContants
import com.leafchild.scopequotas.R
import com.leafchild.scopequotas.common.QuotaAdapter
import com.leafchild.scopequotas.common.QuotasWithDefaultAdapter
import com.leafchild.scopequotas.common.Utils
import com.leafchild.scopequotas.data.DatabaseService
import com.leafchild.scopequotas.data.Quota
import com.leafchild.scopequotas.data.QuotaType
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog

import java.util.ArrayList
import java.util.Calendar

class ReportsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var fromD: DatePickerDialog? = null
    private var quotaName: Spinner? = null
    private var typeName: Spinner? = null
    private var fromDate: Button? = null
    private var toDate: Button? = null
    private var reportBy: Spinner? = null

    private var byCategory: PieChart? = null
    private var byName: BarChart? = null
    private var byType: HorizontalBarChart? = null

    private var type = BY_CATEGORY
    private val from = Calendar.getInstance()
    private val to = Calendar.getInstance()
    private var passedType: QuotaType? = null

    private var service: DatabaseService? = null

    private val categoryChartData: PieData
        get() {

            val entries = ArrayList<PieEntry>()
            val dataSet = PieDataSet(entries, "Logged by category")
            val data = PieData(dataSet)

            val categories = service!!.getLoggedDataByCategory(from.time, to.time)

            categories.keys.forEach( {
                if (categories[it]!! > 0.0) {
                    entries.add(PieEntry(categories[it]!!, it))
                }
            })

            dataSet.setDrawIcons(false)

            dataSet.sliceSpace = 3f
            dataSet.iconsOffset = MPPointF(0f, 40f)
            dataSet.selectionShift = 5f

            dataSet.colors = Utils.randomColors

            data.setValueFormatter(PercentFormatter())
            data.setValueTextSize(11f)
            data.setValueTextColor(Color.BLACK)

            return data
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        service = DatabaseService(this)

        reportBy = findViewById(R.id.report_by)
        val staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.reports_category, android.R.layout.simple_spinner_item)
        reportBy!!.adapter = staticAdapter
        reportBy!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                type = parent.getItemAtPosition(position) as String
                refreshReports()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        toDate = findViewById(R.id.reports_to)
        toDate!!.text = Utils.getDayMonthYearFormatter().format(to.time)

        from.add(Calendar.WEEK_OF_MONTH, -1)
        fromDate = findViewById(R.id.reports_from)
        fromDate!!.text = Utils.getDayMonthYearFormatter().format(from.time)

        byCategory = findViewById(R.id.category_chart)
        byName = findViewById(R.id.name_chart)
        byType = findViewById(R.id.type_chart)
        quotaName = findViewById(R.id.report_by_name)
        typeName = findViewById(R.id.report_by_type)

        getPassedType()
        refreshReports()
    }

    override fun onNewIntent(intent: Intent) {

        super.onNewIntent(intent)
        getPassedType()
        refreshReports()
    }

    private fun getPassedType() {

        val passed = intent.getStringExtra(AppContants.TYPE)
        if (passed != null) {
            reportBy!!.setSelection(2)
            type = BY_TYPE
            passedType = QuotaType.fromString(passed)
        }
    }

    fun showFromDatePicker(view: View) {

        fromD = DatePickerDialog.newInstance(
                this@ReportsActivity,
                from.get(Calendar.YEAR),
                from.get(Calendar.MONTH),
                from.get(Calendar.DAY_OF_MONTH)
        )

        fromD!!.firstDayOfWeek = Calendar.MONDAY
        fromD!!.accentColor = AppContants.ACCENT_COLOR
        fromD!!.show(fragmentManager, "Choose From Date")
    }

    fun showToDatePicker(view: View) {

        val toD = DatePickerDialog.newInstance(
                this@ReportsActivity,
                to.get(Calendar.YEAR),
                to.get(Calendar.MONTH),
                to.get(Calendar.DAY_OF_MONTH)
        )

        toD.firstDayOfWeek = Calendar.MONDAY
        toD.accentColor = AppContants.ACCENT_COLOR
        toD.show(fragmentManager, "Choose To Date")
    }

    override fun onDateSet(view: DatePickerDialog, year: Int, month: Int, dayOfMonth: Int) {

        if (view.tag == fromD!!.tag) {
            //From date
            from.set(year, month, dayOfMonth)
            fromDate!!.text = Utils.getDayMonthYearFormatter().format(from.time)
        } else {
            //To date
            to.set(year, month, dayOfMonth)
            toDate!!.text = Utils.getDayMonthYearFormatter().format(to.time)
        }

        refreshReports()
    }

    private fun refreshReports() {

        when (type) {
            BY_CATEGORY -> {
                hideCharts(byName, byType)
                hideSelector(quotaName)
                hideSelector(typeName)
                showDataByCategory()
            }
            BY_NAME -> {
                hideCharts(byCategory, byType)
                hideSelector(typeName)
                hideSelector(quotaName)
                showDataByName()
            }
            BY_TYPE -> {
                hideCharts(byCategory, byName)
                hideSelector(typeName)
                hideSelector(quotaName)
                showDataByType()
            }
            else -> showDataByCategory()
        }
    }

    private fun hideSelector(selector: Spinner?) {

        selector?.visibility = View.GONE
    }

    private fun showDataByName() {

        if (quotaName!!.visibility == View.VISIBLE && quotaName!!.selectedItem != null) {
            refreshReportsWithQuota(quotaName!!.selectedItem as Quota)
        } else {
            initQueryAdapterForNameReports()

            byName!!.setDrawBarShadow(false)
            byName!!.setDrawValueAboveBar(true)
            byName!!.description.isEnabled = false
            // scaling can now only be done on x- and y-axis separately
            byName!!.setPinchZoom(false)
            byName!!.setDrawGridBackground(false)

            byName!!.xAxis.isEnabled = false

            val yl = byName!!.axisLeft
            yl.setDrawAxisLine(true)
            yl.setDrawGridLines(true)
            yl.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            yl.axisMinimum = 0f

            val yr = byName!!.axisRight
            yr.setDrawAxisLine(true)
            yr.setDrawGridLines(false)
            yr.axisMinimum = 0f

            byName!!.data = getChartData(service!!.getLoggedDataByName(from.time, to.time))

            byName!!.setFitBars(true)
            byName!!.animateY(1500)
            byName!!.setMaxVisibleValueCount(20)

            val l = byName!!.legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.setDrawInside(false)
            l.formSize = 12f
            l.xEntrySpace = 4f
        }

        byName!!.visibility = View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    private fun initQueryAdapterForNameReports() {

        val adapter = QuotaAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                service!!.findAllQuotas(), false)
        quotaName!!.adapter = QuotasWithDefaultAdapter(adapter, R.layout.spinner_row_nothing_selected, this)

        quotaName!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                val selected = parent.getItemAtPosition(position) as Quota?
                byName?.removeAllViews()
                refreshReportsWithQuota(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

                quotaName!!.setSelection(-1)
            }
        }

        quotaName!!.visibility = View.VISIBLE
    }

    private fun initTypeAdapterForNameReports() {

        val staticAdapter = ArrayAdapter<QuotaType>(this, android.R.layout
                .simple_spinner_dropdown_item)
        staticAdapter.add(QuotaType.DAILY)
        staticAdapter.add(QuotaType.WEEKLY)
        staticAdapter.add(QuotaType.MONTHLY)
        typeName!!.adapter = staticAdapter

        if (passedType != null) typeName!!.setSelection(passedType!!.ordinal)

        typeName!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                val selected = parent.getItemAtPosition(position) as QuotaType
                byType?.removeAllViews()
                refreshReportsForType(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

                typeName!!.setSelection(-1)
            }
        }

        typeName!!.visibility = View.VISIBLE
    }

    private fun refreshReportsForType(selected: QuotaType?) {

        if (selected == null) {
            return
        }
        byType!!.data = getChartData(service!!.getLoggedDataByType(selected, from.time, to.time))
        byName!!.refreshDrawableState()

    }

    private fun refreshReportsWithQuota(selected: Quota?) {

        if (selected == null) return

        val entries = ArrayList<BarEntry>()

        for ((amount, worklog) in service!!.getLoggedDataByQuota(selected, from.time, to.time).withIndex()) {
            // turn your data into Entry objects
            entries.add(BarEntry(amount.toFloat(), worklog.amount!!.toFloat()))
        }

        val dataSet = BarDataSet(entries, "")
        byName!!.data = BarData(dataSet)

        byName!!.refreshDrawableState()
    }

    private fun hideCharts(vararg charts: Chart<*>?) {

        charts.forEach { it?.visibility = View.GONE }
    }

    private fun showDataByType() {

        if (typeName!!.visibility == View.VISIBLE && typeName!!.selectedItem != null) {
            refreshReportsForType(typeName!!.selectedItem as QuotaType)
        } else {

            initTypeAdapterForNameReports()

            byType!!.visibility = View.VISIBLE

            byType!!.setDrawBarShadow(false)
            byType!!.setDrawValueAboveBar(true)
            byType!!.description.isEnabled = false
            // scaling can now only be done on x- and y-axis separately
            byType!!.setPinchZoom(false)
            byType!!.setDrawGridBackground(false)

            byType!!.xAxis.isEnabled = false

            val yl = byType!!.axisLeft
            yl.setDrawAxisLine(true)
            yl.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            yl.setDrawGridLines(true)
            yl.axisMinimum = 0f

            val yr = byType!!.axisRight
            yr.setDrawAxisLine(true)
            yr.setDrawGridLines(false)
            yr.axisMinimum = 0f

            byType!!.data = getChartData(service!!.getAllLoggedDataByType(from.time, to.time))

            byType!!.setFitBars(true)
            byType!!.animateY(1500)

            val l = byType!!.legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.setDrawInside(false)
            l.formSize = 12f
            l.xEntrySpace = 4f
        }
    }

    private fun showDataByCategory() {

        byCategory!!.visibility = View.VISIBLE

        byCategory!!.setUsePercentValues(true)
        byCategory!!.description.isEnabled = false
        byCategory!!.setExtraOffsets(5f, 10f, 5f, 5f)
        byCategory!!.dragDecelerationFrictionCoef = 0.95f

        byCategory!!.isDrawHoleEnabled = true
        byCategory!!.setHoleColor(Color.WHITE)
        byCategory!!.setTransparentCircleColor(Color.WHITE)
        byCategory!!.setTransparentCircleAlpha(50)
        byCategory!!.holeRadius = 10f
        byCategory!!.transparentCircleRadius = 5f
        byCategory!!.setDrawCenterText(true)
        byCategory!!.rotationAngle = 0f
        // enable rotation of the chart by touch
        byCategory!!.isRotationEnabled = true
        byCategory!!.isHighlightPerTapEnabled = true

        byCategory!!.data = categoryChartData
        byCategory!!.highlightValues(null)
        byCategory!!.invalidate()

        byCategory!!.animateY(1500, Easing.EasingOption.EaseInOutQuad)

        val l = byCategory!!.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        byCategory!!.setEntryLabelColor(Color.BLACK)
        byCategory!!.setEntryLabelTextSize(12f)
    }

    private fun getChartData(values: Map<String, Float>): BarData {

        var entries: ArrayList<BarEntry>
        val data = BarData()
        var i = 0
        val spaceForBar = 10f

        values.keys.forEach({

            if (values[it]!! > 0.0) {
                entries = ArrayList()
                entries.add(it.let { BarEntry(i * spaceForBar, values[it]!!) })
                val d = BarDataSet(entries, it)
                d.setDrawIcons(false)
                d.color = Utils.randomColors[i]
                data.addDataSet(d)
                i++
            }
        })

        data.setValueTextSize(spaceForBar)
        data.barWidth = 9f

        return data
    }

    companion object {

        private const val BY_CATEGORY = "By Category"
        private const val BY_NAME = "By Name"
        private const val BY_TYPE = "By Type"
    }
}
