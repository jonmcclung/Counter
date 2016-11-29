package com.lerenard.counter3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Created by lerenard on 16-Aug-16.
 */


public class CountAdapter extends ArrayAdapter<Count> implements Serializable, Parcelable {

    public void remove(int index) {
        list.remove(index);
    }
    public void set(int index, Count count) {
        list.set(index, count);
    }

    public void insert(int index, Count count) {
        list.add(index, count);
    }

    public static final Creator<CountAdapter> CREATOR = new Creator<CountAdapter>() {
        @Override
        public CountAdapter createFromParcel(Parcel in) {
            return new CountAdapter(in);
        }

        @Override
        public CountAdapter[] newArray(int size) {
            return new CountAdapter[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this);
    }

    static class ViewHolder {
        public TextView name;
        public TextView amount;
    }

    private Context context;
    private List<Count> list;
    private int resource;

    public CountAdapter(Context context, int resource, List<Count> list) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.list = list;
    }

    public CountAdapter(CountAdapter countAdapter) {
        this(countAdapter.context, countAdapter.resource, countAdapter.list);
    }

    protected CountAdapter(Parcel in) {
        this((CountAdapter) in.readSerializable());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Count count = list.get(position);
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
                    intent.putExtra("count", list.get(position));
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
        holder.amount.setText(String.format(Locale.getDefault(), "%d", count.getCount()));

        return view;
    }
}
