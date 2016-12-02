package com.lerenard.counter3;

import android.content.Context;
import android.content.Intent;
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

import com.lerenard.counter3.util.Consumer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int NEW_COUNT = 0,
            UPDATE_COUNT = 1;
    private static final String
            KEY_LIST_VIEW_STATE = "KEY_LIST_VIEW_STATE";
    public static final String
            TAG = "__MainActivity",
            INTENT_EXTRA_COUNT = "INTENT_EXTRA_COUNT",
            INTENT_EXTRA_INDEX = "INTENT_EXTRA_INDEX";

    private RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    private CountRecyclerAdapter adapter;

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
        if (BuildConfig.DEBUG && !recyclerView.getLayoutManager().canScrollVertically()) {
            throw new AssertionError();
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        Context context = getApplicationContext();
        if (true) {
//        if (savedInstanceState == null) {
            List<Count> countArray = new ArrayList<>();
            countArray.add(new Count("two" , 2));
            countArray.add(new Count("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum volutpat nisl quis aliquet molestie. Vestibulum nec metus eget magna dictum blandit. Proin id est nec orci euismod facilisis. Curabitur at scelerisque nisi, et molestie enim. Sed quis magna euismod odio sagittis porttitor quis aliquet mi. Integer placerat neque eget porttitor congue. In id lorem neque. Nunc eleifend leo et quam sollicitudin sagittis. Vivamus ultricies accumsan felis eget rutrum.\n" + "\n" + "Sed eget est vestibulum, luctus lacus eu, bibendum nisl. Phasellus a massa turpis. Nam sed risus consectetur, blandit lectus ut, porta nisi. Suspendisse a egestas elit. Suspendisse potenti. Ut at vestibulum urna. Fusce placerat porta purus, eget dapibus odio tempor vel.\n" + "\n" + "Integer sapien elit, tempor eu facilisis a, volutpat eu enim. Pellentesque sed interdum neque. Vivamus lacus mi, molestie vel mi sodales, accumsan finibus quam. In ac vestibulum erat. Interdum et malesuada fames ac ante ipsum primis in faucibus. Suspendisse vitae dui quis augue aliquam maximus. Aliquam consectetur nunc vitae purus iaculis, id rutrum velit tincidunt. Mauris consectetur laoreet arcu ut consequat. Quisque tincidunt, nisl quis venenatis auctor, mauris velit finibus tellus, ornare ullamcorper elit erat vel nibh. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla viverra fermentum felis. Vestibulum tincidunt et tellus at sodales. Morbi nec volutpat mauris. Ut eu consectetur dolor. Nam facilisis lobortis accumsan. Ut tristique eget est eu laoreet.\n" + "\n" + "In hendrerit non risus eget auctor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum in euismod turpis. Fusce viverra fermentum velit. Sed ac magna sollicitudin, fringilla elit eu, ultrices ante. Quisque imperdiet nibh sed metus consectetur dignissim. Integer tempus sodales ante, sit amet porttitor nisl malesuada ut. Sed sodales vestibulum odio in finibus. Nam viverra sem ac suscipit interdum. Cras nec libero vestibulum, cursus elit non, blandit nisi. Donec elementum ante dui, ac mattis lorem pharetra eget. In hac habitasse platea dictumst.\n" + "\n" + "Aenean malesuada nibh purus, aliquam hendrerit neque dictum non. Fusce vel lectus et ligula faucibus vestibulum vel lacinia dui. Vivamus in gravida felis. Cras a erat mollis nunc lobortis vulputate et vel mauris. Curabitur ut fringilla metus. Mauris feugiat odio tincidunt tellus convallis, in auctor dolor vestibulum. Integer vel viverra libero. Fusce nec arcu nunc.", 1));
            countArray.add(new Count("one", 1));
            Log.d(TAG, countArray.toString());
            adapter = new CountRecyclerAdapter(countArray, update);
            Snackbar.make(findViewById(R.id.main_layout), "onCreate()", Snackbar.LENGTH_SHORT).show();
        }
        else {
            adapter = new CountRecyclerAdapter(new ArrayList<Count>(), update);
            Snackbar.make(findViewById(R.id.main_layout), "onCreate(" + savedInstanceState.toString() + ")", Snackbar.LENGTH_SHORT).show();
            if (savedInstanceState.containsKey(KEY_LIST_VIEW_STATE)) {
//                recyclerView.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_LIST_VIEW_STATE));
            }
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        adapter.onSaveInstanceState(outState);
//        outState.putParcelable(KEY_LIST_VIEW_STATE, recyclerView.onSaveInstanceState());
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
                Count count = (Count) extras.getParcelable(MainActivity.INTENT_EXTRA_COUNT);
                assert count != null;
                Snackbar.make(findViewById(R.id.main_layout), count.toString(), Snackbar.LENGTH_LONG).show();
                if (requestCode == NEW_COUNT) {
                    adapter.add(count);
                } else /*if (requestCode == UPDATE_COUNT)*/ {
                    adapter.get(
                            extras.getInt(MainActivity.INTENT_EXTRA_INDEX)).copyFrom(count);
                }
                adapter.notifyDataSetChanged();
            } else {
                throw new IllegalArgumentException("unexpected requestCode " + Integer.toString(requestCode));
            }
        }
    }
}
