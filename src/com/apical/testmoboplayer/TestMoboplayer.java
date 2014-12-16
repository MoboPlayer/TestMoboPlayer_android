package com.apical.testmoboplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clov4r.moboplayer.android.nil.codec.ScreenShotLibJni;
import com.clov4r.moboplayer.android.nil.codec.SubtitleJni;
import com.clov4r.moboplayer.android.nil.codec.ScreenShotLibJni.OnBitmapCreatedListener;
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
	final String videoName ="/sdcard/movie/原子弹.flv";
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
	TextView tv2;
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
		// mMoboVideoView
		// .setVideoPath("/sdcard/Movies/[奥黛丽·赫本系列01：罗马假日].Roman.Holiday.1953.DVDRiP.X264.2Audio.AAC.HALFCD-NORM.Christian.mkv");
		mMoboVideoView.setVideoPath(videoName);//
//		 mMoboVideoView.setIsLive(true);
		// playAudioOnly(
		// "/sdcard/Movies/[奥黛丽·赫本系列01：罗马假日].Roman.Holiday.1953.DVDRiP.X264.2Audio.AAC.HALFCD-NORM.Christian.mkv",
		// 0);
		// // /mnt/sdcard/AiproDown/wondergirls-nobody.MP4--
		// 请改为对应的地址
		// mMoboVideoView
		// .setVideoPath("rtmp://183.62.232.213/fileList/test.flv");//http://hot.vrs.sohu.com/ipad2132022_4629335848402_5343343.m3u8?plat=3---info=v;1280;720;0;h264
		// // 网络流不能播放。
		// mMoboVideoView.resetDecodeMode(MoboVideoView.decode_mode_soft);
		videoLayout.addView(mMoboVideoView);
		// mMoboVideoView.resetDecodeMode(MoboVideoView.decode_mode_hard);
		mMoboVideoView
				.setOnVideoStateChangedListener(mOnVideoStateChangedListener);

		// if (mMoboVideoView.getCurrentVideoPath() != null)
		// mMoboVideoView.resetDecodeMode(MoboVideoView.decode_mode_hard);
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
		sb1 = (SeekBar) findViewById(R.id.sb_1);
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
		Log.d("Test", "14112911 - onDestroy");
	};

	public boolean onTouchEvent(android.view.MotionEvent event) {
		sb1.setProgress(mMoboVideoView.getCurrentPosition());
		tv2.setText("当前时间：" + mMoboVideoView.getCurrentPosition() / 1000 + "");

		Logd("141127 - videoLayout.width = " + videoLayout.getWidth()
				+ " videoLayout.height = " + videoLayout.getHeight());
		return false;
	};

	OnVideoStateChangedListener mOnVideoStateChangedListener = new OnVideoStateChangedListener() {

		@Override
		public void afterChanged(String arg0) {
			mMoboVideoView.start();
			tv1.setText("总时间：" + mMoboVideoView.getDuration() / 1000 + "");
			tv2.setText("当前时间：" + mMoboVideoView.getCurrentPosition() / 1000
					+ "");
			sb1.setMax(mMoboVideoView.getDuration());
			Log.d("Test", "141029 - mMoboVideoView.getDecodeMode() = "
					+ mMoboVideoView.getDecodeMode());
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
			// 此处为播放完成回调方法
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
				// getScreenShot(mMoboVideoView.getCurrentVideoPath(),50*1000,300,200);
//				getScreenShot(mMoboVideoView.getCurrentVideoPath(),
//						mMoboVideoView.getCurrentPosition(), 500, 350);
		        String libpath = getFilesDir().getParent()+"/lib/";
		        String libname = "libffmpeg_armv7_neon.so";
				SubtitleJni jni=new SubtitleJni();
				jni.loadFFmpegLibs(libpath,libname);
				ScreenShotLibJni.getInstance().setOnBitmapCreatedListener(mOnBitmapCreatedListener);
				ScreenShotLibJni.getInstance().getScreenShot(mMoboVideoView.getCurrentVideoPath(), 50, 200, 200);
				
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
		public void onBitmapCreated(final Bitmap bitmap, String fileName) {
			imageview.setImageBitmap(bitmap);
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
