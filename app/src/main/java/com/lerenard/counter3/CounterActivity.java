package com.lerenard.counter3;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        if (extras != null) {
            Count count = (Count) extras.getSerializable("count");
            titleView.setText(count.getName());
            countDisplayView.setText(String.format(Locale.getDefault(), "%d", count.getCount()));
        }

        final TextView resetButton = (TextView) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDisplayView.setText(R.string.starting_value);
            }
        });
    }


}
