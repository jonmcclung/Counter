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

    private static final String TAG = "COUNTER_ACTIVITY_TAG";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        Bundle extras = getIntent().getExtras();
        final EditText nameView = (EditText) findViewById(R.id.counter_title);
        final TextView countDisplayView = (TextView) findViewById(R.id.count_display);
        final Count count;
        final int index;
        if (extras != null) {
            count = (Count) extras.getParcelable(MainActivity.INTENT_EXTRA_COUNT);
            index = extras.getInt(MainActivity.INTENT_EXTRA_INDEX);
        } else {
            count = new Count();
            index = -1;
        }
        assert count != null;
        Log.d(TAG, Long.toString(count.getId()));
        nameView.setText(count.getName());
        countDisplayView.setText(String.format(Locale.getDefault(), "%d", count.getCount()));

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
                countDisplayView.setText(String.format(Locale.getDefault(), "%d", 0));
            }
        });

        final Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                count.setName(String.valueOf(nameView.getText()));
                count.setCount(Integer.parseInt(String.valueOf(countDisplayView.getText())));
                data.putExtra(
                        MainActivity.INTENT_EXTRA_COUNT,
                        count);
                if (index != -1) {
                    data.putExtra(MainActivity.INTENT_EXTRA_INDEX, index);
                }
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }


    public void showHelp() {
        new AlertDialog.Builder(CounterActivity.this)
                .setTitle(R.string.help_title)
                .setMessage(R.string.help_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {@Override public void onClick(DialogInterface dialog, int which) {}})
                .show();
    }
}
