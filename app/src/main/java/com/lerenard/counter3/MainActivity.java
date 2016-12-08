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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lerenard.counter3.database.DatabaseHandler;
import com.lerenard.counter3.util.Consumer;

public class MainActivity extends AppCompatActivity implements DataSetListener<Count> {

    static final int NEW_COUNT = 0,
            UPDATE_COUNT = 1;
    private static final String
            KEY_ITEMS = "KEY_ITEMS";
    public static final String
            TAG = "__MainActivity",
            INTENT_EXTRA_COUNT = "INTENT_EXTRA_COUNT";

    private CountRecyclerViewAdapter adapter;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHandler.getCursor().close();
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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false));

        databaseHandler = new DatabaseHandler(this);
        adapter = new CountRecyclerViewAdapter(this, databaseHandler.getCursor(), this);
        recyclerView.setAdapter(adapter);
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
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    adapter.changeCursor(databaseHandler.getCursor());
                                    Snackbar.make(
                                            findViewById(R.id.main_layout),
                                            count.toBriefString(),
                                            Snackbar.LENGTH_LONG).show();
                                }
                            });
                            return null;
                        }
                    }.execute();
                }
                else {
//                    Count updateMe = extras.getParcelable(INTENT_EXTRA_COUNT);
//                    updateMe.copyFrom(count);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            databaseHandler.updateCount(count);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.changeCursor(databaseHandler.getCursor());
                                    Snackbar.make(
                                            findViewById(R.id.main_layout),
                                            count.toBriefString(),
                                            Snackbar.LENGTH_LONG).show();
                                }
                            });
                            return null;
                        }
                    }.execute();
                }
            }
            else {
                throw new IllegalArgumentException(
                        "unexpected requestCode " + Integer.toString(requestCode));
            }
        }
    }

    @Override
    public void onDelete(final Count count) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                databaseHandler.deleteCount(count);
                adapter.changeCursor(databaseHandler.getCursor());
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
                adapter.changeCursor(databaseHandler.getCursor());
                return null;
            }
        }.execute();
    }

    @Override
    public void onClick(Count count) {
        Intent intent = new Intent(getApplicationContext(), CounterActivity.class);
        intent.putExtra(INTENT_EXTRA_COUNT, count);
        startActivityForResult(intent, UPDATE_COUNT);
    }

    @Override
    public void onDrag(Count count, int start, int end) {

    }

    @Override
    public void onLongPress(Count count) {

    }
}
