package com.lerenard.counter3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lerenard.counter3.helper.DatabaseHandler;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CounterActivity extends AppCompatActivity {

    private static final String
            TAG = "COUNTER_ACTIVITY_TAG",
            ALREADY_ADDED_KEY = "ALREADY_ADDED_KEY",
            CURRENT_COUNT_KEY = "CURRENT_COUNT_KEY",
            COUNT_BY_KEY = "COUNT_BY_KEY";
    private Count original;
    private int index, requestCode, countBy;
    private HideCursorEditText nameView;
    private TextView countDisplayView;
    private boolean alreadyAdded;
    private static int[] countByRadioOptions = {1, 2, 3, 5, 10};

    private static int defaultCountBy = 1;
    private HideCursorEditText countByView;
    private RadioGroup radioGroup;
    private List<RadioButton> radioGroupButtons;
    private RadioGroup.OnCheckedChangeListener radioGroupListener =
            new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId != -1) {
                        String newCountBy =
                                ((RadioButton) group.findViewById(checkedId)).getText().toString();
                        updateCountBy(Integer.parseInt(newCountBy));
                        String newCountByString = String.valueOf(newCountBy);
                        countByView.setText(newCountByString);
                    }
                }
            };

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

    private void increment(int howMuch) {
        countDisplayView.setText(String.format(
                Locale.getDefault(),
                "%d",
                howMuch + Integer.parseInt(
                        String.valueOf((countDisplayView).getText()))));
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

    private float spaceNeeded(TextView textView) {
        Layout layout = textView.getLayout();
        if (layout != null) {
            Log.d(TAG, "line width: " + layout.getLineWidth(0));
            Log.d(TAG, "view width: " + (textView.getWidth() - textView.getPaddingLeft() -
                                         textView.getPaddingRight()));
            return Math.max(layout.getLineWidth(0), textView.getMinWidth()) -
                   (textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight());
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        countBy = this.getPreferences(Context.MODE_PRIVATE).getInt(COUNT_BY_KEY, defaultCountBy);

        setContentView(R.layout.activity_counter);
        nameView = (HideCursorEditText) findViewById(R.id.counter_title);
        if (nameView == null) {
            Log.d(TAG, "nameView is null ):");
        }
        countDisplayView = (TextView) findViewById(R.id.count_display);
        ImageView decrementView = (ImageView) findViewById(R.id.decrement_image);
        ImageView incrementView = (ImageView) findViewById(R.id.increment_image);
        countByView = (HideCursorEditText) findViewById(R.id.count_by_amount);
        countDisplayView.bringToFront();

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

        decrementView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                increment(-countBy);
            }
        });

        incrementView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                increment(countBy);
            }
        });

        final Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        final Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        final Button revertButton = (Button) findViewById(R.id.revertButton);
        revertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revert();
            }
        });

        instantiateRadioGroup();

        countByView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) v).setCursorVisible(true);
            }
        });

        countByView.setOnKeyPreImeListener(new OnKeyPreImeListener() {
            @Override
            public boolean onKeyPreIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    String newCountBy = countByView.getText().toString();
                    if (newCountBy.equals("")) {
                        newCountBy = String.format(Locale.getDefault(), "%d", 1);
                        countByView.setText(newCountBy);
                    }
                    updateCountBy(Integer.parseInt(newCountBy));
                    updateRadioButtons(newCountBy);
                }
                return false;
            }
        });

        countByView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        try {
                            String newCountByString = v.getText().toString();
                            updateCountBy(Integer.parseInt(newCountByString));
                            updateRadioButtons(newCountByString);
                        } catch (NumberFormatException e) {
                            Snackbar.make(
                                    findViewById(R.id.count_layout),
                                    "Invalid number (too " +
                                    "big or too small?)",
                                    Snackbar.LENGTH_LONG).show();
                            v.setText(String.valueOf(countBy));
                        }
                        InputMethodManager inputManager =
                                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(
                                getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        v.setCursorVisible(false);
                        return true;
                    default:
                        return false;
                }
            }
        });

        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence charSequence, int start, int count, int before) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "minimum width: " + countByView.getMinWidth());
                Log.d(TAG, "space needed: " + spaceNeeded(countByView));
                float spaceNeeded_ = spaceNeeded(countByView);
                if (spaceNeeded_ != 0) {
                    spaceNeeded_ = shiftRadioButtons(spaceNeeded_);
                    if (spaceNeeded_ > 0) {
                        Log.d(TAG, "shrinking text size");
                        countByView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        Log.d(TAG, "size is " + countByView.getTextSize());
                    }
                }
            }
        };
        countByView.addTextChangedListener(watcher);
        Log.d(TAG, "");
        String newCountBy = String.valueOf(countBy);
        countByView.setText(newCountBy);
