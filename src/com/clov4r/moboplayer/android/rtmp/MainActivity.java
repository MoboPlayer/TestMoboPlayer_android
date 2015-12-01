package com.clov4r.moboplayer.android.rtmp;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
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
import com.clov4r.moboplayer.android.nil.codec.ScreenShotLibJni;
import com.clov4r.moboplayer.android.nil.codec.SubtitleJni;
import com.clov4r.moboplayer.android.nil.library.BufferListener;
import com.clov4r.moboplayer.android.nil.library.Constant;
import com.clov4r.moboplayer.android.nil.library.FloatPlayerListener;
import com.clov4r.moboplayer.android.nil.library.MoboBasePlayer;
import com.clov4r.moboplayer.android.rtmp.MoboVideoView.OnVideoStateChangedListener;

public class MainActivity extends MoboBasePlayer {
	RelativeLayout videoLayout = null;
	SeekBar player_seek_bar;
	TextView subtitle_view, player_time;
	Button player_prev, player_seek_backward, player_pause,
			player_seek_forward, player_next, player_screen_shot, player_scale,
			player_decode_mode, player_rorate, player_speed;
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
	float currentSpeed = 1f;
	/**
	 * 用来保存各个视频的播放进度
	 */
	HashMap<String, Integer> videoPregressMap = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {// rtmp://61.133.116.49/flv/mp4:n2014/jxjy/kc213/kj2276/fc/gdxxkjzd201401.mp4
														// share-04.MP4
		playList.add("/sdcard/Movies/原子弹.flv");
		playList.add("/sdcard/Movies/2015-10-10-11-24-12.MP4");
		playList.add("rtmp://61.133.116.49/flv/mp4:n2014/jxjy/kc213/kj2276/fc/gdxxkjzd201401.mp4");
		playList.add("rtmp://221.2.201.187/flv/mp4:n2014/ys/kc61/kj142/fc/xrfx01.mp4");
		playList.add("rtsp://192.168.42.1/live");//
		playList.add("/sdcard/Non44100&16/星屑の砂時計.flac");
		playList.add("http://183.62.232.213:8080/download/flv/97/2015-08-16-09-48-43.MP4");
		playList.add("rtmp://121.42.26.165/live/livestream");// NORMAL/media001/2015-01-13-03-33-10.MP4
		playList.add("rtsp://192.168.42.1/tmp/fuse_d/share/2015-01-13-02-26-30.MP4");// 2015-01-13-03-21-17.MP4
		playList.add("rtsp://192.168.42.1/live");//
		playList.add("/sdcard/Movies/2015-06-16-15-17-36.MP4");
		playList.add("/sdcard/Movies/2015-01-10-04-29-17.MP4");
		playList.add("/sdcard/movie/2015-08-02-09-34-08.MP4");
		playList.add("rtsp://192.168.42.1/live");//
		playList.add("rtsp://192.168.42.1/tmp/fuse_d/NORMAL/media001/2015-01-13-02-52-50.MP4");
		playList.add("rtsp://192.168.42.1/tmp/fuse_d/SHARE/2015-08-02-09-34-08.MP4");
		playList.add("/sdcard/Movies/all is well.rmvb");
		playList.add("http://1011.lssplay.aodianyun.com/demo/stock.m3u8");//
		playList.add("/sdcard/Movies/sample3.mp4");
		playList.add("/sdcard/Movies/sample.mp4");
		playList.add("/sdcard/Movies/sample2.mp4");
		playList.add("/sdcard/Movies/2015-01-10-04-29-17.MP4");
		super.onCreate(savedInstanceState);
		currentVideoPath = playList.get(0);
		setContentView(R.layout.activity_main);
		initViews();
		playIndexOf(0);
		// currentVideoIndex=-1;

		showFloatPlayerByHomeKey(false);//Home键打开悬浮窗口
		setFloatPlayerListener(mFloatPlayerListener);
		// mMoboVideoView.scanMediaFile(playList.get(0), null, 1, 343, 234, 1);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			stop();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPause() {
		super.onPause();

		mMoboVideoView.pause();
	}

	@Override
	public void onStop() {
		super.onStop();
		// 测试打开新界面播放
		Log.e("player", "onStop..");
		if (mMoboVideoView.getPlayState() != MoboVideoView.state_stop) {
			stop();
			videoLayout.removeView(mMoboVideoView);
		}
		// 测试打开新界面播放
	}

