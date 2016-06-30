package com.hyphenate.litedemo.activity.adapter;

import java.util.List;

import com.hyphenate.litedemo.R;
import com.hyphenate.litedemo.db.InviteMessage;
import com.hyphenate.litedemo.db.InviteMessgeDao;
import com.hyphenate.litedemo.db.InviteMessage.InviteMesageStatus;
import com.hyphenate.chat.EMClient;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 本demo只提供好友的邀请通知，群相关的通知，已经拒绝申请的处理请参考官方demo
 *
 */
public class NewFriendsMsgAdapter extends ArrayAdapter<InviteMessage> {

	private Context context;
	private InviteMessgeDao messgeDao;

	public NewFriendsMsgAdapter(Context context, int textViewResourceId, List<InviteMessage> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		messgeDao = new InviteMessgeDao(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_invite_msg, null);
 			holder.tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.btn_agree = (Button) convertView.findViewById(R.id.btn_agree);
			 
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String str1 = context.getResources().getString(R.string.Has_agreed_to_your_friend_request);
		String str2 = context.getResources().getString(R.string.agree);
		
		String str3 = context.getResources().getString(R.string.Request_to_add_you_as_a_friend);
	 
		
		final InviteMessage msg = getItem(position);
		if (msg != null) {
		    
		    holder.btn_agree.setVisibility(View.INVISIBLE);
		    
		 
			
			holder.tv_reason.setText(msg.getReason());
			holder.tv_name.setText(msg.getFrom());
  			if (msg.getStatus() == InviteMesageStatus.BEAGREED) {
 				holder.tv_reason.setText(str1);
			} else if (msg.getStatus() == InviteMesageStatus.BEINVITEED || msg.getStatus() == InviteMesageStatus.BEAPPLYED ||
			        msg.getStatus() == InviteMesageStatus.GROUPINVITATION) {
			    holder.btn_agree.setVisibility(View.VISIBLE);
                holder.btn_agree.setEnabled(true);
                holder.btn_agree.setBackgroundResource(android.R.drawable.btn_default);
                holder.btn_agree.setText(str2);			 
					if (msg.getReason() == null) {
						// 如果没写理由
						holder.tv_reason.setText(str3);
					}				
				// 设置点击事件
                holder.btn_agree.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 同意别人发的好友请求
                        acceptInvitation(holder.btn_agree,   msg);
                    }
                });
				 
			} else if (msg.getStatus() == InviteMesageStatus.AGREED) {
				 
			} else if(msg.getStatus() == InviteMesageStatus.REFUSED){
				 
			} else if(msg.getStatus() == InviteMesageStatus.GROUPINVITATION_ACCEPTED){
			   
            } else if(msg.getStatus() == InviteMesageStatus.GROUPINVITATION_DECLINED){
                
            }

 		}

		return convertView;
	}

	/**
	 * 同意好友请求或者群申请
	 * 
	 * @param button
	 * @param username
	 */
	private void acceptInvitation(final Button buttonAgree , final InviteMessage msg) {
		final ProgressDialog pd = new ProgressDialog(context);
		String str1 = context.getResources().getString(R.string.Are_agree_with);
		final String str2 = context.getResources().getString(R.string.Has_agreed_to);
		final String str3 = context.getResources().getString(R.string.Agree_with_failure);
		pd.setMessage(str1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		new Thread(new Runnable() {
			public void run() {
				// 调用sdk的同意方法
				try {
					if (msg.getStatus() == InviteMesageStatus.BEINVITEED) {//同意好友请求
						EMClient.getInstance().contactManager().acceptInvitation(msg.getFrom());
					} else if (msg.getStatus() == InviteMesageStatus.BEAPPLYED) { //同意加群申请
						EMClient.getInstance().groupManager().acceptApplication(msg.getFrom(), msg.getGroupId());
					} else if (msg.getStatus() == InviteMesageStatus.GROUPINVITATION) {
					    EMClient.getInstance().groupManager().acceptInvitation(msg.getGroupId(), msg.getGroupInviter());
					}
                    msg.setStatus(InviteMesageStatus.AGREED);
                    // 更新db
                    ContentValues values = new ContentValues();
                    values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                    messgeDao.updateMessage(msg.getId(), values);
					((Activity) context).runOnUiThread(new Runnable() {

						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							pd.dismiss();
							buttonAgree.setText(str2);
							buttonAgree.setBackgroundDrawable(null);
							buttonAgree.setEnabled(false);
							
							 
						}
					});
				} catch (final Exception e) {
					((Activity) context).runOnUiThread(new Runnable() {

						@SuppressLint("ShowToast")
						@Override
						public void run() {
							pd.dismiss();
							Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});

				}
			}
		}).start();
	}
	
	 
	private static class ViewHolder {
 		TextView tv_name;
		TextView tv_reason;
        Button btn_agree;
		 
 	}

}
