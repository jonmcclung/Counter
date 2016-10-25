package com.lerenard.counter3;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class CounterActivity extends AppCompatActivity {

    private Count count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        Bundle extras = getIntent().getExtras();
        final EditText titleView = (EditText) findViewById(R.id.counter_title);
        final TextView countDisplayView = (TextView) findViewById(R.id.count_display);
        final int index;
        if (extras != null) {
            count = (Count) extras.getSerializable("count");
            index = extras.getInt("index", -1);
        } else {
            count = new Count();
            index = -1;
        }

        titleView.setText(count.getName());
        countDisplayView.setText(String.format(Locale.getDefault(), "%d", count.getCount()));

        countDisplayView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((TextView) v).setText(Integer.toString(1 + Integer.parseInt(String.valueOf(((TextView) v).getText()))));
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
                count.setCount(Integer.parseInt(String.valueOf(countDisplayView.getText())));
                count.setName(String.valueOf(titleView.getText()));
                Intent data = new Intent();
                data.putExtra("count", count);
                if (index != -1) {
                    data.putExtra("index", index);
                }
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }


}
