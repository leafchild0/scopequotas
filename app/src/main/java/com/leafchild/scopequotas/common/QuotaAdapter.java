package com.leafchild.scopequotas.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.data.Quota;

import java.util.List;

import static com.leafchild.scopequotas.settings.SettingsActivity.QUOTA_INDICATOR;

/**
 * Created by: leafchild
 * Date: 09/04/2017
 * Project: ScopeQuotas
 */

public class QuotaAdapter extends ArrayAdapter<Quota> {

    private boolean showWorklog = true;
    private boolean showProgress = true;
    private static final String HOURS = " h";

    // View lookup cache
    private static class ViewHolder {

        TextView name;
        TextView amount;
        ProgressBar progress;
    }

    public QuotaAdapter(Context context, List<Quota> quotas) {
        super(context, R.layout.quota_item, quotas);
    }

    public QuotaAdapter(Context context, int resource, List<Quota> quotas, boolean showWorklog) {
        super(context, resource, quotas);
        this.showWorklog = showWorklog;
        this.showProgress = Utils.getDefaultSharedPrefs(context).getBoolean(QUOTA_INDICATOR, true);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        ViewHolder viewHolder;
        if(convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.quota_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.qName);

            viewHolder.progress = (ProgressBar) convertView.findViewById(R.id.quota_progress);
            if(showWorklog) viewHolder.amount = (TextView) convertView.findViewById(R.id.qAmount);
            else viewHolder.progress.setVisibility(View.GONE);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        }
        else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Quota quota = getItem(position);
        if(quota != null) {
            viewHolder.name.setText(quota.getName());

            if(showWorklog) {
                viewHolder.amount.setText(String.format(String.valueOf(quota.getWorkFlowByLastPeriod())
                    + "%s", HOURS));
                if(showProgress) {
                    viewHolder.progress.setProgress(Utils.calculateQuotaProgress(quota.getWorkFlowByLastPeriod(), quota.getMax()));
                }
            }
        }

        return convertView;
    }
}
