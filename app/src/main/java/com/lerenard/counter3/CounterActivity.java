package com.lerenard.counter3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class CounterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        Bundle extras = getIntent().getExtras();
        final EditText titleView = (EditText) findViewById(R.id.counter_title);
        final TextView countDisplayView = (TextView) findViewById(R.id.count_display);
        final int index;
        Count count;
        if (extras != null) {
            count = (Count) extras.getParcelable("count");
            index = extras.getInt("index", -1);
        } else {
            count = new Count();
            index = -1;
        }
        assert count != null;

        titleView.setText(count.getName());
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
                countDisplayView.setText(R.string.starting_value);
            }
        });

        final Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra(
                        "count",
                        new Count(
                                String.valueOf(titleView.getText()),
                                Integer.parseInt(String.valueOf(countDisplayView.getText()))));
                if (index != -1) {
                    data.putExtra("index", index);
                }
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }


    public void showHelp(View view) {
        new AlertDialog.Builder(CounterActivity.this)
                .setTitle(R.string.help_title)
                .setMessage(R.string.help_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {@Override public void onClick(DialogInterface dialog, int which) {}})
                .show();
    }
}
