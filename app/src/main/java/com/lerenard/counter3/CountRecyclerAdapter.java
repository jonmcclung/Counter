package com.lerenard.counter3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lerenard.counter3.util.Consumer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

/**
 * Created by mc on 02-Dec-16.
 */
public class CountRecyclerAdapter extends RecyclerView.Adapter<CountRecyclerAdapter.CountViewHolder> {

    private ArrayList<Count> items;
    private final Consumer<Integer> callback;

    public class CountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, amount;

        public CountViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.count_name);
            amount = (TextView) itemView.findViewById(R.id.count_value);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            callback.accept(getAdapterPosition());
        }
    }

    public CountRecyclerAdapter(ArrayList<Count> items, final Consumer<Integer> callback) {
        this.items = items;
        this.callback = callback;
    }

    @Override
    public CountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_item, parent, false);
        return new CountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CountViewHolder holder, int position) {
        Count count = items.get(position);
        holder.name.setText(count.getName());
        holder.amount.setText(String.format(Locale.getDefault(), "%d", count.getCount()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(Count count) {
        items.add(count);
    }

    public void set(int index, Count count) {
        items.set(index, count);
    }

    public void remove(int index) {
        items.remove(index);
    }

    public Count get(int index) {
        return items.get(index);
    }

    public ArrayList<Count> getItems() {
        return items;
    }

    public void setItems(ArrayList<Count> items) {
        this.items = items;
    }
}
