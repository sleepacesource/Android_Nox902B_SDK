package com.sdk902b.demo;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sdk902b.demo.R;
import com.sdk902b.demo.util.ActivityUtil;
import com.sleepace.sdk.core.nox.domain.BleNoxGestureInfo;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;

public class FloatStopActivity extends BaseActivity {

	private ImageView mImChangeMusic;

	private ImageView mImDisable;
	
	
	
	private  RelativeLayout mLayoutChangeMusic;
	private RelativeLayout  mLayoutDisable;

	private static final byte OPERATION_WAVE = 0x01;// 悬停
	private static final byte OPERATION_MUSIC = 0x01;// 暂停播放
	private static final byte OPERATION_STOP = (byte) 0xFF;// 停用
	private byte TEMP_SELECT = 0x00;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_float_stop);
		findView();
		initListener();
		initUI();
		initData();
	}

	public void findView() {
		super.findView();
		mLayoutChangeMusic = (RelativeLayout) findViewById(R.id.gesture_play_pause_music);
		mLayoutDisable = (RelativeLayout) findViewById(R.id.gesture_hover_rl_invalid);
		mImChangeMusic=(ImageView)findViewById(R.id.gesture_control_im_change_scene);
		mImDisable = (ImageView) findViewById(R.id.gesture_control_im_invalid);
	}

	public void initUI() {
		tvTitle.setText(getString(R.string.Hover));
		tvRight.setText(getString(R.string.save));
	}
	public void initData(){
		showLoading();
		//获取手势信息
		mHelper.gestureConfigGet(3000,new IResultCallback<List<BleNoxGestureInfo>>() {
			@Override
			public void onResultCallback(final CallbackData<List<BleNoxGestureInfo>> cd) {
				// TODO Auto-generated method stub
				if (!ActivityUtil.isActivityAlive(mActivity)) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						hideLoading();
						if (cd.isSuccess()) {
							List<BleNoxGestureInfo> bleNoxGestureInList=cd.getResult();
							int size=bleNoxGestureInList.size();
							for(int i=0;i<size;i++){
								byte gesture=	bleNoxGestureInList.get(i).getGesture();
								byte opt=bleNoxGestureInList.get(i).getOpt();
								if(gesture==1){
									if(opt==OPERATION_STOP){
										selectDisable();
									}else if(opt==OPERATION_MUSIC){
										selectMusic();
									}
								}
							}
							Log.e(TAG,"悬停手势个数:"+size +"操作编号:"+bleNoxGestureInList.get(1).getOpt()+"===输入动作==："+bleNoxGestureInList.get(1).getGesture());
						} else {
							showErrTips(cd);
						}
					}
				});
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.gesture_play_pause_music:
			selectMusic();
			break;

		case R.id.gesture_hover_rl_invalid:
			selectDisable();
			break;
		}

	}

	private void selectMusic() {
		mImChangeMusic.setVisibility(View.VISIBLE);
		mImDisable.setVisibility(View.GONE);
		TEMP_SELECT = OPERATION_MUSIC;

	}

	private void selectDisable() {
		mImChangeMusic.setVisibility(View.GONE);
		mImDisable.setVisibility(View.VISIBLE);
		TEMP_SELECT = OPERATION_STOP;

	}

	public void initListener() {
		super.initListener();
		mLayoutChangeMusic.setOnClickListener(this);
		mLayoutDisable.setOnClickListener(this);
		tvRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showLoading();

				mHelper.gestureConfigSet(OPERATION_WAVE, TEMP_SELECT, 3000,
						new IResultCallback() {
							@Override
							public void onResultCallback(final CallbackData cd) {
								// TODO Auto-generated method stub
								if (!ActivityUtil.isActivityAlive(mActivity)) {
									return;
								}

								runOnUiThread(new Runnable() {
									public void run() {
										hideLoading();
										if (cd.isSuccess()) {
											finish();
										} else {
											showErrTips(cd);
										}
									}
								});
							}
						});
			}
		});
		
	
	}

}
