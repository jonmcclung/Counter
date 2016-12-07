package com.lerenard.counter3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.lerenard.counter3.database.DatabaseHandler;
import com.lerenard.counter3.util.Consumer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int NEW_COUNT = 0,
            UPDATE_COUNT = 1;
    private static final String
            KEY_ITEMS = "KEY_ITEMS";
    public static final String
            TAG = "__MainActivity",
            INTENT_EXTRA_COUNT = "INTENT_EXTRA_COUNT",
            INTENT_EXTRA_INDEX = "INTENT_EXTRA_INDEX";

    private RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    private CountRecyclerAdapter adapter;
    private DatabaseHandler databaseHandler;

    private Consumer<Integer> update = new Consumer<Integer>() {
        @Override
        public void accept(Integer index) {
            Intent intent = new Intent(getApplicationContext(), CounterActivity.class);
            intent.putExtra(INTENT_EXTRA_COUNT, adapter.get(index));
            intent.putExtra(INTENT_EXTRA_INDEX, index);
            startActivityForResult(intent, UPDATE_COUNT);
        }
    };

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
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false));

        adapter = new CountRecyclerAdapter(new ArrayList<Count>(), update);
        recyclerView.setAdapter(adapter);
        databaseHandler = new DatabaseHandler(this);
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                adapter.setItems(databaseHandler.getAllCounts());
                return null;
            }
        }.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_ITEMS, ((CountRecyclerAdapter) recyclerView.getAdapter()).getItems());
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
                if (requestCode == NEW_COUNT) {
                    new AsyncTask<Void, Void, Void>() {
                        protected Void doInBackground(Void... params) {
                            databaseHandler.addCount(count);
                            adapter.add(count);
                            return null;
                        }
                    }.execute();
                }
                else {
                    Count updateMe = adapter.get(
                            extras.getInt(MainActivity.INTENT_EXTRA_INDEX));
                    updateMe.copyFrom(count);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            databaseHandler.updateCount(count);
                            return null;
                        }
                    }.execute();
                }
                Snackbar.make(findViewById(R.id.main_layout), count.toString(), Snackbar.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }
            else {
                throw new IllegalArgumentException("unexpected requestCode " + Integer.toString(requestCode));
            }
        }
    }
}
