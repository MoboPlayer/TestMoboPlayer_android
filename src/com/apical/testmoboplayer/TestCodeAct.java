package com.apical.testmoboplayer;

import com.clov4r.moboplayer.android.nil.library.MoboBasePlayer;
import com.clov4r.moboplayer.android.rtmp.MoboVideoView;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RelativeLayout;

public class TestCodeAct extends MoboBasePlayer {

	// Widget
	RelativeLayout videoLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_test_code);
		initViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.act_test_code, menu);
		return true;
	}

	void initViews() {
		videoLayout = (RelativeLayout) findViewById(R.id.video_layout);
		initMoboVideoView();
	}
	
	void initMoboVideoView() {
		mMoboVideoView = new MoboVideoView(this, null);
		mMoboVideoView.loadNativeLibs();
		mMoboVideoView.resetDecodeMode(MoboVideoView.decode_mode_soft);
		mMoboVideoView
				.setOnVideoStateChangedListener(mOnVideoStateChangedListener);
		videoLayout.addView(mMoboVideoView);
		mMoboVideoView.setIsLive(false);
		videoParams = 0 + "\n" + 0;// ���ŵ�0������+��0����Ļ
		mMoboVideoView.setVideoPath("/mnt/sdcard/AiproDown/wondergirls-nobody.MP4", videoParams);
		if (mMoboVideoView.getCurrentVideoPath() != null)
			mMoboVideoView.resetDecodeMode(MoboVideoView.decode_mode_soft);
	}
	
}
