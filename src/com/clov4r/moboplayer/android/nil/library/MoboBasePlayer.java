package com.clov4r.moboplayer.android.nil.library;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.clov4r.moboplayer.android.rtmp.MoboVideoView;
import com.clov4r.moboplayer.android.rtmp.MoboVideoView.OnVideoStateChangedListener;

public class MoboBasePlayer extends Activity {
	public static final int msg_refresh_progress = 110;
	public static final int msg_get_subtitle_info = 111;
	/** Ĭ�ϱ����ʹ�С **/
	static final public int PLAY_DISPLAY_MODE_NORMAL = 0;
	/** ������������ȫ�� **/
	static final public int PLAY_DISPLAY_MODE_FULL_SCREEN = 1;
	/** ������Ļ **/
	static final public int PLAY_DISPLAY_MODE_FILL = 2;
	/** 16��9 **/
	static final public int PLAY_DISPLAY_MODE_NINE = 3;
	/** 4��3 **/
	static final public int PLAY_DISPLAY_MODE_FOUR = 4;

	public static final int state_play = 0;
	public static final int state_pause = 1;
	public static final int state_stop = 2;

	protected int player_state = state_stop;
	protected int displayMode = PLAY_DISPLAY_MODE_FULL_SCREEN;
	private int videoWidth, videoHeight;
	private boolean isHorizontal = true;

	private String character = "UTF-8";
	/** ��ǰ���ŵ�ַ **/
	protected String currentVideoPath = null;
	/** ���������view **/
	protected MoboVideoView mMoboVideoView = null;
	private WindowCreateLib mWindowCreateLib = null;
	private PlayerStateData mPlayerStateData = null;

	private String currentSubPath;
	private int currentSubSourceIndex;
	private int currentSubStreamIndex;
	/** ��ǰ���Ż���Ŀ�͸� **/
	protected int playerWidth, playerHeight;
	private boolean showFloatPlayerByHomeKey = false;
	/** ��������״̬ **/
	protected boolean isFloatWindowMode = false;
	/** ��Ƶʱ������λ���� **/
	protected int duration = 0;
	/** ��ǰ����λ�ã���λ���� **/
	protected int currentPosition = 0;
	/** ��ǰ���ŵ���Ƶ���б��е�index **/
	protected int currentVideoIndex = 0;
	/** ������Ҫ���ݵĲ�������ʽΪ�������index\n��Ļ��index \n ���ܻ������� **/
	protected String videoParams = null;