//        watcher.afterTextChanged(countByView.getEditableText()); // doesn't seem to have an effect
        updateRadioButtons(newCountBy);
        final ViewTreeObserver countByTreeObserver = countByView.getViewTreeObserver();
        countByTreeObserver.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    private boolean flag = false;
                    @Override
                    public void onGlobalLayout() {
                        if (!flag) {
                            shiftRadioButtons(spaceNeeded(countByView));
                        }
                        flag = true;
                    }
                });
    }


    private int shiftRadioButtons(float spaceNeeded) {
        LinearLayout.LayoutParams oldParams =
                (LinearLayout.LayoutParams) radioGroup.getChildAt(0).getLayoutParams();
        final int oldMargin = oldParams.rightMargin;
        int margin = (int) (Math.floor(-spaceNeeded / (radioGroupButtons.size() * 2))) +
                     oldMargin;
        Log.d(
                TAG,
                "spaceNeeded: " + spaceNeeded + ", old margin: " + oldMargin + ", new margin: " +
                margin);
        int spaceStillNeeded = 0;
        if (margin < 0) {
            spaceStillNeeded = -margin;
            margin = 0;
        }
        int radius = (int) getResources().getDimension(R.dimen.count_by_radio_button_radius);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                radius,
                radius);
        AnimationSet animationSet = new AnimationSet(true);
        final int finalMargin = margin;
        for (final RadioButton button : radioGroupButtons) {
            Animation animation = new Animation() {

                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    int margin = oldMargin + (int) ((finalMargin - oldMargin) * interpolatedTime);
                    params.setMargins(
                            margin, 0,
                            margin, 0);
                    button.setLayoutParams(params);
                }
            };
            animation.setDuration(100);
            animationSet.addAnimation(animation);
        }
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        radioGroup.startAnimation(animationSet);
        return spaceStillNeeded;
    }

    private void save() {
        original = getCount();
    }

    private void updateRadioButtons(String newCountByString) {
        radioGroup.setOnCheckedChangeListener(null);
        radioGroup.clearCheck();
        for (RadioButton radioButton : radioGroupButtons) {
            if (radioButton.getText().equals(newCountByString)) {
                radioButton.setChecked(true);
                break;
            }
        }
        radioGroup.setOnCheckedChangeListener(radioGroupListener);
    }

    private void updateCountBy(int newCountBy) {
        countBy = newCountBy;
        SharedPreferences.Editor editor =
                CounterActivity.this.getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(COUNT_BY_KEY, countBy);
        editor.apply();
    }

    private void instantiateRadioGroup() {
        radioGroupButtons = new ArrayList<>(countByRadioOptions.length);
        radioGroup = (RadioGroup) findViewById(R.id.count_by_radio_group);
        for (final int countByOption : countByRadioOptions) {
            RadioButton radioButton = (RadioButton) getLayoutInflater()
                    .inflate(R.layout.count_by_radio_button, radioGroup, false);
            radioGroupButtons.add(radioButton);
            radioButton.setText(String.valueOf(countByOption));
            radioGroup.addView(radioButton);
        }
        radioGroup.setOnCheckedChangeListener(radioGroupListener);
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
        Log.d(TAG, "stopping");

        final Count count = getCount();
        final DatabaseHandler db = MainActivity.getDatabase();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!alreadyAdded) {
                    db.addCount(count);
                    alreadyAdded = true;
                }
                else if (!count.equals(original)) {
                    db.updateCount(count);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
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