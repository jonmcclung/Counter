package com.lerenard.counter3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lerenard on 16-Aug-16.
 */


public class CountAdapter extends ArrayAdapter<Count> {

    public void remove(int index) {
        list.remove(index);
    }

    static class ViewHolder {
        public TextView name;
        public TextView amount;
    }

    private Context context;
    private List<Count> list;

    public CountAdapter(Context context, int resource, List<Count> list) {
        super(context, resource, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Count count = list.get(position);
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
                    intent.putExtra("count", count);
                    intent.putExtra("index", position);
                    ((Activity) context).startActivityForResult(intent, MainActivity.UPDATE_COUNT);
                }
            });
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(count.getName());
        holder.amount.setText(Integer.toString(count.getCount()));
        //holder.amount.setText("0");

        return view;
    }
}
