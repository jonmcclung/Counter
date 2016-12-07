package com.lerenard.counter3;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lerenard.counter3.database.DatabaseHandler;
import com.lerenard.counter3.util.Consumer;

import java.util.ArrayList;
import java.util.Locale;

/**
 * The adapter is only responsible for the UI stuff. It delegates any events it notices to
 * the listener, so that we have a good separation of UI from logic.
 */
public class CountRecyclerViewAdapter
        extends CursorRecyclerViewAdapter<CountRecyclerViewAdapter.CountViewHolder>
        implements ItemTouchHelperAdapter {

    private static String TAG = "CountRecyclerViewAdapter";
    private final DataSetListener<Count> listener;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //TODO
    }

    @Override
    public void onItemDismiss(int position) {
        //TODO
    }

    public class CountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, amount;
        private Count count;

        public CountViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.count_name);
            amount = (TextView) itemView.findViewById(R.id.count_value);
            itemView.setOnClickListener(this);
            /*itemView.setOnTouchListener(new OnSwipeListener(mContext) {
                @Override
                public void onSwipeRight() {
                    Log.d(TAG, "swiping right on " + name.getText() + ", " + amount.getText());
                }

                @Override
                public void onSwipeLeft() {
                    Log.d(TAG, "swiping left on " + name.getText() + ", " + amount.getText());
                }
            });*/
        }

        @Override
        public void onClick(View v) {
            listener.onClick(count);
        }

        public void setCount(Count count) {
            this.count = count;
            name.setText(count.getName());
            amount.setText(String.format(Locale.getDefault(), "%d", count.getCount()));
        }
    }

    public CountRecyclerViewAdapter(
            Context context,
            Cursor cursor,
            DataSetListener<Count> listener) {
        super(context, cursor);
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
    public void onBindViewHolder(final CountViewHolder holder, Cursor cursor) {
        final Count count = DatabaseHandler.getCountFromCursor(cursor);
        holder.setCount(count);
    }

    @Override
    public int getItemCount() {
        return getCursor().getCount();
    }
}
