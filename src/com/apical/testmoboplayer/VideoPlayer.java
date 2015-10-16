package com.apical.testmoboplayer;

import com.apical.testmoboplayer.TestMoboplayer.BtnClickListener;
import com.clov4r.moboplayer.android.rtmp.MoboVideoView;
import com.clov4r.moboplayer.android.rtmp.MoboVideoView.OnVideoStateChangedListener;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.RelativeLayout;

public class VideoPlayer extends Activity {

	RelativeLayout videoLayout = null;
	MoboVideoView mMoboVideoView = null;
	
	String path = "rtsp://192.168.42.1/live";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_video_player);
		initMember();
		videoLayout = (RelativeLayout) findViewById(R.id.video_flayout);
		mMoboVideoView = new MoboVideoView(this, null);
		mMoboVideoView.loadNativeLibs();
		videoLayout.addView(mMoboVideoView);
		mMoboVideoView
				.setOnVideoStateChangedListener(mOnVideoStateChangedListener);
		
		mMoboVideoView.setVideoPath(path);
	}

	void initMember()
	{
//		mBtnClickListener = new BtnClickListener();
	}
	
	public void onDestroy() {
		super.onDestroy();
		mMoboVideoView.stop();
		Log.d("Test", "14112911 - onDestroy");
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.act_video_player, menu);
		return true;
	}

	OnVideoStateChangedListener mOnVideoStateChangedListener = new OnVideoStateChangedListener() {

		@Override
		public void afterChanged(String arg0) {
			mMoboVideoView.start();
			Log.d("Test", "141029 - mMoboVideoView.getDecodeMode() = " + mMoboVideoView.getDecodeMode());
		}

		@Override
		public void beforeChange(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void playFailed(String arg0, int arg1) {
			Log.d("Test", "141029 - arg0 = " + arg0);
		}

		@Override
		public void onPlayFinished(String arg0) {
			// TODO Auto-generated method stub
			
		}
	};
}
