package com.changhong.xiami.activity;

/*
 * 显示对应专辑ID的歌曲列表
 * 传入参数的名字为 albumID
 * BY CYM
 */
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
import com.baidu.android.common.logging.Log;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.xiami.data.MusicsListAdapter;
import com.changhong.xiami.data.SceneInfor;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.xiami.sdk.entities.LanguageType;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineSong;

public class XiamiMusicListActivity extends BaseActivity {

	private TextView albumName;
	private ListView musicsList;
	private MusicsListAdapter adapter;
	private List<OnlineSong> songsList;
	private OnlineAlbum album;
	private long albumID = 0;
	private int albumIndex = 0;

	// 根据不同的音乐类型，进入不同的操作流程
	private final int MUSIC_TYPE_ALBUM = 1;
	// 显示场景歌曲
	private final int MUSIC_TYPE_SCENE = 2;
	// 显示专辑歌曲列表
	private final int MUSIC_ALBUM_UPDATE = 3;
	// 显示今日歌曲列表
	private final int MUSIC_TODAY_MUSICS = 4;

	private int curMusicType = 1;

	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MUSIC_ALBUM_UPDATE: // 专辑音乐
				if (album != null) {
					String title = album.getAlbumName();
					songsList = album.getSongs();
					adapter.setData(songsList);
					albumName.setText(title);
				}
				break;
			case MUSIC_TYPE_SCENE: // 场景音乐
				SceneInfor curScene = (SceneInfor) getIntent()
						.getSerializableExtra("sceneInfor");
				if (null != curScene) {
					String title = curScene.getSceneName();
					songsList = curScene.getSongsList();
					adapter.setData(songsList);
					albumName.setText(title);
				}
				break;
			case MUSIC_TODAY_MUSICS:

				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	protected void initView() {
		setContentView(R.layout.xiami_music_list);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		albumName = (TextView) findViewById(R.id.ablum_name);
		musicsList = (ListView) findViewById(R.id.musics_list);
		adapter = new MusicsListAdapter(XiamiMusicListActivity.this);
		musicsList.setAdapter(adapter);
	}

	protected void initData() {
		super.initData();
		// 启动activity的时候传进参数名为"musicType"的专辑。
		curMusicType = getIntent().getIntExtra("musicType", 1);
		dealMusicType(curMusicType);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void dealMusicType(int type) {
		if (1 == type) {
			albumID = getIntent().getIntExtra("albumID", 0);
			getAlbumList();
		} else {
			mhandler.sendEmptyMessage(curMusicType);
		}
	}

	private void getAlbumList() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				// 测试代码
				ArrayList<OnlineAlbum> albumList = (ArrayList<OnlineAlbum>) XMMusicData
						.getInstance(XiamiMusicListActivity.this).getNewAlbums(
								LanguageType.huayu, 10, 1);
				album = albumList.get(albumIndex);
				albumID = album.getAlbumId();
				// 根据ID获取专辑相信信息，带歌曲列表
				album = XMMusicData.getInstance(XiamiMusicListActivity.this)
						.getDetailAlbum(albumID);

				mhandler.sendEmptyMessage(MUSIC_ALBUM_UPDATE);

			}
		}).start();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