	@Override
	public void onRestart() {
		super.onRestart();
		Log.e("player", "onRestart..");
		playIndexOf(currentVideoIndex);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("player", "onActivityResult..");
		if (111 == requestCode) {
			initMoboVideoView();
			playIndexOf(0);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mMoboVideoView.getHolder().setFixedSize(
					mMoboVideoView.getVideoWidth(),
					mMoboVideoView.getVideoHeight());
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			mMoboVideoView.getHolder().setFixedSize(
					mMoboVideoView.getVideoWidth(),
					mMoboVideoView.getVideoHeight());
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
		player_speed = (Button) findViewById(R.id.player_speed);
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
		player_speed.setOnClickListener(mOnClickListener);
		player_loading.setVisibility(View.GONE);
		player_rorate.setVisibility(View.GONE);
		player_decode_mode.setVisibility(View.GONE);

		if (isShangeHaiYaoyuan) {
			player_seek_backward.setVisibility(View.GONE);
			player_seek_forward.setVisibility(View.GONE);
			player_screen_shot.setVisibility(View.GONE);
			player_scale.setVisibility(View.GONE);
			player_rorate.setVisibility(View.GONE);
		}
	}

	void initMoboVideoView() {
		mMoboVideoView = new MoboVideoView(this, null,
				MoboVideoView.decode_mode_soft);
		mMoboVideoView.loadNativeLibs();
		mMoboVideoView
				.setOnVideoStateChangedListener(mOnVideoStateChangedListener);
		mMoboVideoView.setBufferedTime(1);
		// mMoboVideoView.setPlayMultiplier(2f);
		// mMoboVideoView.setTimeout(15);
		videoLayout.removeAllViews();
		videoLayout.addView(mMoboVideoView);

	}

	public void onDestroy() {
		super.onDestroy();
		stop();
	}

	/** 播放回调接口 **/
	OnVideoStateChangedListener mOnVideoStateChangedListener = new OnVideoStateChangedListener() {
		/**
		 * 视频准备完成，可以进行播放、快进等操作了
		 */
		@Override
		public void afterChanged(String arg0) {
			// TODO Auto-generated method stub
			player_loading.setVisibility(View.GONE);
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
				Toast.makeText(MainActivity.this,
						String.format("%s播放失败", arg0), Toast.LENGTH_LONG)
						.show();
		}

		@Override
		public void onPlayFinished(String path) {
			// TODO Auto-generated method stub
			Log.e("MainActivity", "onPlayFinished");
			stop();
			// if (currentPosition >= duration - 1) {
			currentVideoIndex += 1;
			playIndexOf(currentVideoIndex);
			// }
		}
	};

	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (player_prev == v) {
				playIndexOf(currentVideoIndex - 1);
				// finish();
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
			} else if (player_speed == v) {
				currentSpeed += 0.2;
				if (currentSpeed > 2.1)
					currentSpeed = 0.5f;
				player_speed.setText(String.format("播放速度:%1.1f", currentSpeed));
				mMoboVideoView.setPlayMultiplier(currentSpeed);
			} else if (player_screen_shot == v) {// 截图
				final String currentPath = mMoboVideoView.getCurrentVideoPath();
				Bitmap bitmap = ScreenShotLibJni.getInstance()
						.getKeyFrameScreenShot_2(currentPath,
								"/sdcard/mobo_videoview_test.png",
								mMoboVideoView.getCurrentPosition() / 1000,
								200, 200);
				if (bitmap != null) {
					Toast.makeText(MainActivity.this, "截图成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this, "截图失败",
							Toast.LENGTH_SHORT).show();
				}
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
				int width = mMoboVideoView.getVideoWidth();
				int height = mMoboVideoView.getVideoHeight();
				int dirctWidth = height;
				int dirctHeight = dirctWidth * height / width;
				mMoboVideoView.setRotation(rotation);
				mMoboVideoView.getHolder()
						.setFixedSize(dirctWidth, dirctHeight);
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
		Log.e("MainActivity", "pause()");
		isPlaying = false;
		if (mMoboVideoView.getPlayState() != state_stop) {
			mMoboVideoView.pause();
			player_state = state_pause;
			player_pause.setBackgroundResource(R.drawable.player_pause);
			releaseScreenOnSetting();
		}
	}

	protected void stop() {
		Log.e("MainActivity", "stop()");
		cancelPlayerTimer();
		mMoboVideoView.pause();
		mMoboVideoView.stop();
		mMoboVideoView.stopBuffering();
		if (isOpenSubtitleFileSuccess)
			closeSubtitleFile();
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
			if (mMoboVideoView != null)
				stop();

			initMoboVideoView();
			if (playAudioOnly)
				playAudioOnly(currentVideoPath, 0);
			else {

				// mMoboVideoView.setIsLive(true);// 点播时可不设置
				mMoboVideoView.dropInvalidFrame(true);
				mMoboVideoView.setBufferedTime(7);// 设置缓冲时间
				mMoboVideoView.setSaveBufferInfoOrNot(false);
				mMoboVideoView.dropInvalidFrame(true);
				mMoboVideoView.setBufferListener(mBufferListener);// 设置缓冲回调接口

				videoParams = 0 + "\n" + 0;// 播放第0个音轨+第0个字幕
				setVideoPath(currentVideoPath, videoParams,
						MoboVideoView.decode_mode_soft);// decode_mode_hard
				openSubtitleFile(currentVideoPath, 0);// 打开字幕文件
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
			player_time.setText(currentPosition + "/" + duration);
			if (isOpenSubtitleFileSuccess) {
				int time = mMoboVideoView.getCurrentPosition();
				String subtitle = getSubtitle(time);
				if (subtitle != null)
					subtitle_view.setText(Html.fromHtml(subtitle));
			}

		}
	}

	protected boolean isOpenSubtitleFileSuccess;

	/**
	 * set isOpenSubtitleFileSuccess = true if subtitle is exits and open it
	 * else set isOpenSubtitleFileSuccess = false.
	 * 
	 * @param filePath
	 *            : Can be a video file or a subtitle file.
	 * @param index
	 *            : the index of subtitle stream.
	 */
	protected void openSubtitleFile(String filePath, int index) {
		File file = new File(filePath);
		if (!file.exists()) {
			isOpenSubtitleFileSuccess = false;
			Toast.makeText(this, "字幕文件不存在！", Toast.LENGTH_LONG).show();
			return;
		}
		int numOfSubtitle = SubtitleJni.getInstance().isSubtitleExits(filePath);
		if (numOfSubtitle > 0) {
			int flag = SubtitleJni.getInstance().openSubtitleFile_2(filePath,
					index);
			if (flag >= 0) {
				isOpenSubtitleFileSuccess = true;
				return;
			}
		}
		isOpenSubtitleFileSuccess = false;
	}

	protected String getSubtitle(int currentTime) {
		return SubtitleJni.getInstance().getSubtitleByTime_2(currentTime);
	}

	/**
	 * May run out of memory if you are not close the subtitle file .
	 */
	protected void closeSubtitleFile() {
		SubtitleJni.getInstance().closeSubtitle_2();
	}

}
