package com.lerenard.counter3;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lerenard.counter3.helper.ItemTouchHelperAdapter;
import com.lerenard.counter3.helper.ItemTouchHelperViewHolder;

import java.util.ArrayList;
import java.util.Locale;

/**
 * The adapter has an internal copy of the data. When something happens,
 * it notifies the DataSetListener about it so that it can react appropriately. It is
 * allows dragging items around and swiping to delete.
 */
public class CountRecyclerViewAdapter
        extends RecyclerView.Adapter<CountRecyclerViewAdapter.CountViewHolder>
        implements ItemTouchHelperAdapter {

    private static String TAG = "CountRecyclerViewAdapter";
    private final DataSetListener<Count> listener;
    private ArrayList<Count> items;

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        /*if (fromPosition == toPosition) {
            return false;
        }
        Count moved = items.get(fromPosition);
        if (fromPosition > toPosition) {
            for (int i = fromPosition; i > toPosition; --i) {
                items.set(i, items.get(i - 1));
            }
        }
        else {
            for (int i = fromPosition; i < toPosition; ++i) {
                items.set(i, items.get(i + 1));
            }
        }
        items.set(toPosition, moved);*/
        Count moved = items.get(fromPosition);
        items.set(fromPosition, items.get(toPosition));
        items.set(toPosition, moved);
        notifyItemMoved(fromPosition, toPosition);
        listener.onDrag(moved, fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        remove(position);
    }

    public void remove(int position) {
        Count removed = items.remove(position);
        notifyItemRemoved(position);
        listener.onDelete(removed, position);
    }

    public void add(Count count) {
        items.add(count);
        int index = items.size() - 1;
        notifyItemInserted(index);
        listener.onAdd(count, index);
    }

    public void set(int index, Count count) {
        items.set(index, count);
        notifyItemChanged(index);
        listener.onUpdate(count);
    }

    public void insert(int index, Count count) {
        items.add(index, count);
        notifyItemInserted(index);
        listener.onAdd(count, index);
    }

    public class CountViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, ItemTouchHelperViewHolder {
        private TextView name, amount;
        private Count count;

        public CountViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.count_name);
            amount = (TextView) itemView.findViewById(R.id.count_value);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(count, getAdapterPosition());
        }

        public void setCount(Count count) {
            this.count = count;
            name.setText(count.getName());
            amount.setText(String.format(Locale.getDefault(), "%d", count.getCount()));
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public CountRecyclerViewAdapter(
            Context context,
            ArrayList<Count> items,
            DataSetListener<Count> listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public CountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_view_item, parent, false);
        return new CountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CountViewHolder holder, int position) {
        holder.setCount(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
