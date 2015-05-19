package com.apical.testmoboplayer;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.clov4r.moboplayer.android.nil.codec.ScreenShotLibJni;
import com.clov4r.moboplayer.android.nil.codec.ScreenShotLibJni.OnBitmapCreatedListener;
import com.clov4r.moboplayer.android.nil.codec.SubtitleJni;
import com.clov4r.moboplayer.android.nil.library.BufferListener;
import com.clov4r.moboplayer.android.nil.library.Constant;
import com.clov4r.moboplayer.android.nil.library.Global;
import com.clov4r.moboplayer.android.nil.library.ScreenShotLib;
import com.clov4r.moboplayer.android.nil.library.ScreenShotLib.ScreenShotListener;
import com.clov4r.moboplayer.android.nil.library.SoftDecodeSAData;
import com.clov4r.moboplayer.android.rtmp.MoboVideoView;
import com.clov4r.moboplayer.android.rtmp.MoboVideoView.OnVideoStateChangedListener;

public class TestMoboplayer extends Activity {

	// 全局常量
	public static final String TAG = "Aipro-TestMoboplayer";
	public static final char LEFTBRACKET = '{';
	public static final char RIGHTBRACKET = '}';

	RelativeLayout videoLayout = null;
	MoboVideoView mMoboVideoView = null;
	String path = "/mnt/sdcard/AiproDown/wondergirls-nobody.MP4"; // rtmp://183.62.232.213/fileList/test.flv;/mnt/sdcard/03181751_1684.flv
	// final String videoName = "/sdcard/Movies/01010020_0006.MP4";//
	// /sdcard/Movies/output_file_low.mkv--/sdcard/dy/ppkard.mp4

	final String videoName = "rtsp://192.168.42.1/live";// 郑源_一万个理由.wmv  rtmp://61.133.116.49/flv/mp4:n2014/jxjy/kc213/kj2276/fc/gdxxkjzd201401.mp4
															// rtsp://183.58.12.204/PLTV/88888905/224/3221227287/10000100000000060000000001066432_0.smil--rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov

	// Widget
	Button btn1;
	Button btn2;
	Button btn3;
	Button btn4;
	Button btn5;
	Button btn6;
	Button btn7;
	Button btnShow;
	TextView tv1;
	TextView tv2, player_subtitle_textview;
	SeekBar sb1;
	ImageView imageview;

	int recordSize = 0;
	int size[] = { 100, 200, 300, 400 };

	int currentSubSourceIndex = 0;
	// Control
	BtnClickListener mBtnClickListener;

