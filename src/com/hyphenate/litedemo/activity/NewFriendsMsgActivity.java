package com.hyphenate.litedemo.activity;

import java.util.List;

import com.hyphenate.litedemo.R;
import com.hyphenate.litedemo.activity.adapter.NewFriendsMsgAdapter;
import com.hyphenate.litedemo.db.InviteMessage;
import com.hyphenate.litedemo.db.InviteMessgeDao;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

 

/**
 * 申请与通知
 *
 */
public class NewFriendsMsgActivity extends Activity {
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friends_msg);

		listView = (ListView) findViewById(R.id.list);
		InviteMessgeDao dao = new InviteMessgeDao(this);
		List<InviteMessage> msgs = dao.getMessagesList();
 		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs); 
		listView.setAdapter(adapter);
		dao.saveUnreadMessageCount(0);
		
	}

	public void back(View view) {
		finish();
	}
	
	
}
