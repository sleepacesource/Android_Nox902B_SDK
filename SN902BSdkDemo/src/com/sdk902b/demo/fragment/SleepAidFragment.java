package com.sdk902b.demo.fragment;

import com.sdk902b.demo.BaseActivity.MyOnTouchListener;
import com.sdk902b.demo.DataListActivity;
import com.sdk902b.demo.DemoApp;
import com.sdk902b.demo.R;
import com.sdk902b.demo.bean.MusicInfo;
import com.sdk902b.demo.util.ActivityUtil;
import com.sdk902b.demo.util.Utils;
import com.sdk902b.demo.view.SelectValueDialog;
import com.sdk902b.demo.view.SelectValueDialog.ValueSelectedListener;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.core.nox.interfs.INoxManager;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.SdkLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SleepAidFragment extends BaseFragment {
	private View maskView;
	private TextView tvMusic, tvLoopMode, tvSleepAidTimeValue, tvSleepAidTips;
	private View vSleepAidTime;
	private EditText etVolume, etR, etG, etB, etW, etBrightness;
	private Button btnSendVolume, btnPlayMusic, btnSendColor, btnSendBrightness, btnCloseLight, btnSave;
	private RadioGroup rgAroma;
	
	private SelectValueDialog valueDialog;
	private static byte aidStopDuration = 45;
	private static byte loopMode = 2; //循环模式， 0:顺序循环，1:随机循环，2:单曲循环
	private static MusicInfo music = new MusicInfo();
	private Object[] aidDurationItems = null, loopModeItems = null;
	
	private static byte cR = (byte)255, cG = 35, cB = 0, cW = 0, bri = 30, vol = 6;
	
	private static boolean playing;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_sleepaid, null);
