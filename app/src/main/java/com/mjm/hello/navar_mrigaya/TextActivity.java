package com.mjm.hello.navar_mrigaya;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;


public class TextActivity extends Activity {

	private TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text);

		Bundle b = getIntent().getExtras();
		String title = b.getString("title");
		setTitle(title);
		
		String value = b.getString("text");
		text = findViewById(R.id.text_activity_text);
		text.setMovementMethod(LinkMovementMethod.getInstance());
		text.setText(value);
	}
}
