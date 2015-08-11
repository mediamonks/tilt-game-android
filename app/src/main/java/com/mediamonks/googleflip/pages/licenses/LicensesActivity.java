package com.mediamonks.googleflip.pages.licenses;

import android.os.Bundle;
import android.widget.ImageView;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.ui.RegisteredFragmentActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import temple.core.ui.CustomTextView;

/**
 * Activity for showing game licenses
 */
public class LicensesActivity extends RegisteredFragmentActivity {
	private static String TAG = LicensesActivity.class.getSimpleName();

	@InjectView(R.id.text)
	protected CustomTextView _text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_licenses);
		ButterKnife.inject(this);

		try {
			InputStream is = getAssets().open("OFL.txt");
			_text.setText(getStringFromInputStream(is));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnClick(R.id.close_button)
	protected void onCloseButtonClick() {
		onBackPressed();
	}

	public static String getStringFromInputStream(InputStream stream) throws IOException
	{
		int n = 0;
		char[] buffer = new char[1024 * 4];
		InputStreamReader reader = new InputStreamReader(stream);
		StringWriter writer = new StringWriter();
		while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
		return writer.toString();
	}
}
