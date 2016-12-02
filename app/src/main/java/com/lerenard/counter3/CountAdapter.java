package com.lerenard.counter3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

/**
 * Created by lerenard on 16-Aug-16.
 */


public class CountAdapter extends StateSavingArrayAdapter<Count> {

    public CountAdapter(Context context, int resource, Bundle savedInstanceState) {
        super(context, resource);
        init(context);
        onRestoreInstanceState(savedInstanceState);
    }

    private void init(Context context) {
        this.context = context;
    }

    static class ViewHolder {
        public TextView name;
        public TextView amount;
    }

    private Context context;

    public CountAdapter(Context context, int resource, List<Count> list) {
        super(context, resource, list);
        init(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            view = inflater.inflate(R.layout.list_view_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.count_name);
            holder.amount = (TextView) view.findViewById(R.id.count_value);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent intent = new Intent(context, CounterActivity.class);
                    intent.putExtra("count", getItem(position));
                    intent.putExtra("index", position);
                    ((Activity) context).startActivityForResult(intent, MainActivity.UPDATE_COUNT);
                }
            });
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        Count count = getItem(position);
        holder.name.setText(count.getName());
        holder.amount.setText(String.format(Locale.getDefault(), "%d", count.getCount()));

        return view;
    }
}
