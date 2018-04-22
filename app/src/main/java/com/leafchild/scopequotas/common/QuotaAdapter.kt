package com.leafchild.scopequotas.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.leafchild.scopequotas.R
import com.leafchild.scopequotas.data.Quota
import com.leafchild.scopequotas.settings.SettingsActivity.QUOTA_INDICATOR

/**
 * Created by: leafchild
 * Date: 09/04/2017
 * Project: ScopeQuotas
 */

class QuotaAdapter : ArrayAdapter<Quota> {

    private var showWorklog = true
    private var showProgress = true

    // View lookup cache
    private class ViewHolder {

        internal lateinit var name: TextView
        internal lateinit var range: TextView
        internal var amount: TextView? = null
        internal var progressBar: RoundCornerProgressBar? = null
    }

    constructor(context: Context, quotas: List<Quota>) : super(context, R.layout.quota_item, quotas)

    constructor(context: Context, resource: Int, quotas: List<Quota>, showWorklog: Boolean) : super(context, resource, quotas) {
        this.showWorklog = showWorklog
        this.showProgress = Utils.getDefaultSharedPrefs(context).getBoolean(QUOTA_INDICATOR, true)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var updated = convertView
        // Get the data item for this position
        val viewHolder: ViewHolder

        if (updated == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            updated = inflater.inflate(R.layout.quota_item, parent, false)
            viewHolder.name = updated!!.findViewById(R.id.qName)
            viewHolder.range = updated.findViewById(R.id.qRange)

            if (showWorklog) viewHolder.amount = updated.findViewById(R.id.qAmount)
            if (showProgress) viewHolder.progressBar = updated.findViewById(R.id.quota_progress_bar)

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

        if (quota.archived!!) {
            updated!!.setBackgroundColor(updated.resources.getColor(R.color.light_grey2, context.theme))
        }

        viewHolder.name.text = quota.name
        viewHolder.range.text = quota.min!!.toString() + "-" + quota.max!!.toString()
        viewHolder.amount?.text = String.format(quota.workFlowByLastPeriod.toString() + "%s", "h")
        viewHolder.progressBar?.secondaryProgress = Utils.calculateQuotaProgress(quota.min!!.toFloat(), quota.max)
        viewHolder.progressBar?.progress = Utils.calculateQuotaProgress(quota.workFlowByLastPeriod, quota.max)
        viewHolder.progressBar?.progressColor = Utils.nextColor()
    }
}
