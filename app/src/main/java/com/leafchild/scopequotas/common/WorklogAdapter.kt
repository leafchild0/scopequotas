package com.leafchild.scopequotas.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.leafchild.scopequotas.R
import com.leafchild.scopequotas.data.Quota
import com.leafchild.scopequotas.data.Worklog
import com.shawnlin.numberpicker.NumberPicker

/**
 * Created by: leafchild
 * Date: 09/04/2017
 * Project: ScopeQuotas
 */

class WorklogAdapter(context: Context, private val quotas: List<Quota>)
    : ArrayAdapter<Quota>(context, R.layout.worklog_quota_item, quotas) {

    var data: MutableMap<Quota, Int> = mutableMapOf()

    // View lookup cache
    private class ViewHolder {

        internal var name: TextView? = null
        internal var numberPicker: NumberPicker? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var updated = convertView
        val viewHolder: ViewHolder

        if (updated == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            updated = inflater.inflate(R.layout.worklog_quota_item, parent, false)
            viewHolder.name = updated!!.findViewById(R.id.qName)
            viewHolder.numberPicker = updated.findViewById(R.id.number_picker)

            viewHolder.numberPicker!!.setOnValueChangedListener { _, _, newVal ->
                data[quotas[position]] = newVal
            }

            // Cache the viewHolder object inside the fresh view
            updated.tag = viewHolder
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = updated.tag as ViewHolder
        }

        updateValues(position, viewHolder, updated)

        return updated
    }

    private fun updateValues(position: Int, viewHolder: ViewHolder, updated: View?) {

        val quota = getItem(position)
        if (quota != null) {
            viewHolder.name!!.text = quota.name

            viewHolder.numberPicker!!.maxValue = quota.max!!
        }
    }

    fun getFilledWorklogs(): Map<Quota, Int> {
        return data
    }
}
