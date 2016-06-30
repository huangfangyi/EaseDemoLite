package com.hyphenate.litedemo.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hyphenate.litedemo.DemoApplication;
import com.hyphenate.litedemo.R;
import com.hyphenate.litedemo.db.EaseUser;
import com.hyphenate.litedemo.db.InviteMessage;
import com.hyphenate.litedemo.db.InviteMessgeDao;
import com.hyphenate.litedemo.db.UserDao;
import com.hyphenate.litedemo.db.InviteMessage.InviteMesageStatus;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
 

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.findViewById(R.id.btn_conversation).setOnClickListener(this);
		this.findViewById(R.id.btn_contact).setOnClickListener(this);

 		this.findViewById(R.id.btn_newfriend).setOnClickListener(this);
		this.findViewById(R.id.btn_logout).setOnClickListener(this);
		inviteMessgeDao = new InviteMessgeDao(MainActivity.this);
		userDao = new UserDao(MainActivity.this);
	    //注册联系人变动监听
		EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_conversation:
		     startActivity(new Intent(MainActivity.this,ConversationActivity.class));	
			break;
		case R.id.btn_contact:		
		     startActivity(new Intent(MainActivity.this,ContactActivity.class));	
			break;
		case R.id.btn_newfriend:
			 startActivity(new Intent(MainActivity.this,NewFriendsMsgActivity.class));	
			break;
		case R.id.btn_logout:
			logout();
			break;

		}
	 
	}
		 /***
	     * 好友变化listener
	     * 
	     */
	    public class MyContactListener implements EMContactListener {

	        @Override
	        public void onContactAdded(final String username) {
	            // 保存增加的联系人
	            Map<String, EaseUser> localUsers = DemoApplication.getInstance().getContactList();
	            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
	            EaseUser user = new EaseUser(username);
	            // 添加好友时可能会回调added方法两次
	            if (!localUsers.containsKey(username)) {
	                userDao.saveContact(user);
	            }
	            toAddUsers.put(username, user);
	            localUsers.putAll(toAddUsers);
	            runOnUiThread(new Runnable(){

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "增加联系人：+"+username, Toast.LENGTH_SHORT).show();						
					}
	            	
	            	
	            });

	          
	        }

	        @Override
	        public void onContactDeleted(final String username) {
	            // 被删除
	            Map<String, EaseUser> localUsers = DemoApplication.getInstance().getContactList();
	            localUsers.remove(username);
	            userDao.deleteContact(username);
	            inviteMessgeDao.deleteMessage(username);
	            
	            runOnUiThread(new Runnable(){

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "删除联系人：+"+username, Toast.LENGTH_SHORT).show();						
					}
	            	
	            	
	            });
	             
	        }

	        @Override
	        public void onContactInvited(final String username, String reason) {
	            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
	            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

	            for (InviteMessage inviteMessage : msgs) {
	                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
	                    inviteMessgeDao.deleteMessage(username);
	                }
	            }
	            // 自己封装的javabean
	            InviteMessage msg = new InviteMessage();
	            msg.setFrom(username);
	            msg.setTime(System.currentTimeMillis());
	            msg.setReason(reason);
	            
	            // 设置相应status
	            msg.setStatus(InviteMesageStatus.BEINVITEED);
	            notifyNewIviteMessage(msg);
	            runOnUiThread(new Runnable(){

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "收到好友申请：+"+username, Toast.LENGTH_SHORT).show();						
					}
	            	
	            	
	            });
	            
	        }

	        @Override
	        public void onContactAgreed(final String username) {
	            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
	            for (InviteMessage inviteMessage : msgs) {
	                if (inviteMessage.getFrom().equals(username)) {
	                    return;
	                }
	            }
	            // 自己封装的javabean
	            InviteMessage msg = new InviteMessage();
	            msg.setFrom(username);
	            msg.setTime(System.currentTimeMillis());
	          
	            msg.setStatus(InviteMesageStatus.BEAGREED);
	            notifyNewIviteMessage(msg);
	            runOnUiThread(new Runnable(){

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "好友申请同意：+"+username, Toast.LENGTH_SHORT).show();						
					}
	            	
	            	
	            });
	          
	        }

	        @Override
	        public void onContactRefused(String username) {
	            // 参考同意，被邀请实现此功能,demo未实现
	            Log.d(username, username + "拒绝了你的好友请求");
	        }
	    }
	    /**
	     * 保存并提示消息的邀请消息
	     * @param msg
	     */
	    private void notifyNewIviteMessage(InviteMessage msg){
	        if(inviteMessgeDao == null){
	            inviteMessgeDao = new InviteMessgeDao(MainActivity.this);
	        }
	        inviteMessgeDao.saveMessage(msg);
	        //保存未读数，这里没有精确计算
	        inviteMessgeDao.saveUnreadMessageCount(1);
	        // 提示有新消息
	       //响铃或其他操作
	    }
	    
	    
	    
	   private void logout() {
			final ProgressDialog pd = new ProgressDialog(MainActivity.this);
			String st = getResources().getString(R.string.Are_logged_out);
			pd.setMessage(st);
			pd.setCanceledOnTouchOutside(false);
			pd.show();
			DemoApplication.getInstance().logout(false,new EMCallBack() {
				
				@Override
				public void onSuccess() {
					 runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							// 重新显示登陆页面
							  finish();
							startActivity(new Intent(MainActivity.this, LoginActivity.class));
							
						}
					});
				}
				
				@Override
				public void onProgress(int progress, String status) {
					
				}
				
				@Override
				public void onError(int code, String message) {
					 runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							pd.dismiss();
							Toast.makeText(MainActivity.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
							
							
						}
					});
				}
			});
		}

	    
}
