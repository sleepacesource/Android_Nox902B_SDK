package com.sdk902b.demo;

import com.sdk902b.demo.R;
import com.sdk902b.demo.util.CrashHandler;
import com.sleepace.sdk.util.SdkLog;

import android.app.Application;

public class DemoApp extends Application {

	public static final String APP_TAG = "SN902BSdk";
	public static final String FIRMWARE_NAME = "SN902B_20210331_1.45.MVA";

	public static final int[][] ALARM_MUSIC = { 
			{ 31001, R.string.alarm_list_1 }, 
			{ 31002, R.string.alarm_list_2 }, 
			{ 31003, R.string.alarm_list_3 }, 
			{ 31004, R.string.alarm_list_4 },
			{ 31005, R.string.alarm_list_5 }, 
			{ 31006, R.string.alarm_list_6 }, 
			{ 31007, R.string.alarm_list_7 }, 
			{ 31008, R.string.alarm_list_8 }, 
			{ 31009, R.string.alarm_list_9 } };

	public static final int[][] SLEEPAID_MUSIC = { 
			{ 30001, R.string.music_list_sea }, 
			{ 30002, R.string.music_list_sun }, 
			{ 30003, R.string.music_list_dance }, 
			{ 30004, R.string.music_list_star },
			{ 30005, R.string.music_list_solo }, 
			{ 30006, R.string.music_list_rain }, 
			{ 30007, R.string.music_list_wind }, 
			{ 30008, R.string.music_list_summer } };

	private static DemoApp instance;
	
	private static long seqId = 0;
	
	public synchronized static long getSeqId() {
		seqId++;
		if(seqId >= Long.MAX_VALUE ) {
			seqId = 1;
		}
		return seqId;
	}

	public static DemoApp getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		CrashHandler.getInstance().init(this);
		SdkLog.setLogTag(APP_TAG);
		SdkLog.setLogEnable(true);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		// 低内存的时候执行
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		// 程序在内存清理的时候执行
		super.onTrimMemory(level);

	}

}
