package com.gameofcoding.statussaver.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.gameofcoding.statussaver.R;
import com.gameofcoding.statussaver.adapters.DataLoader;
import com.gameofcoding.statussaver.adapters.WhatsAppStatusAdapter;
import com.gameofcoding.spy.utils.Utils;
import com.gameofcoding.spy.utils.XLog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
	
    private Context mContext = this;
    private Utils mUtils = new Utils(mContext);
    public static SwipeRefreshLayout mSwipeRefreshLyt;
    public static RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	mSwipeRefreshLyt = findViewById(R.id.swipeContainer);
	mRecyclerView = findViewById(R.id.whatsapp_statuses);
	mSwipeRefreshLyt.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
		@Override
		public void onRefresh() {
		    showStatuses();
		}
	    });
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
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
	switch(item.getItemId()) {
	case R.id.about:
	    startActivity(new Intent(mContext, AboutActivity.class));
	    break;
	}
	return super.onMenuItemSelected(featureId, item);
    }
	
    public void showStatuses() {
	final DataLoader dataLoader = new DataLoader(mContext);
	dataLoader.reloadData(new DataLoader.Listener() {
		@Override
		public void onFinish() {
		    mRecyclerView.post(new Runnable() {
			    @Override
			    public void run() {
				WhatsAppStatusAdapter adapter =
				    new WhatsAppStatusAdapter(mContext, dataLoader);
				mRecyclerView.setAdapter(adapter);
			    }
			});
		}
	    });
    }
}