//		SdkLog.log(TAG+" onCreateView-----------");
		findView(root);
		initListener();
		initUI();
		return root;
	}
	
	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		maskView = root.findViewById(R.id.mask);
		tvMusic = (TextView) root.findViewById(R.id.tv_music_name);
		tvLoopMode = (TextView) root.findViewById(R.id.tv_loop_mode);
		etVolume = (EditText) root.findViewById(R.id.et_volume);
		etR = (EditText) root.findViewById(R.id.et_r); 
		etG = (EditText) root.findViewById(R.id.et_g); 
		etB = (EditText) root.findViewById(R.id.et_b); 
		etW = (EditText) root.findViewById(R.id.et_w);
		etBrightness = (EditText) root.findViewById(R.id.et_brightness);
		btnSendVolume = (Button) root.findViewById(R.id.btn_volume);
		btnPlayMusic = (Button) root.findViewById(R.id.btn_music);
		btnSendColor = (Button) root.findViewById(R.id.btn_w);
		btnSendBrightness = (Button) root.findViewById(R.id.btn_brightness);
		btnCloseLight = (Button) root.findViewById(R.id.btn_close_light);
		btnSave = (Button) root.findViewById(R.id.btn_save);
		rgAroma = (RadioGroup) root.findViewById(R.id.rg_aroma_speed);
		vSleepAidTime = root.findViewById(R.id.layout_sleepaid_time);
		tvSleepAidTimeValue = (TextView) root.findViewById(R.id.tv_sleepaid_time_value);
		tvSleepAidTips = (TextView) root.findViewById(R.id.tv_tips_sleep_aid);
	}


	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getDeviceHelper().addConnectionStateCallback(stateCallback);
		tvMusic.setOnClickListener(this);
		tvLoopMode.setOnClickListener(this);
		vSleepAidTime.setOnClickListener(this);
		btnSendVolume.setOnClickListener(this);
		btnPlayMusic.setOnClickListener(this);
		btnSendColor.setOnClickListener(this);
		btnSendBrightness.setOnClickListener(this);
		btnCloseLight.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		rgAroma.setOnCheckedChangeListener(checkedChangeListener);
		etVolume.addTextChangedListener(volumeWatcher);
		etG.addTextChangedListener(gWatcher);
		etBrightness.addTextChangedListener(brightnessWatcher);
		registerTouchListener(touchListener);
	}
	
	private MyOnTouchListener touchListener = new MyOnTouchListener() {
		@Override
		public boolean onTouch(MotionEvent ev) {
			// TODO Auto-generated method stub
			View view = mActivity.getCurrentFocus();
			ActivityUtil.hideKeyboard(ev, view, mActivity);
			return false;
		}
	};
	
	private TextWatcher volumeWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String str = s.toString();
			if(!TextUtils.isEmpty(str) && !Utils.inputTips(etVolume, 16)) {
				vol = (byte)(int)Integer.valueOf(str);
			}
		}
	};
	
	private TextWatcher gWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String str = s.toString();
			if(!TextUtils.isEmpty(str) && !Utils.inputTips(etG, 120)) {
				cG = (byte)(int)Integer.valueOf(str);
			}
		}
	};
	
	private TextWatcher brightnessWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String str = s.toString();
			if(!TextUtils.isEmpty(str) && !Utils.inputTips(etBrightness, 100)) {
				bri = (byte)(int)Integer.valueOf(str);;
			}
		}
	};
	
	private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if(group.getTag() != null) {
				group.setTag(null);
				return;
			}
			
			byte aromaRate = 0;
			if(checkedId == R.id.rb_fast){
				aromaRate = INoxManager.AromaSpeed.FAST.getValue();
			}else if(checkedId == R.id.rb_mid){
				aromaRate = INoxManager.AromaSpeed.COMMON.getValue();
			}else if(checkedId == R.id.rb_slow){
				aromaRate = INoxManager.AromaSpeed.SLOW.getValue();
			}else if(checkedId == R.id.rb_close){
				aromaRate = 0;
			}
		}
	};


	protected void initUI() {
		// TODO Auto-generated method stub
//		aidStopDuration = 45;
		
		aidDurationItems = new Object[60];
		for(int i=1;i<=aidDurationItems.length;i++) {
			aidDurationItems[i - 1] = i;
		}
		
		loopModeItems = new Object[3];
		loopModeItems[0] = getString(R.string.loop_order);
		loopModeItems[1] = getString(R.string.loop_random);
		loopModeItems[2] = getString(R.string.loop_single);
		
		valueDialog = new SelectValueDialog(mActivity);
		valueDialog.setValueSelectedListener(valueSelectedListener);
        
		mActivity.setTitle(R.string.device);
		if(music.getMusicID() == 0) { //首次设置默认音乐
			music.setMusicID(DemoApp.SLEEPAID_MUSIC[0][0]);
		}
		music.setMusicName(getString(Utils.getSleepAidMusicName(music.getMusicID())));
		initMusicView();
		initLoopMode();
		etVolume.setText(String.valueOf(vol));
		etR.setText(String.valueOf(cR & 0xff));
		etG.setText(String.valueOf(cG & 0xff));
		etB.setText(String.valueOf(cB & 0xff));
		etW.setText(String.valueOf(cW & 0xff));
		etBrightness.setText(String.valueOf(bri & 0xff));
		initMusicButtonStatus();
		initSleepAidDurationView();
		rgAroma.setTag("ok");
		rgAroma.check(R.id.rb_mid);
	}
	
	private void initLoopMode() {
		if(loopMode == 0) {
			tvLoopMode.setText(R.string.loop_order);
		}else if(loopMode == 1) {
			tvLoopMode.setText(R.string.loop_random);
		}else if(loopMode == 2) {
			tvLoopMode.setText(R.string.loop_single);
		}
	}
	
	private ValueSelectedListener valueSelectedListener = new ValueSelectedListener() {
		@Override
		public void onValueSelected(SelectValueDialog dialog, int type, int index, Object value) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onValueSelected type:" + type+",idx:" + index+",val:" + value);
			if(type == SelectValueDialog.TYPE_AID_DURATION) {
				aidStopDuration = (byte)((int)((Integer)value));
				initSleepAidDurationView();
			}else if(type == SelectValueDialog.TYPE_LOOP_MODE) {
				loopMode = (byte) index;
				initLoopMode();
			}
		}
	};
	
	private void initMusicView() {
		tvMusic.setText(music.getMusicName());
	}
	
	private void initSleepAidDurationView() {
		String str = Utils.getDuration(mActivity, aidStopDuration);
		SdkLog.log(TAG+" initSleepAidDurationView str:" + str+",duration:" + aidStopDuration);
		tvSleepAidTimeValue.setText(str);
		tvSleepAidTips.setText(getString(R.string.music_aroma_light_close2, String.valueOf(aidStopDuration)));
	}
	
	private void initMusicButtonStatus() {
		if(playing) {
			btnPlayMusic.setText(R.string.pause);
		}else {
			btnPlayMusic.setText(R.string.play);
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isConnected = getDeviceHelper().isConnected();
		initPageState(isConnected);
	}
	
	private void initPageState(boolean isConnected) {
//		isConnected = true;
		initBtnConnectState(isConnected);
		setPageEnable(isConnected);
	}
	
	private void initBtnConnectState(boolean isConnected) {
		btnSendVolume.setEnabled(isConnected);
		btnPlayMusic.setEnabled(isConnected);
		btnSendColor.setEnabled(isConnected);
		btnSendBrightness.setEnabled(isConnected);
		btnCloseLight.setEnabled(isConnected);
		btnSave.setEnabled(isConnected);
		Utils.setRadioGroupEnable(rgAroma, isConnected);
	}
	
	private void setPageEnable(boolean enable){
		if(enable) {
			maskView.setVisibility(View.GONE);
		}else {
			maskView.setVisibility(View.VISIBLE);
		}
	}
	
	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager, final CONNECTION_STATE state) {
			// TODO Auto-generated method stub
			
			if(!isAdded()){
				return;
			}
			
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					initPageState(state == CONNECTION_STATE.CONNECTED);
					
					if(state == CONNECTION_STATE.DISCONNECT){
						
					}else if(state == CONNECTION_STATE.CONNECTED){
						
					}
				}
			});
		}
	};
	

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		getDeviceHelper().removeConnectionStateCallback(stateCallback);
		unregisterTouchListener(touchListener);
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == tvMusic) {
			Intent intent = new Intent(mActivity, DataListActivity.class);
			intent.putExtra("dataType", DataListActivity.DATA_TYPE_SLEEPAID_MUSIC);
			intent.putExtra("musicId", music.getMusicID());
			startActivityForResult(intent, 100);
		} else if(v == tvLoopMode) {
			valueDialog.initData(SelectValueDialog.TYPE_LOOP_MODE, getString(R.string.cancel), "", getString(R.string.confirm), null, loopModeItems);
			valueDialog.setDefaultIndex(loopMode);
			valueDialog.show();
		} else if(v == vSleepAidTime) {
			valueDialog.initData(SelectValueDialog.TYPE_AID_DURATION, getString(R.string.cancel), getString(R.string.sa_last_time), getString(R.string.confirm), null, aidDurationItems);
			valueDialog.setDefaultIndex(aidStopDuration - 1);
			valueDialog.show();
		}else if(v == btnSendColor) {
			if(Utils.inputTips(etR, 255)) {
				return;
			}
			if(Utils.inputTips(etG, 120)) {
				return;
			}
			if(Utils.inputTips(etB, 255)) {
				return;
			}
			if(Utils.inputTips(etW, 255)) {
				return;
			}
			if(Utils.inputTips(etBrightness, 100)) {
				return;
			}
			
			String strR = etR.getText().toString();
			String strG = etG.getText().toString();
			String strB = etB.getText().toString();
			String strW = etW.getText().toString();
			
			cR = (byte)(int)Integer.valueOf(strR);
			cG = (byte)(int)Integer.valueOf(strG);
			cB = (byte)(int)Integer.valueOf(strB);
			cW = (byte)(int)Integer.valueOf(strW);
			
			String strBrightness = etBrightness.getText().toString();
			bri = (byte)(int)Integer.valueOf(strBrightness);
			SdkLog.log(TAG+" send color r:" + strR+",g:" + strG+",b:" +strB+",w:" + strW);
			
			SLPLight light = new SLPLight();
			light.setR(cR);
			light.setG(cG);
			light.setB(cB);
			light.setW(cW);
			
			getDeviceHelper().turnOnSleepAidLight(light, bri, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
					if(playing){
						playMusic();
					}
					
				}
			});
			
		}else if(v == btnSendBrightness) {
			if(Utils.inputTips(etBrightness, 100)) {
				return;
			}
			
			String strBrightness = etBrightness.getText().toString();
			bri = (byte)(int)Integer.valueOf(strBrightness);
			getDeviceHelper().setSleepAidLightBrightness(bri, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
			
		}else if(v == btnCloseLight) {
			getDeviceHelper().turnOffLight(3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}else if(v == btnSave) {
			
			if(Utils.inputTips(etVolume, 16)) {
				return;
			}
			
			if(Utils.inputTips(etR, 255)) {
				return;
			}
			if(Utils.inputTips(etG, 120)) {
				return;
			}
			if(Utils.inputTips(etB, 255)) {
				return;
			}
			if(Utils.inputTips(etW, 255)) {
				return;
			}
			
			if(Utils.inputTips(etBrightness, 100)) {
				return;
			}
			
			String strVolume = etVolume.getText().toString();
			vol = (byte)(int)Integer.valueOf(strVolume);
			
			String strR = etR.getText().toString();
			String strG = etG.getText().toString();
			String strB = etB.getText().toString();
			String strW = etW.getText().toString();
			String strBrightness = etBrightness.getText().toString();
			
			cR = (byte)(int)Integer.valueOf(strR);
			cG = (byte)(int)Integer.valueOf(strG);
			cB = (byte)(int)Integer.valueOf(strB);
			cW = (byte)(int)Integer.valueOf(strW);
			bri = (byte)(int)Integer.valueOf(strBrightness);
			
			SdkLog.log(TAG+" save config vol:" + vol+", brightness:" + bri+",r:" + cR + ",g:" + cG +",b:" +cB+",w:" + cW+",duration:" + aidStopDuration);
			
			mActivity.showLoading();
			getDeviceHelper().sleepAidConfig(vol, bri, cR, cG, cB, cW, aidStopDuration, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					if(!isAdded()){
						return;
					}
					
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mActivity.hideLoading();
							if(cd.isSuccess()) {
								Toast.makeText(mActivity, R.string.save_succeed, Toast.LENGTH_SHORT).show();
							}else {
								mActivity.showErrTips(cd);
							}
						}
					});
				}
			});
		}
		
		
		else if(v == btnSendVolume) {
			
			if(Utils.inputTips(etVolume, 16)) {
				return;
			}
			
			String strVolume = etVolume.getText().toString();
			vol = (byte)(int)Integer.valueOf(strVolume);
			
			getDeviceHelper().setSleepAidMusicVolume(vol, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		
		else if(v == btnPlayMusic) {
			
			if(!playing) {
				playMusic();
			}else {
				getDeviceHelper().turnOffSleepAidMusic(3000, new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(cd.isSuccess()) {
									playing = false;
									initMusicButtonStatus();
								}
							}
						});
					}
				});
			}
		}
	}
	
	
	private void playMusic() {
		
		if(Utils.inputTips(etVolume, 16)) {
			return;
		}
		
		String strVolume = etVolume.getText().toString();
		vol = (byte)(int)Integer.valueOf(strVolume);
		
		getDeviceHelper().turnOnSleepAidMusic(music.getMusicID(), vol, loopMode, 3000, new IResultCallback() {
			@Override
			public void onResultCallback(final CallbackData cd) {
				// TODO Auto-generated method stub
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(cd.isSuccess()) {
							playing = true;
							initMusicButtonStatus();
						}
					}
				});
			}
		});
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		SdkLog.log(TAG+" onActivityResult req:" + requestCode+",res:" + resultCode+",data:" + data);
		if(requestCode == 100 && resultCode == Activity.RESULT_OK) {
			int musicId = data.getIntExtra("musicId", 0);
			music.setMusicID(musicId);
			music.setMusicName(getString(Utils.getSleepAidMusicName(musicId)));
			initMusicView();
			if(playing) {
				playMusic();
			}
		}
	}
	
	
//	@Override
//	public void onDetach() {
//		super.onDetach();
//		try {
//			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//			childFragmentManager.setAccessible(true);
//			childFragmentManager.set(this, null);
//		} catch (NoSuchFieldException e) {
//			throw new RuntimeException(e);
//		} catch (IllegalAccessException e) {
//			throw new RuntimeException(e);
//		}
//	}
	
}










