package com.tangledbytes.statussaver.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import com.tangledbytes.statussaver.R;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		TextView tvAppAboutFooter = findViewById(R.id.tv_app_desc_footer);
		Button btnOk = findViewById(R.id.btn_ok);
		tvAppAboutFooter.setMovementMethod(LinkMovementMethod.getInstance());
		btnOk.setOnClickListener(view -> finish());
	}
}
