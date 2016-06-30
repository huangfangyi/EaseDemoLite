package com.hyphenate.litedemo.activity;

import com.hyphenate.litedemo.R;
import com.hyphenate.chat.EMClient;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddContactActivity extends Activity {

 
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		final EditText et_username = (EditText) this.findViewById(R.id.et_username);
		Button btn_add = (Button) this.findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = et_username.getText().toString().trim();

				if (TextUtils.isEmpty(username)) {

					Toast.makeText(getApplicationContext(), "请输入内容...", Toast.LENGTH_SHORT).show();
					return;

				}
				addContact(username);
			}

		});

	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	public void addContact(final String username) {
		progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {

				try {
					// demo写死了个reason，实际应该让用户手动填入
					String s = getResources().getString(R.string.Add_a_friend);
					EMClient.getInstance().contactManager().addContact(username, s);					 
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(R.string.send_successful);
							Toast.makeText(getApplicationContext(), s1, 1).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(getApplicationContext(), s2 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	public void back(View v) {
		finish();
	}
}
