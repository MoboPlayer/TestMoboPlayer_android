package com.clov4r.moboplayer.android.rtmp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.apical.testmoboplayer.R;
import com.clov4r.moboplayer.android.nil.library.BufferListener;
import com.clov4r.moboplayer.android.nil.library.Constant;
import com.clov4r.moboplayer.android.nil.library.FloatPlayerListener;
import com.clov4r.moboplayer.android.nil.library.Global;
import com.clov4r.moboplayer.android.nil.library.MoboBasePlayer;
import com.clov4r.moboplayer.android.nil.library.ScreenShotLib;
import com.clov4r.moboplayer.android.nil.library.ScreenShotLib.ScreenShotListener;
import com.clov4r.moboplayer.android.rtmp.MoboVideoView.OnVideoStateChangedListener;

public class MainActivity_2 extends MoboBasePlayer {
	RelativeLayout videoLayout = null;
	SeekBar player_seek_bar;
	TextView subtitle_view, player_time;
	Button player_prev, player_seek_backward, player_pause,
			player_seek_forward, player_next, player_screen_shot, player_scale,
			player_decode_mode, player_rorate;
	ProgressBar player_loading;
	boolean isPlaying = false;

	// MoboVideoView mMoboVideoView = null;
	// ArrayList<String> playList = new ArrayList<String>();
	String path = "rtmp://183.62.232.213/fileList/test.flv";// rtmp://183.62.232.213/fileList/video4.flv---rtmp://183.62.232.213/fileList/test22--rtmp://183.62.232.213/fileList/test.flv
	Timer mTimer = null;
	int seekInterval = 20;
	/** 只播放音频 **/
	boolean playAudioOnly = false;
	boolean isShangeHaiYaoyuan = false;
	int rotation = 0;
	/**
	 * 用来保存各个视频的播放进度
	 */
	HashMap<String, Integer> videoPregressMap = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {// rtmp://61.133.116.49/flv/mp4:n2014/jxjy/kc213/kj2276/fc/gdxxkjzd201401.mp4
														// share-04.MP4
		playList.add("rtsp://192.168.42.1/tmp/fuse_d/share/2015-01-13-02-26-30.MP4");///NORMAL/media001/2015-01-13-03-33-10.MP4
		playList.add("rtsp://192.168.42.1/tmp/fuse_d/share/2015-01-13-02-18-53.MP4");
		 playList.add("rtsp://192.168.42.1/live");//
		 playList.add("rtsp://192.168.42.1/live");//
		 playList.add("rtsp://192.168.42.1/live");//
		 playList.add("rtsp://192.168.42.1/live");//
		playList.add("rtsp://192.168.42.1/tmp/fuse_d/share/2015-01-13-03-31-36.MP4");//2015-01-13-03-21-17.MP4
		 playList.add("http://1011.lssplay.aodianyun.com/demo/stock.m3u8");//
		playList.add("/sdcard/Movies/sample3.mp4");
		playList.add("/sdcard/Movies/sample.mp4");
		playList.add("/sdcard/Movies/sample2.mp4");
		// playList.add("/sdcard/Music/当爱已成往事.mp3");
		// playList.add("rtmp://61.133.116.49/flv/mp4:n2014/jxjy/kc218/kj2294/fc/kxsydwkjzd201401.mp4");
		// playList.add("/sdcard/movie/情歌.mp4");
		// playList.add("rtmp://61.133.116.49/flv/mp4:n2014/jxjy/kc213/kj2276/fc/gdxxkjzd201401.mp4");
		// playList.add("rtsp://183.58.12.204/PLTV/88888905/224/3221227038/10000100000000060000000000657995_0.smil");
		// playList.add("/sdcard/Movies/liudehua.avi");
		// playList.add("/sdcard/Movies/Frozen.2013.3D.BluRay.HSBS.1080p.DTS.x264-CHD3D.mkv");
		// playList.add("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov");
		// playList.add("http://27.221.44.43/65722A7056C3E83624182D4CB5/0300010E0054C96DBA3EF603BAF2B16135A553-86F1-7270-8753-BBB5274B597B.flv");
		// playList.add("http://localhost:8098/mobo.mkv");// 192.168.31.245
		// playList.add("/sdcard/mobo_download.mkv");
		// playList.add("/sdcard/Movies/[奥黛丽·赫本系列01：罗马假日].Roman.Holiday.1953.DVDRiP.X264.2Audio.AAC.HALFCD-NORM.Christian.mkv");
		// playList.add("rtmp://183.62.232.213/fileList/test.flv");
		// playList.add("http://flv1.vodfile.m1905.com/movie/leJ8Al00HnqXH-L2.flv");
		// playList.add(path);
		// playList.add("/sdcard/Movies/07141635_1278.MP4");
		// playList.add("");
		// playList.add("rtmp://183.62.232.213/fileList/test22");
		// playList.add("/storage/emulated/0/测试视频/[奥黛丽·赫本系列01：罗马假日].Roman.Holiday.1953.DVDRiP.X264.2Audio.AAC.HALFCD-NORM.Christian.mkv");
		// playList.add("/storage/emulated/0/测试视频/[YYDM-11FANS][CLANNAD][BDrip][24][X264_AAC][1280X720].mkv");
		// playList.add("/storage/emulated/0/测试视频/偶阵雨.mp3");
		// playList.add("/storage/emulated/0/测试视频/(4K HD)2014预告片 .mp4");
		// playList.add("/sdcard/Movies/[奥黛丽·赫本系列01：罗马假日].Roman.Holiday.1953.DVDRiP.X264.2Audio.AAC.HALFCD-NORM.Christian.mkv");
		super.onCreate(savedInstanceState);
		currentVideoPath = playList.get(0);
		setContentView(R.layout.activity_main);
		initViews();
		playIndexOf(0);
		// currentVideoIndex=-1;

		showFloatPlayerByHomeKey(true);
		setFloatPlayerListener(mFloatPlayerListener);
		// mMoboVideoView.scanMediaFile(playList.get(0), null, 1, 343, 234, 1);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.e("", "KEYCODE_BACK");
			stop();
			setResult(RESULT_OK);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// mMoboVideoView.getHolder().setFixedSize(
			// mMoboVideoView.getVideoWidth(),
			// mMoboVideoView.getVideoHeight());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					2048, 1536);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			mMoboVideoView.setLayoutParams(params);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// mMoboVideoView.getHolder().setFixedSize(
			// mMoboVideoView.getVideoWidth(),
			// mMoboVideoView.getVideoHeight());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					1536, 1152);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			mMoboVideoView.setLayoutParams(params);
		}
		// displayMode--;
		// changePlayerScale(displayMode);
	}

	void initViews() {
		videoLayout = (RelativeLayout) findViewById(R.id.video_layout);
		player_seek_bar = (SeekBar) findViewById(R.id.player_seek_bar);
		subtitle_view = (TextView) findViewById(R.id.subtitle_view);
		player_time = (TextView) findViewById(R.id.player_time);
		player_prev = (Button) findViewById(R.id.player_prev);
		player_seek_backward = (Button) findViewById(R.id.player_seek_backward);
		player_pause = (Button) findViewById(R.id.player_pause);
		player_seek_forward = (Button) findViewById(R.id.player_seek_forward);
		player_next = (Button) findViewById(R.id.player_next);
		player_screen_shot = (Button) findViewById(R.id.player_screen_shot);
		player_scale = (Button) findViewById(R.id.player_scale);
		player_decode_mode = (Button) findViewById(R.id.player_decode_mode);
		player_rorate = (Button) findViewById(R.id.player_rorate);
		player_loading = (ProgressBar) findViewById(R.id.player_loading);
		player_prev.setOnClickListener(mOnClickListener);
		player_seek_backward.setOnClickListener(mOnClickListener);
		player_pause.setOnClickListener(mOnClickListener);
		player_seek_forward.setOnClickListener(mOnClickListener);
		player_next.setOnClickListener(mOnClickListener);
		player_screen_shot.setOnClickListener(mOnClickListener);
		player_decode_mode.setOnClickListener(mOnClickListener);
		player_seek_bar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		player_scale.setOnClickListener(mOnClickListener);
		player_rorate.setOnClickListener(mOnClickListener);
		player_loading.setVisibility(View.GONE);
		player_rorate.setVisibility(View.GONE);
		player_decode_mode.setVisibility(View.GONE);
//		initMoboVideoView();

		if (isShangeHaiYaoyuan) {
			player_seek_backward.setVisibility(View.GONE);
			player_seek_forward.setVisibility(View.GONE);
			player_screen_shot.setVisibility(View.GONE);
			player_scale.setVisibility(View.GONE);
			player_rorate.setVisibility(View.GONE);
		}
	}

	void initMoboVideoView() {
		mMoboVideoView = new MoboVideoView(this, null);
		mMoboVideoView.loadNativeLibs();
		mMoboVideoView
				.setOnVideoStateChangedListener(mOnVideoStateChangedListener);
		mMoboVideoView.setBufferedTime(1);
//		mMoboVideoView.setTimeout(15);
		videoLayout.addView(mMoboVideoView);
	}

	public void onDestroy() {
		super.onDestroy();

	}

	/** 播放回调接口 **/
	OnVideoStateChangedListener mOnVideoStateChangedListener = new OnVideoStateChangedListener() {
		/**
		 * 视频准备完成，可以进行播放、快进等操作了
		 */
		@Override
		public void afterChanged(String arg0) {
			// TODO Auto-generated method stub
			if (mMoboVideoView.getDecodeMode() == MoboVideoView.decode_mode_soft) {
				player_decode_mode.setText("当前为软解");
			} else
				player_decode_mode.setText("当前为硬解");
			changePlayerScale(PLAY_DISPLAY_MODE_FULL_SCREEN);
			start();
			// mMoboVideoView.seekTo(currentPosition);
			duration = mMoboVideoView.getDuration() / 1000;
			player_seek_bar.setMax(duration);
			// getSubInfo(currentVideoPath);//获取视频的字幕信息
			// enableExtSubtitle("/sdcard/Movies/[YYDM-11FANS][CLANNAD][BDrip][24][X264_AAC][1280X720].ass");//打开外置字幕

			startPlayerTimer();
		}

		@Override
		public void beforeChange(String arg0) {
			// TODO Auto-generated method stub

		}

		/**
		 * 播放失败
		 * 
		 * @param arg0
		 *            播放失败的视频地址
		 * @param arg1
		 *            解码方式，若为硬解，内部已经自动转为软解，只需更新界面（如有界面显示当前解码方式的话）；
		 *            若为软解失败，则根据需求播放下一集或关闭播放界面
		 */
		@Override
		public void playFailed(String arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg1 == Constant.decode_mode_soft
					|| arg1 == Constant.decode_mode_mediacodec)
				Toast.makeText(MainActivity_2.this,
						String.format("%s播放失败", arg0), Toast.LENGTH_LONG)
						.show();
		}

		@Override
		public void onPlayFinished(String path) {
			// TODO Auto-generated method stub
			stop();
			// if (currentPosition >= duration - 1) {
			currentVideoIndex += 1;
			playIndexOf(currentVideoIndex);
			// }
			Log.e("MainActivity", "onPlayFinished");
		}
	};

	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (player_prev == v) {
				playIndexOf(currentVideoIndex - 1);
			} else if (player_seek_backward == v) {
				int currentTime = mMoboVideoView.getCurrentPosition() / 1000;
				seekTo(currentTime - seekInterval);
			} else if (player_pause == v) {
				if (player_state == state_play) {
					pause();
				} else {
					start();
				}
			} else if (player_seek_forward == v) {
				int currentTime = mMoboVideoView.getCurrentPosition() / 1000;
				seekTo(currentTime + seekInterval);
			} else if (player_next == v) {
				playIndexOf(currentVideoIndex + 1);
			} else if (player_screen_shot == v) {// 截图
				File file = new File("/sdcard/mobo_video_view/");
				file.mkdirs();
				// 异步方式获取截图
				ScreenShotLib mScreenShotLib = new ScreenShotLib(
						MainActivity_2.this, currentVideoPath,
						"/sdcard/mobo_video_view/"
								+ Global.getNameOf(currentVideoPath) + "_"
								+ 500 * 1000 + "_a.png", 500 * 1000, 800, 480);
				mScreenShotLib.screenShotAsynchronous(mScreenShotListener);
				// 异步方式获取截图

				// 同步方式获取截图
				mScreenShotLib = new ScreenShotLib(MainActivity_2.this,
						currentVideoPath, "/sdcard/mobo_video_view/"
								+ Global.getNameOf(currentVideoPath) + "_"
								+ 600 * 1000 + "_s.png", 600 * 1000, 480, 320);
				mScreenShotLib.screenShotSynchronous();
				// 同步方式获取截图
			} else if (player_scale == v) {
				changePlayerScale(displayMode);
			} else if (player_decode_mode == v) {
				if (mMoboVideoView.getDecodeMode() == MoboVideoView.decode_mode_soft) {
					mMoboVideoView
							.resetDecodeMode(MoboVideoView.decode_mode_hard);
				} else {
					mMoboVideoView
							.resetDecodeMode(MoboVideoView.decode_mode_soft);
				}
			} else if (player_rorate == v) {
				rotation += 90;
				if (rotation >= 360)
					rotation = 0;
				int width=mMoboVideoView.getVideoWidth();
				int height=mMoboVideoView.getVideoHeight();
				int dirctWidth=height;
				int dirctHeight=dirctWidth*height/width;
				mMoboVideoView.setRotation(rotation);
				mMoboVideoView.getHolder().setFixedSize(dirctWidth, dirctHeight);
			}
		}
	};

	OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			seekTo(seekBar.getProgress());
		}
	};

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case msg_refresh_progress:
				refreshProgress();
				break;
			case msg_get_subtitle_info:
				if (msg.obj != null) {
					/** 字幕列表 **/
					ArrayList<String> subList = getSubtitleList(msg.obj
							.toString());
					mMoboVideoView.closeSAOf(msg.arg1);// 释放获取字幕信息的相关内存
					if (subList != null && subList.size() > 0) {
						enableInnerSubtitle(0);
					}
				}
				break;
			}
		}
	};

	FloatPlayerListener mFloatPlayerListener = new FloatPlayerListener() {

		@Override
		public void onFloatPlayerPop() {
			// TODO Auto-generated method stub
			videoLayout.removeView(mMoboVideoView);
		}

		@Override
		public void onFloatPlayerDismiss() {
			// TODO Auto-generated method stub
			videoPregressMap.put(currentVideoPath, currentPosition);// 保存小窗口播放的进度
			initMoboVideoView();
			playIndexOf(currentVideoIndex);
		}
	};

	/** 截图完毕回调的接口 **/
	ScreenShotListener mScreenShotListener = new ScreenShotListener() {
		@Override
		public void onFinished(String videoPath, String imageSavePath,
				Bitmap bitmap) {
			// TODO Auto-generated method stub
			Toast.makeText(MainActivity_2.this, "截图完成", Toast.LENGTH_LONG).show();
		}
	};

	BufferListener mBufferListener = new BufferListener() {
		@Override
		public void onBufferStart() {
			// TODO Auto-generated method stub
			player_loading.setVisibility(View.VISIBLE);
			pause();
		}

		@Override
		public void onBufferEnd() {
			// TODO Auto-generated method stub
			if (!isPlaying) {
				player_loading.setVisibility(View.GONE);
				start();
			}
		}

		@Override
		public void onBufferFailed(String msg) {
			// TODO Auto-generated method stub
			Log.e("MainActivity", msg);
		}

		@Override
		public void onBufferProgressChanged(int current, int duration) {
			// TODO Auto-generated method stub
			if (player_seek_bar.getMax() < current)
				player_seek_bar.setMax(duration);
			player_seek_bar.setSecondaryProgress(current);
		}
	};

	protected void start() {
		isPlaying = true;
		mMoboVideoView.start();
		player_state = state_play;
		player_pause.setBackgroundResource(R.drawable.player_play);
		enableScreenOnSetting();
	}

	protected void pause() {
		isPlaying = false;
		if (mMoboVideoView.getPlayState() != state_stop) {
			mMoboVideoView.pause();
			player_state = state_pause;
			player_pause.setBackgroundResource(R.drawable.player_pause);
			releaseScreenOnSetting();
		}
	}

	protected void stop() {
		cancelPlayerTimer();
		mMoboVideoView.pause();
		mMoboVideoView.stop();
		mMoboVideoView.stopBuffering();
	}

	protected void seekTo(int time) {
		if (duration > 0 && time >= duration - 1) {
			playIndexOf(currentVideoIndex + 1);
		} else
			mMoboVideoView.seekTo(time);
	}

	protected void playIndexOf(int index) {
		super.playIndexOf(index);
		if (currentVideoIndex >= playList.size())
			finish();
		else if (currentVideoIndex < 0) {
			Toast.makeText(this, "已经是第一个视频了", Toast.LENGTH_LONG).show();
		} else {
			cancelPlayerTimer();
			currentVideoIndex = index;
			currentVideoPath = playList.get(index);
			// if (videoPregressMap.containsKey(currentVideoPath))
			// currentPosition = videoPregressMap.get(currentVideoPath);
			// else {
			// currentPosition = 0;
			// videoPregressMap.put(currentVideoPath, 0);
			// }
			if (mMoboVideoView != null)
				stop();

			initMoboVideoView();
			if (playAudioOnly)
				playAudioOnly(currentVideoPath, 0);
			else {
				mMoboVideoView.setIsLive(true);// 点播时可不设置
				mMoboVideoView.setBufferedTime(1);// 设置缓冲时间
				mMoboVideoView.setSaveBufferInfoOrNot(false);
				mMoboVideoView.setBufferListener(mBufferListener);// 设置缓冲回调接口

				videoParams = 0 + "\n" + 0;// 播放第0个音轨+第0个字幕
				setVideoPath(currentVideoPath, videoParams,
						MoboVideoView.decode_mode_soft);// decode_mode_hard
			}
		}
	}

	protected void changePlayerScale(int displayMode) {
		setPlayerScale(displayMode);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				playerWidth, playerHeight);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		Log.e("MoboVideoView", playerWidth + "X" + playerHeight);
		mMoboVideoView.setLayoutParams(params);
	}

	protected void startPlayerTimer() {
		cancelPlayerTimer();
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(msg_refresh_progress);
			}
		}, 1000, 1000);
	}

	protected void cancelPlayerTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	void refreshProgress() {
		if (mMoboVideoView.isPlaying()) {
			currentPosition = mMoboVideoView.getCurrentPosition() / 1000;
			int decodeMode = mMoboVideoView.getDecodeMode();
			int bufferedTime = mMoboVideoView.getBufferedTime();
			player_seek_bar.setSecondaryProgress(bufferedTime);
			player_seek_bar.setProgress(currentPosition);
			// if (currentPosition >= duration - 1) {//放到onPlayFinished()方法里边
			// playIndexOf(currentVideoIndex + 1);
			// }
			String subtitle = getTextSubtitle(currentPosition);
			if (subtitle != null)
				subtitle_view.setText(Html.fromHtml(subtitle));//

			player_time.setText(currentPosition + "/" + duration);
		}
	}

	/**
	 * 获取字幕信息
	 * 
	 * @param subPath
	 */
	void getSubInfo(String subPath) {
		mMoboVideoView.setBaseHandler(mHandler);// 获取字幕信息成功后会回调mHandler
		setSubtitle(currentVideoPath, 0);
	}

	/**
	 * 打开内置的第index个字幕
	 * 
	 * @param index
	 */
	void enableInnerSubtitle(int index) {
		mMoboVideoView.setBaseHandler(null);
		setSubtitle(currentVideoPath, index);
	}

	/**
	 * 打开外置字幕
	 */
	void enableExtSubtitle(String subPath) {
		setSubtitle(subPath, 0);
	}
}
