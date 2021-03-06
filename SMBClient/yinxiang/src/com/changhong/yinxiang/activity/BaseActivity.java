package com.changhong.yinxiang.activity;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.NetworkConnectChangedReceiver;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.xiami.data.XMMusicController;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.yinxiang.service.ClientGetCommandService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {

	/*
	 * wifi监听
	 */
	private IntentFilter wifiFilter = null;
	private NetworkConnectChangedReceiver networkConnectChange = null;
	private NetworkIpListChangedReceiver ipListChange = null;

	/************************************************** IP连接部分 *******************************************************/

	public static TextView title = null;
	protected Button listClients;
	protected ImageView back;
	protected ListView clients = null;
	protected BoxSelectAdapter IpAdapter;

	// json数据解析
	protected XMMusicData mXMMusicData;
	protected XMMusicController mXMController;
	protected int mScreemWidth, mScreemHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();
		initData();
	}

	protected void initData() {

		mXMMusicData = XMMusicData.getInstance(this);
		mXMController = XMMusicController.getInstance(this);

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		mScreemWidth = d.getWidth();
		mScreemHeight = d.getHeight();

		/**
		 * IP连接部分
		 */
		IpAdapter = new BoxSelectAdapter(BaseActivity.this,
				ClientSendCommandService.serverIpList);
		clients.setAdapter(IpAdapter);
		clients.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				clients.setVisibility(View.GONE);
				return false;
			}
		});
		clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 < ClientSendCommandService.serverIpList.size()) {

					ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList
							.get(arg2);
					ClientSendCommandService.titletxt = ClientSendCommandService
							.getCurrentConnectBoxName();
					title.setText(ClientSendCommandService
							.getCurrentConnectBoxName());
					ClientSendCommandService.handler.sendEmptyMessage(2);
					clients.setVisibility(View.GONE);
				}
			}
		});
		listClients.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					MyApplication.vibrator.vibrate(100);
					if (ClientSendCommandService.serverIpList.isEmpty()) {
						Toast.makeText(BaseActivity.this, "未获取到服务器IP",
								Toast.LENGTH_LONG).show();
					} else {
						clients.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				finish();
			}
		});
	}

	private void regBroadcastRec() {
		wifiFilter = new IntentFilter();
		if (null == networkConnectChange) {
			networkConnectChange = new NetworkConnectChangedReceiver();
		}
		wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		wifiFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(networkConnectChange, wifiFilter);

		// 注册ipListChange监听
		if (null == ipListChange) {
			ipListChange = new NetworkIpListChangedReceiver();
		}
		IntentFilter ipListFilter = new IntentFilter();
		ipListFilter.addAction(ClientGetCommandService.NETWORK_IP_LIST_CHANGED);
		registerReceiver(ipListChange, ipListFilter);

	}

	private void unRegisterBroad() {
		if (networkConnectChange != null) {
			unregisterReceiver(networkConnectChange);
		}
		if (null != ipListChange) {
			unregisterReceiver(ipListChange);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ClientSendCommandService.titletxt != null) {
			title.setText(ClientSendCommandService.titletxt);
		}
		regBroadcastRec();
	}

	@Override
	protected void onPause() {
		unRegisterBroad();
		super.onPause();
	}

	protected abstract void initView();

	/**
	 * 接收器 获取IPlist变化
	 * 
	 * @author di
	 * 
	 */
	public class NetworkIpListChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ClientGetCommandService.NETWORK_IP_LIST_CHANGED == intent
					.getAction()) {
				IpAdapter.setData(ClientSendCommandService.serverIpList);
			}
		}
	}

}
