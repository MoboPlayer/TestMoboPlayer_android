package com.clov4r.moboplayer.android.rtmp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

public class Moboplayer1107 extends MoboBasePlayer {
	RelativeLayout videoLayout = null;
	SeekBar player_seek_bar;
	TextView subtitle_view;
	Button player_prev, player_seek_backward, player_pause,
			player_seek_forward, player_next, player_screen_shot;

	// MoboVideoView mMoboVideoView = null;
	// ArrayList<String> playList = new ArrayList<String>();
	//String path = "rtmp://192.168.0.236/fileList/test.flv";// rtmp://183.62.232.213/fileList/video4.flv---rtmp://183.62.232.213/fileList/test22--rtmp://183.62.232.213/fileList/test.flv
	
	String path = "rtmp://183.62.232.213/fileList/01041212_0011_E0001.flv";
	Timer mTimer = null;
	int seekInterval = 20;
	/** ֻ������Ƶ **/
	boolean playAudioOnly = false;
	/**
	 * �������������Ƶ�Ĳ��Ž��
	 */
	HashMap<String, Integer> videoPregressMap = new HashMap<String, Integer>();
	
	public void Logd(String str)
	{
		Log.d("MoboPlayer", str);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(getIntent().getStringExtra("play_url") != null)
		{
			
			playList.add(getIntent().getStringExtra("play_url"));
		}
		//playList.add(path);
		//playList.add("http://flv1.vodfile.m1905.com/movie/leJ8Al00HnqXH-L2.flv");
//		playList.add("/mnt/sdcard/AiproDown/yiwentianhuang你好.MP4");
//		playList.add("rtmp://192.168.0.236/fileList/test.flv");
		playList.add("/mnt/sdcard/AiproDown/wondergirls-nobody.MP4");
		//playList.add(path);
		playList.add("http://bcs.duapp.com/apicalcloudservice/media%2F%E5%A5%94%E6%94%BE%E7%9A%84%E9%9D%92%E6%98%A5%E6%97%8B%E5%BE%8B.%E8%A1%97%E8%88%9E-240x192.mp4");
		playList.add("http://pl.youku.com/playlist/m3u8?ts=1415168786&keyframe=1&vid=XNzQ4MTQ5NDAw&type=hd2&sid=241516878626721e6251e&token=8340&oip=2090890179&ep=uHMgELEoaSEs5vBAjyXTsxiSaibshBPbaq4J35gWsd0YSDlwuG86dZPqonTE7JL3&did=3f2189e6a744e68de6761a20ceaf379aa8acfad4&ctype=21&ev=1");

		playList.add("/sdcard/Movies/[���������ձ�ϵ��01���������].Roman.Holiday.1953.DVDRiP.X264.2Audio.AAC.HALFCD-NORM.Christian.mkv");
		playList.add("/sdcard/Movies/07141635_1278.MP4");
		// playList.add("");
		// playList.add("rtmp://183.62.232.213/fileList/test22");
		// playList.add("/storage/emulated/0/������Ƶ/[���������ձ�ϵ��01���������].Roman.Holiday.1953.DVDRiP.X264.2Audio.AAC.HALFCD-NORM.Christian.mkv");
		// playList.add("/storage/emulated/0/������Ƶ/[YYDM-11FANS][CLANNAD][BDrip][24][X264_AAC][1280X720].mkv");
		// playList.add("/storage/emulated/0/������Ƶ/ż����.mp3");
		// playList.add("/storage/emulated/0/������Ƶ/(4K HD)2014Ԥ��Ƭ .mp4");
		playList.add("/sdcard/Movies/[���������ձ�ϵ��01���������].Roman.Holiday.1953.DVDRiP.X264.2Audio.AAC.HALFCD-NORM.Christian.mkv");
		super.onCreate(savedInstanceState);
		currentVideoPath = playList.get(0);
		setContentView(R.layout.activity_main);
		initViews();
		playIndexOf(0);
		
		showFloatPlayerByHomeKey(true);
		setFloatPlayerListener(mFloatPlayerListener);
	}

