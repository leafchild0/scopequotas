package com.leafchild.scopequotas.common

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.leafchild.scopequotas.R
import com.leafchild.scopequotas.data.Quota
import com.shawnlin.numberpicker.NumberPicker

/**
 * Created by: leafchild
 * Date: 09/04/2017
 * Project: ScopeQuotas
 */

class WorklogAdapter(context: Context, quotas: List<Quota>)
    : ArrayAdapter<Quota>(context, R.layout.worklog_quota_item, quotas) {

    var data: MutableMap<Quota, Int> = mutableMapOf()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val updated: View?
        val quota = getItem(position)

        // If there's no view to re-use, inflate a brand new view for row
        val inflater = LayoutInflater.from(context)
        updated = inflater.inflate(R.layout.worklog_quota_item, parent, false)
        val name = updated!!.findViewById(R.id.wName) as TextView

        val numberPicker = updated.findViewById(R.id.number_picker) as NumberPicker
        numberPicker.setOnValueChangedListener { _, _, newVal ->
            data[quota] = newVal
        }
        numberPicker.value = data[quota] ?: 0

        updateValues(quota, name, numberPicker)

        return updated
    }

    private fun updateValues(quota: Quota, name: TextView, numberPicker: NumberPicker) {

        name.text = quota.name
        numberPicker.maxValue = quota.max!!
    }

    fun getFilledWorklogs(): Map<Quota, Int> {

        return data
    }
}