	protected ArrayList<String> playList = new ArrayList<String>();
	private ArrayList<LocalVideoData> videoDataList = new ArrayList<LocalVideoData>();
	private LocalVideoData currentLocalVideoData = null;
	private FloatPlayerListener mFloatPlayerListener = null;
	private HomeKeyBroadCastReceiver mHomeKeyBroadCastReceiver = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreverenceLib.initSp(this);
		init();
		initData();
	}

	public void onDestroy() {
		super.onDestroy();
//		mMoboVideoView.stop();
		unregisterReceiver(mHomeKeyBroadCastReceiver);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int orientation = newConfig.orientation;
		isHorizontal = orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (player_state == state_play && !isFloatWindowMode) {
			pause();
		}
	}

	@Override
	public void onRestart() {
		super.onRestart();
		if (isFloatWindowMode) {
			changeToNormalPlayer();
		} else {
			start();
		}
	}

	/** ���Żص��ӿ� **/
	protected OnVideoStateChangedListener mOnVideoStateChangedListener = new OnVideoStateChangedListener() {

		@Override
		public void afterChanged(String arg0) {
			// TODO Auto-generated method stub
			mMoboVideoView.start();
			// mMoboVideoView.pause();
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
		 *            ���뷽ʽ����ΪӲ��
		 */
		@Override
		public void playFailed(String arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPlayFinished(String path) {
			// TODO Auto-generated method stub
			
		}
	};

	private class HomeKeyBroadCastReceiver extends BroadcastReceiver {
		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";// home key
		static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)
							&& showFloatPlayerByHomeKey) {
						changeToFloatPlayer();
					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
						// long home key
					}
				}
			}
		}

	}

	DisplayMetrics dm = null;

	private void init() {
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = Global.getRealScreenSize(true, this);// dm.widthPixels;
		int height = Global.getRealScreenSize(false, this);// dm.heightPixels;
		Global.screenHeight = Math.max(width, height);
		Global.screenWidth = Math.min(width, height);

		if ((Global.screenHeight >= 1000 || Global.screenWidth >= 720)
				&& dm.density > 0) {
			Global.screenSize = (int) (Math.sqrt(Global.screenWidth
					* Global.screenWidth + Global.screenHeight
					* Global.screenHeight) / dm.densityDpi);
			if (Global.screenSize >= 7) {
				Global.isPad = true;
			}
		}
		mHomeKeyBroadCastReceiver = new HomeKeyBroadCastReceiver();
		registerReceiver(mHomeKeyBroadCastReceiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
	}

	private void initData() {
		if (playList != null && playList.size() > 0) {
			for (int i = 0; i < playList.size(); i++) {
				String videoPath = playList.get(i);
				videoDataList.add(getLocalVideoDataOf(videoPath));
			}
			currentLocalVideoData = videoDataList.get(currentVideoIndex);
		}
	}

	private LocalVideoData getLocalVideoDataOf(String path) {
		LocalVideoData data = new LocalVideoData();
		data.absPath = path;
		data.name = Global.getNameOf(path);
		data.fileFormat = Global.getFileFormat(path);
		return data;
	}

	/**
	 * ��ʹ���������
	 */
	protected void initVideoView() {
		mMoboVideoView = new MoboVideoView(this, null);
		// mMoboVideoView.setIsLive(true);//�㲥ʱ�ɲ�����
		mMoboVideoView
				.setOnVideoStateChangedListener(mOnVideoStateChangedListener);
	}

	/**
	 * �����Ƿ��ڵ��home��ʱ����С����
	 * 
	 * @param enable
	 */
	protected void showFloatPlayerByHomeKey(boolean enable) {
		showFloatPlayerByHomeKey = enable;
	}

	/**
	 * �����������ڵĻص��ӿ�
	 * 
	 * @param listener
	 */
	protected void setFloatPlayerListener(FloatPlayerListener listener) {
		mFloatPlayerListener = listener;
	}

	/**
	 * ���ò��ŵ�ַ
	 * 
	 * @param path
	 * @param playParams
	 *            audioIndex+"\n"+subIndex
	 * @param decodeMode
	 */
	protected void setVideoPath(String path, String playParams, int decodeMode) {
		currentVideoPath = path;
		mMoboVideoView.setVideoPath(path, playParams);//, 0
		if (mMoboVideoView.getCurrentVideoPath() != null)
			mMoboVideoView.resetDecodeMode(decodeMode);
	}

	protected void pause() {

	}

	protected void start() {

	}

	/**
	 * �л�������Ļ
	 * 
	 * @param subStreamIndex
	 *            ҕ�l�ļ��ȵ���Ļ����index
	 * @param subSourceIndex
	 *            ��Ļ��sourceIndex��ÿ����Ļ����ָ��һ��sourceIndex.
	 */
	private void exchangeInnerSubtitle(int subSourceIndex, int subStreamIndex) {
		currentSubPath = currentVideoPath;
		currentSubSourceIndex = subSourceIndex;
		currentSubStreamIndex = subStreamIndex;
		openSubtitle();
	}

	/**
	 * �л�������Ļ
	 * 
	 * @param subPath
	 * @param subSourceIndex
	 *            ��Ļ��sourceIndex��ÿ����Ļ����ָ��һ��sourceIndex.
	 * @param subStreamIndex
	 *            ����������Ļ����Ļ����index(����������Ļ�ļ�����������Ļ��)
	 */
	private void setExtSubtitle(String subPath, int subSourceIndex,
			int subStreamIndex) {
		currentSubPath = subPath;
		currentSubSourceIndex = subSourceIndex;
		currentSubStreamIndex = subStreamIndex;
		openSubtitle();
	}

	private void openSubtitle() {
		mMoboVideoView.openSA(currentSubPath, SoftDecodeSAData.type_subtitle,
				currentSubSourceIndex, currentSubStreamIndex);
	}

	/**
	 * �ر���Ļ���ͷ���Ļ������ص��ڴ�
	 * 
	 * @param subPath
	 * @param subSourceIndex
	 *            ��Ļ��sourceIndex��ÿ����Ļ����ָ��һ��sourceIndex.
	 * @param subStreamIndex
	 *            ����������Ļ����Ļ����index(����������Ļ�ļ�����������Ļ��)
	 */
	private void closeSub(String subPath, int subSourceIndex, int subStreamIndex) {
		mMoboVideoView.closeSA(subPath, SoftDecodeSAData.type_subtitle,
				subSourceIndex, subStreamIndex);
	}

	/**
	 * ������Ļ
	 * 
	 * @param subPath
	 *            ��Ļ�ļ�·������Ƶ������Ļ��Ҫ������Ƶ·����
	 * @param streamIndex
	 *            ������Ļ����Ļ����index
	 */
	protected void setSubtitle(String subPath, int streamIndex) {
		if (subPath == null || subPath.equals(currentVideoPath)) {
			exchangeInnerSubtitle(currentSubSourceIndex + 1, streamIndex);
		} else {
			setExtSubtitle(subPath, currentSubSourceIndex + 1, streamIndex);
		}
	}

	/**
	 * ֻ��������
	 * 
	 * @param videoPath
	 *            ��Ƶ����Ƶ·��
	 * @param audioIndex
	 *            Ҫ���ŵ���Ƶ����Ƶ�ļ���audiotrack��index
	 */
	protected void playAudioOnly(String videoPath, int audioIndex) {
		mMoboVideoView.openSA(videoPath, SoftDecodeSAData.type_audio,
				currentSubSourceIndex + 1, audioIndex);
	}

	protected void changeAudioTrack(int index) {
		mMoboVideoView.changeAudioChannel(index);
	}

	/**
	 * ��ȡ��ǰʱ����ļ���Ļ
	 * 
	 * @param time
	 * @return
	 */
	protected String getTextSubtitle(int time) {
		return mMoboVideoView.getCurrentSubtitle(time * 1000);//, character
	}

	/**
	 * ��ȡ��ǰʱ���ͼ����Ļ
	 * 
	 * @param time
	 * @return
	 */
	protected Bitmap getImageSubtitle(int time) {
		return mMoboVideoView.getCurrentImageSubtitle(time * 1000);
	}

	/**
	 * ������Ļ����
	 */
	protected void enableScreenOnSetting() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * �ͷ���Ļ����
	 */
	protected void releaseScreenOnSetting() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * ���ò��ű���
	 * 
	 * @param displayMode
	 */
	protected void setPlayerScale(int displayMode) {
		this.displayMode = displayMode;
		if (videoHeight <= 0 || videoWidth <= 0) {
			videoWidth = mMoboVideoView.getVideoWidth();
			videoHeight = mMoboVideoView.getVideoHeight();
		}
		if (videoHeight <= 0 || videoWidth <= 0)
			return;
		exchangePlayerScale();
	}

	private void exchangePlayerScale() {
		if (videoHeight <= 0 || videoWidth <= 0)
			return;
		float f = (float) videoWidth / (float) videoHeight;

		if (displayMode > PLAY_DISPLAY_MODE_FOUR) {
			displayMode = PLAY_DISPLAY_MODE_NORMAL;
		} else if (displayMode < 0)
			displayMode = 1;

		int dstWidth = 0;
		int dstHeight = 0;

		int h = 0;
		int w = 0;

		h = isHorizontal ? Global.screenWidth : Global.screenHeight;
		w = isHorizontal ? Global.screenHeight : Global.screenWidth;

		switch (displayMode) {
		case PLAY_DISPLAY_MODE_NORMAL:
			dstWidth = videoWidth;
			dstHeight = videoHeight;

			break;
		case PLAY_DISPLAY_MODE_FULL_SCREEN:
			if (w > (int) (h * f))
				w = (int) (h * f);
			else
				h = (int) (w / f);

			dstWidth = w;
			dstHeight = h;

			break;
		case PLAY_DISPLAY_MODE_FILL:
			dstWidth = w;
			dstHeight = h;

			break;
		case PLAY_DISPLAY_MODE_NINE:
			float widthScale_1 = w / 16f;
			float heightScale_1 = h / 9f;
			if (widthScale_1 > heightScale_1) {
				dstHeight = h;
				dstWidth = dstHeight * 16 / 9;
			} else {
				dstWidth = w;
				dstHeight = dstWidth * 9 / 16;
			}

			break;
		case PLAY_DISPLAY_MODE_FOUR:
			float widthScale_2 = w / 4f;
			float heightScale_2 = h / 3f;
			if (widthScale_2 > heightScale_2) {
				dstHeight = h;
				dstWidth = dstHeight * 4 / 3;
			} else {
				dstWidth = w;
				dstHeight = dstWidth * 3 / 4;
			}

			break;
		}

		playerWidth = dstWidth;
		playerHeight = dstHeight;
	}

	protected ArrayList<String> getSubtitleList(String subtitleInfo) {
		ArrayList<String> subList = new ArrayList<String>();
		// String[] titleArray = null;
		String[] titleLine = subtitleInfo.split("\n");
		if (titleLine != null) {
			// titleArray = new String[titleLine.length];
			for (int i = 0; i < titleLine.length; i++) {
				String tmpInfo = titleLine[i];
				if (tmpInfo != null && tmpInfo.startsWith("s")) {
					String[] titleInfo = tmpInfo.split(";");
					if (titleInfo != null && titleInfo.length == 4) {
						subList.add(titleInfo[1] + "-" + titleInfo[2]);
					}
				}
			}
		}

		return subList;
	}

	protected void playIndexOf(int index) {

	}

	protected void changeToFloatPlayer() {
		isFloatWindowMode = true;
		mMoboVideoView.pause();
		mMoboVideoView.stop();

		currentLocalVideoData = videoDataList.get(currentVideoIndex);
		currentLocalVideoData.lastDecodeMode = mMoboVideoView.getDecodeMode();
		currentLocalVideoData.lastEndTime = currentPosition;
		mPlayerStateData = new PlayerStateData(videoDataList, currentVideoPath,
				currentVideoIndex, player_state, videoParams, 0, false, false);
		mWindowCreateLib = new WindowCreateLib(this, mPlayerStateData, false,
				false);
		mWindowCreateLib.addVideoView(0);
		if (mFloatPlayerListener != null)
			mFloatPlayerListener.onFloatPlayerPop();
	}

	protected void changeToNormalPlayer() {
		int decodeMode = Constant.decode_mode_hard;
		if (isFloatWindowMode && mWindowCreateLib != null) {
			mWindowCreateLib.removeView();
			videoParams = mPlayerStateData.params;
			currentVideoIndex = mPlayerStateData.currentIndex;
			currentLocalVideoData = mPlayerStateData.dataList
					.get(currentVideoIndex);
			currentPosition = currentLocalVideoData.lastEndTime;
			decodeMode = currentLocalVideoData.lastDecodeMode;
			if (currentVideoIndex != mPlayerStateData.currentIndex) {
				currentVideoIndex = mPlayerStateData.currentIndex;
			}
		}
		if (mFloatPlayerListener != null)
			mFloatPlayerListener.onFloatPlayerDismiss();
		isFloatWindowMode = false;
	}

	private void reinitMoboVideoView(int decodeMode) {

		initVideoView();
		setVideoPath(currentVideoPath, videoParams, decodeMode);
	}

}
