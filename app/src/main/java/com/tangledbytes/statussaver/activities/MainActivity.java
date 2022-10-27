package com.tangledbytes.statussaver.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tangledbytes.statussaver.R;
import com.tangledbytes.statussaver.adapters.DataLoader;
import com.tangledbytes.statussaver.adapters.WhatsAppStatusAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static SwipeRefreshLayout mSwipeRefreshLyt;
    public static RecyclerView mRecyclerView;
    private final Context mContext = this;
    boolean mHasFinishedLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Show splash screen until all images are loaded
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> !mHasFinishedLoading);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.tb_main));
        mSwipeRefreshLyt = findViewById(R.id.swipeContainer);
        mRecyclerView = findViewById(R.id.whatsapp_statuses);
        mSwipeRefreshLyt.setOnRefreshListener(this::showStatuses);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        showStatuses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            startActivity(new Intent(mContext, AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void showStatuses() {
        final DataLoader dataLoader = new DataLoader(mContext);
        dataLoader.reloadData(() -> mRecyclerView.post(() -> {
            WhatsAppStatusAdapter adapter =
                    new WhatsAppStatusAdapter(mContext, dataLoader);
            mRecyclerView.setAdapter(adapter);
            mHasFinishedLoading = true;
        }));
    }
}