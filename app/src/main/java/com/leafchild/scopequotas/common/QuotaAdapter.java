package com.leafchild.scopequotas.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.data.Quota;

import java.util.List;

/**
 * Created by: leafchild
 * Date: 09/04/2017
 * Project: ScopeQuotas
 */

public class QuotaAdapter extends ArrayAdapter<Quota> {
    // View lookup cache
    private static class ViewHolder {
        TextView name;
    }

    public QuotaAdapter(Context context, List<Quota> quotas) {
        super(context, R.layout.quota_item, quotas);
    }

    public QuotaAdapter(Context context, int resource, List<Quota> quotas) {
        super(context, resource, quotas);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Quota quota = getItem(position);

        if(quota != null) {
            ViewHolder viewHolder;
            if(convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.quota_item, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.qName);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            }
            else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data from the data object via the viewHolder object
            // into the template view.
            viewHolder.name.setText(quota.getName());
        }

        return convertView;
    }
}
