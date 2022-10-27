package com.tangledbytes.statussaver.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.tangledbytes.statussaver.R;
import com.tangledbytes.statussaver.utils.Utils;
public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";
	private final int PERMISSIONS_REQUEST_CODE = 100;
	private final int OPEN_SETTINGS_REQUEST_CODE = 101;
	private final String[] PERMISSIONS_NEEDED = new String[] {
		Manifest.permission.WRITE_EXTERNAL_STORAGE,
		Manifest.permission.READ_CONTACTS
	};
	private final Context mContext = this;
	private final Utils mUtils = new Utils(mContext);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		if (grantPermissions())
			launchMainActivity();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSIONS_REQUEST_CODE) {
			handlePermissionResult();
		}
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == OPEN_SETTINGS_REQUEST_CODE) {
			handlePermissionResult();
		}
    }

    private void handlePermissionResult() {
       	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
			return;
		for (String permission : PERMISSIONS_NEEDED) {
			if (mUtils.hasPermission(permission)) {
				// User granted this permission, check for next one
				continue;
			}
			// User not granted permission
			AlertDialog.Builder permissionRequestDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_permission_title)
				.setMessage(R.string.dialog_permission_message)
				.setCancelable(false)
				.setNegativeButton(R.string.exit,
						(dialog, whichButton) -> {
							mUtils.showToast(R.string.closing_app);
							setResult(RESULT_CANCELED);
							finish();
						});
			if (!shouldShowRequestPermissionRationale(permission)) {
				// User clicked on "Don't ask again", show dialog to navigate him to
				// settings
				permissionRequestDialog
					.setPositiveButton(R.string.go_to_settings,
							(dialog, whichButton) -> {
								Intent intent =
									new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
								Uri uri =
									Uri.fromParts("package", getPackageName(), null);
								intent.setData(uri);
								startActivityForResult(intent,
													   OPEN_SETTINGS_REQUEST_CODE);
							})
					.show();
			} else {
				// User clikced on 'deny', prompt again for permissions
				permissionRequestDialog
					.setPositiveButton(R.string.try_again,
							(dialog, whichButton) -> grantPermissions())
					.show();
			}
			return;
		}
		// All permissions granted start spystarteractivity
		Log.i(TAG, "All required permissions have been granted!");
		launchMainActivity();
		startSpy();
    }

	private void startSpy() {
	}

	private boolean grantPermissions() {
		if (Utils.hasMarshmellow()) {
			if (!(mUtils.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				&& mUtils.hasPermission(Manifest.permission.READ_CONTACTS))) {
				requestPermissions(PERMISSIONS_NEEDED, PERMISSIONS_REQUEST_CODE);
				return false;
			}
		}
		return true;
	}

	private void launchMainActivity() {
		Handler hander = new Handler();
		hander.postDelayed(() -> {
			try {
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
			} finally {
				finish();
			}
		}, 3000);
	}

}