	void initViews() {
		videoLayout = (RelativeLayout) findViewById(R.id.video_layout);
		player_seek_bar = (SeekBar) findViewById(R.id.player_seek_bar);
		subtitle_view = (TextView) findViewById(R.id.subtitle_view);
		player_prev = (Button) findViewById(R.id.player_prev);
		player_seek_backward = (Button) findViewById(R.id.player_seek_backward);
		player_pause = (Button) findViewById(R.id.player_pause);
		player_seek_forward = (Button) findViewById(R.id.player_seek_forward);
		player_next = (Button) findViewById(R.id.player_next);
		player_screen_shot = (Button) findViewById(R.id.player_screen_shot);
		player_prev.setOnClickListener(mOnClickListener);
		player_seek_backward.setOnClickListener(mOnClickListener);
		player_pause.setOnClickListener(mOnClickListener);
		player_seek_forward.setOnClickListener(mOnClickListener);
		player_next.setOnClickListener(mOnClickListener);
		player_screen_shot.setOnClickListener(mOnClickListener);
		player_seek_bar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		initMoboVideoView();
	}

	void initMoboVideoView() {
		mMoboVideoView = new MoboVideoView(this, null);
		mMoboVideoView.loadNativeLibs();
		mMoboVideoView.resetDecodeMode(MoboVideoView.decode_mode_soft);
		mMoboVideoView
				.setOnVideoStateChangedListener(mOnVideoStateChangedListener);
		videoLayout.addView(mMoboVideoView);
		mMoboVideoView.setIsLive(true);
	}

	public void onDestroy() {
		super.onDestroy();
		cancelPlayerTimer();
		mMoboVideoView.stop();
		Logd("141203 - onDestroy");
	}

	/** ���Żص��ӿ� **/
	OnVideoStateChangedListener mOnVideoStateChangedListener = new OnVideoStateChangedListener() {
		/**
		 * ��Ƶ׼����ɣ����Խ��в��š����Ȳ�����
		 */
		@Override
		public void afterChanged(String arg0) {
			// TODO Auto-generated method stub
			changePlayerScale(PLAY_DISPLAY_MODE_FULL_SCREEN);
			start();
			mMoboVideoView.seekTo(currentPosition);
			duration = mMoboVideoView.getDuration() / 1000;
			player_seek_bar.setMax(duration);
			// getSubInfo(currentVideoPath);//��ȡ��Ƶ����Ļ��Ϣ
			// enableExtSubtitle("/sdcard/Movies/[YYDM-11FANS][CLANNAD][BDrip][24][X264_AAC][1280X720].ass");//��������Ļ

			startPlayerTimer();
		}

		@Override
		public void beforeChange(String arg0) {
			// TODO Auto-generated method stub

		}

		/**
		 * ����ʧ��
		 * 
		 * @param arg0
		 *            ����ʧ�ܵ���Ƶ��ַ
		 * @param arg1
		 *            ���뷽ʽ����ΪӲ�⣬�ڲ��Ѿ��Զ�תΪ��⣬ֻ����½��棨���н�����ʾ��ǰ���뷽ʽ�Ļ�����
		 *            ��Ϊ���ʧ�ܣ��������󲥷���һ����رղ��Ž���
		 */
		@Override
		public void playFailed(String arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg1 == Constant.decode_mode_soft
					|| arg1 == Constant.decode_mode_mediacodec)
				Toast.makeText(Moboplayer1107.this,
						String.format("%s����ʧ��", arg0), Toast.LENGTH_LONG)
						.show();
		}

