package com.changhong.xiami.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.xiami.data.MusicsListAdapter;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.yinxiang.R;
import com.xiami.sdk.entities.LanguageType;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineSong;

public class XiamiMusicListActivity extends Activity {

	private Button back;
	private TextView albumName;
	private ListView musicsList;
	private MusicsListAdapter adapter;
	private List<OnlineSong> songsList;
	private OnlineAlbum album;
	
	private long albumID=0;
	
	private int albumIndex = 0;

	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:
				if (album != null) {
					String title = album.getAlbumName();
					songsList = album.getSongs();
					adapter.setData(songsList);
					albumName.setText(title);
				}
				break;
			case 2:
				
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();
		initData();
		
	}

	private void initView() {
		setContentView(R.layout.xiami_music_list);
		back = (Button) findViewById(R.id.btn_back);
		albumName = (TextView) findViewById(R.id.ablum_name);
		musicsList = (ListView) findViewById(R.id.musics_list);
		adapter = new MusicsListAdapter(XiamiMusicListActivity.this);
		musicsList.setAdapter(adapter);
	}

	private void initData() {

		// 启动activity的时候传进参数名为"musicsAlbum"的专辑。
		Intent intent = getIntent();
		albumID = intent.getIntExtra("albumID", 0);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		getAlbumList();
	}

	private void getAlbumList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				//测试代码
				ArrayList<OnlineAlbum> albumList = (ArrayList<OnlineAlbum>) XMMusicData.getInstance(
						XiamiMusicListActivity.this).getNewAlbums(
						LanguageType.huayu, 10, 1);
				album = albumList.get(albumIndex);
				albumID=album.getAlbumId();
				
				//根据ID获取专辑相信信息，带歌曲列表
				album = XMMusicData.getInstance(XiamiMusicListActivity.this)
						.getDetailAlbum(albumID);

				mhandler.sendEmptyMessage(1);

			}
		}).start();

		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
