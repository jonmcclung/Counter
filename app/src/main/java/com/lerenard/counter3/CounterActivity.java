package com.lerenard.counter3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class CounterActivity extends AppCompatActivity {

    private static final String
            TAG = "COUNTER_ACTIVITY_TAG",
            ALREADY_ADDED_KEY = "ALREADY_ADDED_KEY",
            CURRENT_COUNT_KEY = "CURRENT_COUNT_KEY";
    private Count original;
    private int index, requestCode;
    private EditText nameView;
    private TextView countDisplayView;
    private boolean alreadyAdded;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.counter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                showHelp();
                return true;
        }
        return false;
    }

    private void revert() {
        setData(original);
    }

    private void setData(Count count) {
        nameView.setText(count.getName());
        countDisplayView.setText(String.format(Locale.getDefault(), "%d", count.getCount()));
    }

    private void reset() {
        countDisplayView.setText(String.format(Locale.getDefault(), "%d", 0));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_counter);
        nameView = (EditText) findViewById(R.id.counter_title);
        countDisplayView = (TextView) findViewById(R.id.count_display);

        Bundle extras = getIntent().getExtras();
        requestCode = extras.getInt(MainActivity.INTENT_EXTRA_REQUEST_CODE);
        switch (requestCode) {
            case MainActivity.UPDATE_COUNT:
                original = (Count) extras.getParcelable(MainActivity.INTENT_EXTRA_COUNT);
                index = extras.getInt(MainActivity.INTENT_EXTRA_INDEX);
                alreadyAdded = true;
                break;
            case MainActivity.NEW_COUNT:
                original = new Count();
                index = -1;
                alreadyAdded = false;
                break;
            default:
                throw new IllegalStateException(
                        "expected request code to be one of MainActivity.NEW_COUNT, MainActivity" +
                        ".UPDATE_COUNT. Instead I got " + requestCode);
        }
        if (savedInstanceState != null) {
            alreadyAdded = savedInstanceState.getBoolean(ALREADY_ADDED_KEY);
            setData(savedInstanceState.<Count>getParcelable(CURRENT_COUNT_KEY));
        }
        else {
            setData(original);
        }
        assert original != null;
        Log.d(TAG, Long.toString(original.getId()));

        countDisplayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) v).setText(String.format(
                        Locale.getDefault(),
                        "%d",
                        1 + Integer.parseInt(
                                String.valueOf(((TextView) v).getText()))));
            }
        });

        final TextView resetButton = (TextView) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        final Button revertButton = (Button) findViewById(R.id.revertButton);
        revertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revert();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ALREADY_ADDED_KEY, true);
        outState.putParcelable(CURRENT_COUNT_KEY, getCount());
    }

    private Count getCount() {
        final EditText nameView = (EditText) findViewById(R.id.counter_title);
        final TextView countDisplayView = (TextView) findViewById(R.id.count_display);
        Count count = new Count(original);
        count.setName(String.valueOf(nameView.getText()));
        count.setCount(Integer.parseInt(String.valueOf(countDisplayView.getText())));
        return count;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop, alreadyAdded: " + alreadyAdded);
        Log.d(TAG, MainActivity.getDatabase().toString());
        Count count = getCount();
        if (!alreadyAdded) {
            MainActivity.getDatabase().addCount(count);
            alreadyAdded = true;
        }
        else {
            if (!count.equals(original)) {
                Log.d(TAG, "updating since " + count.toString() + " != " + original.toString());
                MainActivity.getDatabase().updateCount(count);
            }
        }
        Log.d(TAG, "onStop, exiting");
        Log.d(TAG, MainActivity.getDatabase().toString());
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        Log.d(TAG, MainActivity.getDatabase().toString());
        Intent data = new Intent();
        data.putExtra(MainActivity.INTENT_EXTRA_COUNT, getCount());
        if (index != -1) {
            data.putExtra(MainActivity.INTENT_EXTRA_INDEX, index);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    public void showHelp() {
        new AlertDialog.Builder(CounterActivity.this)
                .setTitle(R.string.help_title)
                .setMessage(R.string.help_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }
}