	public void Logd(String str) {
		Log.e(TAG, str);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_test_moboplayer);
		initMember();
		Logd("122 - onCreate");
		findWidget();
		initVideo();
	}

	void initVideo() {

		mMoboVideoView = new MoboVideoView(this, null);
		mMoboVideoView.loadNativeLibs();
		String libpath = getFilesDir().getParent() + "/lib/";
		String libname = "libffmpeg_armv7_neon.so";
		SubtitleJni.getInstance().loadFFmpegLibs(libpath, libname);
		// 打开测试字幕，默认放到SD卡根目录
		String filePath = Environment.getExternalStorageDirectory()
				+ "/Gone.srt";
		// openSubtitleFile(filePath, 0);
		openSubtitleFile(videoName, 0);
		mMoboVideoView.setIsLive(true);
		mMoboVideoView.setBufferedTime(1);// 设置缓冲时间
		mMoboVideoView.setSaveBufferInfoOrNot(false);
		mMoboVideoView.setBufferListener(mBufferListener);// 设置缓冲回调接口
		mMoboVideoView.setVideoPath(videoName);
//		mMoboVideoView.resetDecodeMode(MoboVideoView.decode_mode_soft);
		mMoboVideoView
				.setOnVideoStateChangedListener(mOnVideoStateChangedListener);
		// playAudioOnly(videoName, 0);
		videoLayout.addView(mMoboVideoView);

	}

	BufferListener mBufferListener = new BufferListener() {
		@Override
		public void onBufferStart() {
			// TODO Auto-generated method stub
			mMoboVideoView.pause();
		}

		@Override
		public void onBufferEnd() {
			// TODO Auto-generated method stub
			mMoboVideoView.start();
		}

		@Override
		public void onBufferFailed(String msg) {
			// TODO Auto-generated method stub
			Log.e("MainActivity", msg);
		}

		@Override
		public void onBufferProgressChanged(int current,int duration) {
			// TODO Auto-generated method stub
			if(sb1.getMax()<current)
				sb1.setMax(duration);
			sb1.setSecondaryProgress(current);
		}
	};

	protected boolean isOpenSubtitleFileSuccess;

	/**
	 * Get subtitle by current time.
	 * 
	 * @param currentTime
	 *            : current time.
	 */
	protected String getSubtitle(int currentTime) {
		return SubtitleJni.getInstance().getSubtitleByTime_2(currentTime);
	}

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

	/**
	 * May run out of memory if you are not close the subtitle file .
	 */
	protected void closeSubtitleFile() {
		SubtitleJni.getInstance().closeSubtitle_2();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.act_test_moboplayer, menu);
		return true;
	}

	public void changeSize() {
		Logd("141203 -- videoLayout.getWidth() = " + videoLayout.getWidth()
				+ " videoLayout.getHeight() = " + videoLayout.getHeight());

		recordSize++;
		if (recordSize >= size.length) {
			recordSize = 0;
		}
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				videoLayout.getWidth(), videoLayout.getHeight());
		mMoboVideoView.setLayoutParams(params);
		mMoboVideoView.getHolder().setFixedSize(videoLayout.getWidth(),
				videoLayout.getHeight());
		Logd("141203 - mMoboVideoView - w = " + mMoboVideoView.getWidth()
				+ " h = " + mMoboVideoView.getHeight());

	}

	Timer mTimer;

	protected void startTimer() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				mHandler.sendEmptyMessage(0);
			}
		}, 0, 1000);
	}

	protected void cancelTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	final int msg_show_subtitle = 0;
	final int msg_play_finished = 111;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case msg_show_subtitle:
				sb1.setProgress(mMoboVideoView.getCurrentPosition());
				if (isOpenSubtitleFileSuccess) {
					// long time1 = System.currentTimeMillis();
					int time = mMoboVideoView.getCurrentPosition();
					String subtitle = getSubtitle(time);
					// long time2 = System.currentTimeMillis();
					Log.e("testmobo", "time1=" + time + "---subtitle="
							+ subtitle);
					player_subtitle_textview.setText(subtitle == null ? ""
							: subtitle);
				}

				break;

			case msg_play_finished:
				Toast.makeText(TestMoboplayer.this,
						"mobovideoview--play finished", Toast.LENGTH_SHORT)
						.show();
				break;
			}

		}

	};

	void initMember() {
		mBtnClickListener = new BtnClickListener();
	}

	void findWidget() {
		btn1 = (Button) findViewById(R.id.btn_1);
		btn2 = (Button) findViewById(R.id.btn_2);
		btn3 = (Button) findViewById(R.id.btn_3);
		btn4 = (Button) findViewById(R.id.btn_4);
		btn5 = (Button) findViewById(R.id.btn_5);
		btn6 = (Button) findViewById(R.id.btn_6);
		btnShow = (Button) findViewById(R.id.btn_hideorshow);
		btn7 = (Button) findViewById(R.id.btn_7);
		tv1 = (TextView) findViewById(R.id.tv_1);
		tv2 = (TextView) findViewById(R.id.tv_2);
		player_subtitle_textview = (TextView) findViewById(R.id.player_subtitle_textview);
		sb1 = (SeekBar) findViewById(R.id.sb_1);
		sb1.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		btn1.setOnClickListener(mBtnClickListener);
		btn2.setOnClickListener(mBtnClickListener);
		btn3.setOnClickListener(mBtnClickListener);
		btn4.setOnClickListener(mBtnClickListener);
		btn5.setOnClickListener(mBtnClickListener);
		btn6.setOnClickListener(mBtnClickListener);
		btn7.setOnClickListener(mBtnClickListener);
		videoLayout = (RelativeLayout) findViewById(R.id.video_flayout);
		imageview = (ImageView) findViewById(R.id.imageview);
	}

	public void onDestroy() {
		super.onDestroy();
		mMoboVideoView.stop();
		mMoboVideoView.stopBuffering();
		cancelTimer();
		if (isOpenSubtitleFileSuccess)
			closeSubtitleFile();
		Log.d("Test", "14112911 - onDestroy");
	};

	public boolean onTouchEvent(android.view.MotionEvent event) {
		sb1.setProgress(mMoboVideoView.getCurrentPosition());
		tv2.setText("当前时间：" + mMoboVideoView.getCurrentPosition() / 1000 + "");

		Logd("141127 - videoLayout.width = " + videoLayout.getWidth()
				+ " videoLayout.height = " + videoLayout.getHeight());
		return false;
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
			mMoboVideoView.seekTo(seekBar.getProgress() / 1000);
		}
	};

	OnVideoStateChangedListener mOnVideoStateChangedListener = new OnVideoStateChangedListener() {

		@Override
		public void afterChanged(String arg0) {
			startTimer();
			mMoboVideoView.start();
//			mMoboVideoView.seekTo(1);
			tv1.setText("总时间：" + mMoboVideoView.getDuration() / 1000 + "");
			tv2.setText("当前时间：" + mMoboVideoView.getCurrentPosition() / 1000
					+ "");
			sb1.setMax(mMoboVideoView.getDuration());
			Log.d("Test", "141029 - mMoboVideoView.getDecodeMode() = "
					+ mMoboVideoView.getDecodeMode() + "----duration="
					+ mMoboVideoView.getDuration());

		}

		@Override
		public void beforeChange(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void playFailed(String arg0, int arg1) {
			if (arg1 == Constant.decode_mode_soft
					|| arg1 == Constant.decode_mode_mediacodec)
				Toast.makeText(TestMoboplayer.this, "播放失败", Toast.LENGTH_SHORT)
						.show();
			else
				Toast.makeText(TestMoboplayer.this, "硬解失败,正在转软解",
						Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onPlayFinished(String arg0) {
			// TODO Auto-generated method stub
			// 此处为播放完成回调方法
			mHandler.sendEmptyMessage(msg_play_finished);
			mMoboVideoView.stop();
			cancelTimer();
		}
	};

	class BtnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btn_1:
				if (mMoboVideoView.isPlaying()) {
					mMoboVideoView.pause();
				} else {
					mMoboVideoView.start();
				}
				break;
			case R.id.btn_2:
				mMoboVideoView
						.setVideoPath("/mnt/sdcard/AiproDown/wondergirls-nobody.MP4");
				if (mMoboVideoView.getCurrentVideoPath() != null)
					mMoboVideoView
							.resetDecodeMode(MoboVideoView.decode_mode_soft);
				break;
			case R.id.btn_3:
				Log.d("Test", "141029 - mMoboVideoView.getDecodeMode() = "
						+ mMoboVideoView.getDecodeMode());
				// ScreenShotLibJni.getInstance().setOnBitmapCreatedListener(
				// mOnBitmapCreatedListener);
				final String currentPath = mMoboVideoView.getCurrentVideoPath();
				Bitmap bitmap = ScreenShotLibJni.getInstance().getScreenShot(
						currentPath, "/sdcard/mobo_videoview_test.png",
						mMoboVideoView.getCurrentPosition() / 1000, 200, 200);//
				// Bitmap bitmap=
				// ScreenShotLibJni.getInstance().getIDRFrameThumbnail(
				// currentPath, "/sdcard/mobo_videoview_test.png", 300,
				// 300);
				imageview.setImageBitmap(bitmap);
				break;
			case R.id.btn_4:
				mMoboVideoView.pause();
				Intent inc_callVideoPlayer = new Intent(TestMoboplayer.this,
						VideoPlayer.class);
				startActivity(inc_callVideoPlayer);
				break;
			case R.id.btn_5:
				changeSize();
				break;
			case R.id.btn_6:
				if (btnShow.getVisibility() == View.VISIBLE) {
					btnShow.setVisibility(View.GONE);
				} else {
					btnShow.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.btn_7:
				Intent int_callPlayer = new Intent(TestMoboplayer.this,
						TestCodeAct.class);
				TestMoboplayer.this.startActivity(int_callPlayer);
				break;
			}
		}

	}

	/** 截图完毕回调的接口 **/
	ScreenShotListener mScreenShotListener = new ScreenShotListener() {
		@Override
		public void onFinished(String videoPath, String imageSavePath,
				Bitmap bitmap) {
			// TODO Auto-generated method stub
			Toast.makeText(TestMoboplayer.this, "截图完成", Toast.LENGTH_LONG)
					.show();
		}
	};

	OnBitmapCreatedListener mOnBitmapCreatedListener = new OnBitmapCreatedListener() {
		@Override
		public void onBitmapCreated(final Bitmap bitmap, String fileName,
				String screenshotSavePath) {
			imageview.setImageBitmap(bitmap);
		}

		@Override
		public void onBitmapCreatedFailed(String videoPath) {
			// TODO Auto-generated method stub
			Toast.makeText(TestMoboplayer.this, "截图失败", Toast.LENGTH_SHORT)
					.show();
		}
	};

	/**
	 * 截图缩略图均可用此方法
	 * 
	 * @param currentVideoPath
	 *            视频路径
	 * @param time
	 *            截图时间，单位毫秒
	 * @param width
	 *            截图宽
	 * @param height
	 *            截图高
	 */
	void getScreenShot(String currentVideoPath, int time, int width, int height) {
		// 异步方式获取截图
		ScreenShotLib mScreenShotLib = new ScreenShotLib(this,
				currentVideoPath, "/sdcard/mobo_video_view/"
						+ Global.getNameOf(currentVideoPath) + time + "_a.png",
				time, width, height);
		mScreenShotLib.screenShotAsynchronous(mScreenShotListener);
		// 异步方式获取截图

		// 同步方式获取截图
		mScreenShotLib = new ScreenShotLib(this, currentVideoPath,
				"/sdcard/mobo_video_view/" + Global.getNameOf(currentVideoPath)
						+ time + "_s.png", time - 5 * 1000, width, height);
		mScreenShotLib.screenShotSynchronous();
		// 同步方式获取截图
	}

	String getImageSavePath(String currentVideoPath, long time) {
		return "/sdcard/mobo_video_view/" + Global.getNameOf(currentVideoPath)
				+ time + "_s.png";
	}

	/**
	 * 只播放声音
	 * 
	 * @param videoPath
	 *            视频或音频路径
	 * @param audioIndex
	 *            要播放的视频、音频文件的audiotrack的index
	 */
	protected void playAudioOnly(String videoPath, int audioIndex) {
		mMoboVideoView.openSA(videoPath, SoftDecodeSAData.type_audio,
				currentSubSourceIndex + 1, audioIndex);
	}
}
