package com.lerenard.counter3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int NEW_COUNT = 0,
            UPDATE_COUNT = 1;

    private CountAdapter adapter;

    public void gotoCounter(View view) {

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

        ListView list = (ListView) findViewById(R.id.list);
        list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    fab.setVisibility(View.INVISIBLE);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    fab.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        Context context = getApplicationContext();
        List<Count> countArray = new ArrayList<>();
        countArray.add(new Count("one", 1));
        adapter = new CountAdapter(this, R.layout.list_view_item, countArray);
        list.setAdapter(adapter);

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

        //noinspection SimplifiableIfStatement
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
                Count count = (Count) extras.getSerializable("count");
                if (requestCode == NEW_COUNT) {
                    adapter.add(count);
                } else /*if (requestCode == UPDATE_COUNT)*/ {
                    int index = extras.getInt("index");
                    adapter.remove(index);
                    adapter.insert(count, index);
                }
            } else {
                throw new IllegalArgumentException("unexpected requestCode " + Integer.toString(requestCode));
            }
        }
    }
}
