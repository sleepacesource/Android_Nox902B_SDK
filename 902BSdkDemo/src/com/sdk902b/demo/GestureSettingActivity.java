package com.sdk902b.demo;

import java.util.List;

import android.R.bool;
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

public class GestureSettingActivity extends BaseActivity {

	private ImageView mImChangeScene;

	private ImageView mImChangeMusic;

	private ImageView mImDisable;



	private RelativeLayout mlayoutChangeScene;
	private RelativeLayout mlayoutChangeMusic;
	private RelativeLayout mlayoutChangeInvalid;

	private static final byte OPERATION_WAVE = 0x00;// 挥手
	private static final byte OPERATION_LIGTH_COLOR = 0x00;// 切换灯光颜色
	private static final byte OPERATION_MUSIC = 0x02;// 切换音乐
	private static final byte OPERATION_STOP = (byte) 0xFF;// 停用
	private byte TEMP_SELECT = 0x00;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_set);
		findView();
		initListener();
		initUI();
		initData();
	}

	public void findView() {
		super.findView();
		mImChangeScene = (ImageView) findViewById(R.id.gesture_control_im_change_scene);
		mImChangeMusic = (ImageView) findViewById(R.id.gesture_control_im_change_music);
		mImDisable = (ImageView) findViewById(R.id.gesture_control_im_invalid);
	
		mlayoutChangeScene = (RelativeLayout) findViewById(R.id.gesture_control_rl_change_light_color);
		mlayoutChangeMusic = (RelativeLayout) findViewById(R.id.gesture_control_rl_change_music);
		mlayoutChangeInvalid = (RelativeLayout) findViewById(R.id.gesture_control_rl_invalid);

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
								byte  gesture=	bleNoxGestureInList.get(i).getGesture();
								byte opt=bleNoxGestureInList.get(i).getOpt();
								if(gesture==0){
									if(opt==OPERATION_LIGTH_COLOR){
										selectLightColor();
									}else if(opt==OPERATION_MUSIC){
										selectMusic();
									}else if(opt==OPERATION_STOP){
										selectDisable();
									}
								}
							}
							//Log.e(TAG,"挥手手势个数:"+size +"操作编号:"+bleNoxGestureInList.get(0).getOpt()+"===输入动作==："+bleNoxGestureInList.get(0).getGesture());
						} else {
							showErrTips(cd);
						}
					}
				});
			}
		});
	}

	public void initListener() {
		super.initListener();
		mlayoutChangeScene.setOnClickListener(this);
		mlayoutChangeMusic.setOnClickListener(this);
		mlayoutChangeInvalid.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();

			}
		});

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

	public void initUI() {
		tvTitle.setText(getString(R.string.Wave));
		tvRight.setText(getString(R.string.save));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gesture_control_rl_change_light_color:
			selectLightColor();
			break;

		case R.id.gesture_control_rl_change_music:
			selectMusic();
			break;

		case R.id.gesture_control_rl_invalid:
			selectDisable();
			break;
		}

	}

	private void selectMusic() {
		mImChangeMusic.setVisibility(View.VISIBLE);
		mImChangeScene.setVisibility(View.GONE);
		mImDisable.setVisibility(View.GONE);
		TEMP_SELECT = OPERATION_MUSIC;

	}

	private void selectDisable() {
		mImChangeMusic.setVisibility(View.GONE);
		mImChangeScene.setVisibility(View.GONE);
		mImDisable.setVisibility(View.VISIBLE);
		TEMP_SELECT = OPERATION_STOP;

	}

	private void selectLightColor() {
		mImChangeMusic.setVisibility(View.GONE);
		mImChangeScene.setVisibility(View.VISIBLE);
		mImDisable.setVisibility(View.GONE);
		TEMP_SELECT = OPERATION_LIGTH_COLOR;

	}
}
