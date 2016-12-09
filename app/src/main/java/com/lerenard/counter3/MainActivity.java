package com.lerenard.counter3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lerenard.counter3.helper.DatabaseHandler;
import com.lerenard.counter3.helper.SimpleItemTouchHelperCallback;

public class MainActivity extends AppCompatActivity
        implements DataSetListener<Count> {

    static final int NEW_COUNT = 0,
            UPDATE_COUNT = 1;
    private static final String
            KEY_ITEMS = "KEY_ITEMS";
    public static final String
            TAG = "__MainActivity",
            INTENT_EXTRA_INDEX = "INTENT_EXTRA_INDEX",
            INTENT_EXTRA_COUNT = "INTENT_EXTRA_COUNT";

    private CountRecyclerViewAdapter adapter;
    private DatabaseHandler databaseHandler;
    private RecyclerView recyclerView;

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stopping");
        databaseHandler.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CounterActivity.class);
                startActivityForResult(intent, NEW_COUNT);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        databaseHandler = new DatabaseHandler(this);
        adapter = new CountRecyclerViewAdapter(this, databaseHandler.getAllCounts(), this);
        recyclerView.setAdapter(adapter);
        new ItemTouchHelper(
                new SimpleItemTouchHelperCallback(adapter))
                .attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == NEW_COUNT || requestCode == UPDATE_COUNT) {
                Bundle extras = data.getExtras();
                if (extras == null) {
                    throw new IllegalArgumentException("Intent passed RESULT_OK but empty intent.");
                }

                final Count count = (Count) extras.getParcelable(MainActivity.INTENT_EXTRA_COUNT);
                assert count != null;

                if (requestCode == NEW_COUNT) {
                    adapter.add(count);
                }
                else {
                    int index = extras.getInt(MainActivity.INTENT_EXTRA_INDEX);
                    adapter.set(index, count);
                }
                Snackbar.make(
                        findViewById(R.id.main_layout),
                        count.toBriefString(),
                        Snackbar.LENGTH_LONG).show();
            }
            else {
                throw new IllegalArgumentException(
                        "unexpected requestCode " + Integer.toString(requestCode));
            }
        }
    }

    @Override
    public void onAdd(final Count count, final int index) {
        recyclerView.getLayoutManager().scrollToPosition(index);
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                if (index != adapter.getItemCount() - 1) {
                    databaseHandler.addCount(count, index);
                }
                else {
                    databaseHandler.addCount(count);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onDelete(final Count count, final int position) {
        Snackbar.make(
                findViewById(R.id.main_layout),
                R.string.count_deleted,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_count_deleted, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.insert(position, count);
                    }
                }).show();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                databaseHandler.deleteCount(count);
                return null;
            }
        }.execute();
    }

    @Override
    public void onUpdate(final Count count) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                databaseHandler.updateCount(count);
                return null;
            }
        }.execute();
    }

    @Override
    public void onClick(Count count, int position) {
        Intent intent = new Intent(getApplicationContext(), CounterActivity.class);
        intent.putExtra(INTENT_EXTRA_COUNT, count);
        intent.putExtra(INTENT_EXTRA_INDEX, position);
        startActivityForResult(intent, UPDATE_COUNT);
    }

    @Override
    public void onDrag(Count count, int start, int end) {
        databaseHandler.moveCount(count.getId(), start, end);
    }

    @Override
    public void onLongPress(Count count, int position) {

    }
}