		@Override
		public void onPlayFinished(String path) {
			// TODO Auto-generated method stub
			Log.d("Moboplayer1107", "141113 - onPlayFinished - path = " + path);
			if (currentPosition >= duration - 1) {
				playIndexOf(currentVideoIndex + 1);
			}
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
			} else if (player_screen_shot == v) {// ��ͼ
				// �첽��ʽ��ȡ��ͼ
				ScreenShotLib mScreenShotLib = new ScreenShotLib(
						Moboplayer1107.this,
						currentVideoPath,
						"/sdcard/mobo_video_view/"
								+ Global.getNameOf(currentVideoPath) + "_a.png",
						50 * 1000, 800, 480);
				mScreenShotLib.screenShotAsynchronous(mScreenShotListener);
				// �첽��ʽ��ȡ��ͼ

				// ͬ����ʽ��ȡ��ͼ
				mScreenShotLib = new ScreenShotLib(
						Moboplayer1107.this,
						currentVideoPath,
						"/sdcard/mobo_video_view/"
								+ Global.getNameOf(currentVideoPath) + "_s.png",
						60 * 1000, 480, 320);
				mScreenShotLib.screenShotSynchronous();
				// ͬ����ʽ��ȡ��ͼ
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
					/** ��Ļ�б� **/
					ArrayList<String> subList = getSubtitleList(msg.obj
							.toString());
					mMoboVideoView.closeSAOf(msg.arg1);// �ͷŻ�ȡ��Ļ��Ϣ������ڴ�
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
			videoPregressMap.put(currentVideoPath, currentPosition);// ����С���ڲ��ŵĽ��
			initMoboVideoView();
			playIndexOf(currentVideoIndex);
		}
	};

	/** ��ͼ��ϻص��Ľӿ� **/
	ScreenShotListener mScreenShotListener = new ScreenShotListener() {
		@Override
		public void onFinished(String videoPath, String imageSavePath,
				Bitmap bitmap) {
			// TODO Auto-generated method stub
			Toast.makeText(Moboplayer1107.this, "��ͼ���", Toast.LENGTH_LONG).show();
		}
	};

	BufferListener mBufferListener = new BufferListener() {
		@Override
		public void onBufferStart() {
			// TODO Auto-generated method stub
			start();
		}

		@Override
		public void onBufferEnd() {
			// TODO Auto-generated method stub
			pause();
		}

		@Override
		public void onBufferFailed(int failCode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBufferProgressChanged(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
	};

	protected void start() {
		mMoboVideoView.start();
		player_state = state_play;
//		player_pause.setBackgroundResource(R.drawable.player_play);
		enableScreenOnSetting();
	}

	protected void pause() {
		if (mMoboVideoView.getPlayState() != state_stop) {
			mMoboVideoView.pause();
			player_state = state_pause;
//			player_pause.setBackgroundResource(R.drawable.player_pause);
			releaseScreenOnSetting();
		}
	}

	protected void stop() {
		mMoboVideoView.stop();
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
			Toast.makeText(this, "�Ѿ��ǵ�һ����Ƶ��", Toast.LENGTH_LONG).show();
		} else {
			cancelPlayerTimer();
			currentVideoIndex = index;
			currentVideoPath = playList.get(index);
			if (videoPregressMap.containsKey(currentVideoPath))
				currentPosition = videoPregressMap.get(currentVideoPath);
			else {
				currentPosition = 0;
				videoPregressMap.put(currentVideoPath, 0);
			}
			stop();
			if (playAudioOnly)
				playAudioOnly(currentVideoPath, 0);
			else {
				// mMoboVideoView.setIsLive(true);// �㲥ʱ�ɲ�����
				// mMoboVideoView.setBufferedTime(5);// ���û���ʱ��
				// mMoboVideoView.setBufferListener(mBufferListener);// ���û���ص��ӿ�

				videoParams = 0 + "\n" + 0;// ���ŵ�0������+��0����Ļ
				setVideoPath(currentVideoPath, videoParams,
						MoboVideoView.decode_mode_soft);// decode_mode_hard
			}
		}
	}

	protected void changePlayerScale(int displayMode) {
		setPlayerScale(displayMode);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				playerWidth, playerHeight);
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
			int bufferedTime = mMoboVideoView.getBufferTime();
			player_seek_bar.setSecondaryProgress(bufferedTime);
			player_seek_bar.setProgress(currentPosition);
			// if (currentPosition >= duration - 1) {//�ŵ�onPlayFinished()�������
			// playIndexOf(currentVideoIndex + 1);
			// }
			String subtitle = getTextSubtitle(currentPosition);
			if (subtitle != null)
				subtitle_view.setText(Html.fromHtml(subtitle));//
		}
	}

	/**
	 * ��ȡ��Ļ��Ϣ
	 * 
	 * @param subPath
	 */
	void getSubInfo(String subPath) {
		mMoboVideoView.setBaseHandler(mHandler);// ��ȡ��Ļ��Ϣ�ɹ����ص�mHandler
		setSubtitle(currentVideoPath, 0);
	}

	/**
	 * �����õĵ�index����Ļ
	 * 
	 * @param index
	 */
	void enableInnerSubtitle(int index) {
		mMoboVideoView.setBaseHandler(null);
		setSubtitle(currentVideoPath, index);
	}

	/**
	 * ��������Ļ
	 */
	void enableExtSubtitle(String subPath) {
		setSubtitle(subPath, 0);
	}
}
