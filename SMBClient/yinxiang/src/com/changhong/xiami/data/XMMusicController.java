package com.changhong.xiami.data;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.changhong.xiami.activity.XiamiMusicListActivity;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.JsonElement;
import com.xiami.sdk.entities.OnlineSong;

public class XMMusicController {
	private List<OnlineSong> playMusicList=null;
	private Context context;
	private XMMusicData mXMData;
	private static XMMusicController mXMController=null;
	
	public XMMusicController(Context con){
		this.context=con;
		if(null==mXMData){
			mXMData=XMMusicData.getInstance(context);
		}
		
	}
	
	public static XMMusicController getInstance(Context context){
		if(null==mXMController){
			mXMController=new XMMusicController(context);
		}
		return mXMController;
	}
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			JsonElement element=null;
			element=(JsonElement) msg.obj;
			switch (msg.what) {

			case Configure.XIAMI_RANK_DETAIL:
				playMusicList = mXMData.getRankSongList(element);
				sendRankMusics(playMusicList);
				break;
			case Configure.XIAMI_ALBUM_DETAIL:
				playMusicList=mXMData.getTheAlbumSongs(element);
				sendRankMusics(playMusicList);
				break;
			}
		}
		
	};
	
	/*
	 * 直接播放排行榜音乐
	 */
	public void playRankMusic(final String type){
		if(null==type) return;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mXMData.getTheRank(handler,type);
			}
		}).start();
		
	}
	
	
	/*
	 * 播放指定专辑ID的音乐
	 */
	 public void playTheAlbum(final long id){
		 if(0==id) return;
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mXMData.getTheAlbum(handler, id);
			}
		}).start();
	 }
	 
	/*
	 * 播放传进来的音乐
	 */
	public void sendRankMusics(final List<OnlineSong> list){
		XMPlayMusics.getInstance(context).playMusics(list);
		
	}

}
