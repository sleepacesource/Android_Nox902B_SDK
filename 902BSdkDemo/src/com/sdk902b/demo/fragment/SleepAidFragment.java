package com.sdk902b.demo.fragment;

import com.sdk902b.demo.DataListActivity;
import com.sdk902b.demo.DemoApp;
import com.sdk902b.demo.R;
import com.sdk902b.demo.BaseActivity.MyOnTouchListener;
import com.sdk902b.demo.bean.MusicInfo;
import com.sdk902b.demo.util.ActivityUtil;
import com.sdk902b.demo.util.LogUtil;
import com.sdk902b.demo.util.Utils;
import com.sdk902b.demo.view.SelectValueDialog;
import com.sdk902b.demo.view.SelectValueDialog.ValueSelectedListener;
import com.sleepace.sdk.core.nox.domain.BleNoxAidInfo;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.core.nox.interfs.INoxManager;
import com.sleepace.sdk.core.nox.interfs.ISleepAidManager;
import com.sleepace.sdk.core.nox.interfs.INoxManager.AromaSpeed;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;

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
	private TextView tvMusic, tvSleepAidTimeValue, tvSleepAidTips;
	private View vSleepAidTime;
	private EditText etVolume, etR, etG, etB, etW, etBrightness;
	private Button btnSendVolume, btnPlayMusic, btnSendColor, btnSendBrightness, btnCloseLight, btnSave;
	private RadioGroup rgAroma;
	
	private SelectValueDialog valueDialog;
	private short aidStopDuration = 0;
	private MusicInfo music = new MusicInfo();
	
	private boolean playing;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_sleepaid, null);
//		LogUtil.log(TAG+" onCreateView-----------");
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
			if(!TextUtils.isEmpty(str)) {
				Utils.inputTips(etVolume, 16);
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
			if(!TextUtils.isEmpty(str)) {
				Utils.inputTips(etG, 120);
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
			if(!TextUtils.isEmpty(str)) {
				Utils.inputTips(etBrightness, 100);
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
		aidStopDuration = 1;
		
		int[] data = new int[45];
		for(int i=1;i<=data.length;i++) {
			data[i - 1] = i;
		}
		
		valueDialog = new SelectValueDialog(mActivity, data);
		valueDialog.setValueSelectedListener(valueSelectedListener);
        
		mActivity.setTitle(R.string.device);
		music.setMusicID(DemoApp.SLEEPAID_MUSIC[0][0]);
		music.setMusicName(getString(Utils.getSleepAidMusicName(music.getMusicID())));
		initMusicView();
		
//		etVolume.setText("10");
		etR.setText("255");
//		etG.setText("120");
		etB.setText("0");
		etW.setText("0");
//		etBrightness.setText("38");
		initMusicButtonStatus();
		initSleepAidDurationView();
		rgAroma.setTag("ok");
		rgAroma.check(R.id.rb_mid);
	}
	
	private ValueSelectedListener valueSelectedListener = new ValueSelectedListener() {
		@Override
		public void onValueSelected(SelectValueDialog dialog, byte value) {
			// TODO Auto-generated method stub
			LogUtil.log(TAG+" onValueSelected val:" + value);
			aidStopDuration = value;
			initSleepAidDurationView();
		}
	};
	
	private void initMusicView() {
		tvMusic.setText(music.getMusicName());
	}
	
	private void initSleepAidDurationView() {
		String str = Utils.getDuration(mActivity, aidStopDuration);
		LogUtil.log(TAG+" initSleepAidDurationView str:" + str+",duration:" + aidStopDuration);
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
		} else if(v == vSleepAidTime) {
			valueDialog.setLabel(getString(R.string.cancel), getString(R.string.sa_last_time), getString(R.string.confirm), null);
			valueDialog.setDefaultValue((byte) aidStopDuration);
			valueDialog.show();
		}
		
		else if(v == btnSendColor) {
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
			
			String strR = etR.getText().toString();
			String strG = etG.getText().toString();
			String strB = etB.getText().toString();
			String strW = etW.getText().toString();
			
			byte brightness = 50;
			String strBrightness = etBrightness.getText().toString();
			if(!TextUtils.isEmpty(strBrightness)) {
				brightness = (byte)(int)Integer.valueOf(strBrightness);
			}
			
			byte r = (byte)(int)Integer.valueOf(strR);
			byte g = (byte)(int)Integer.valueOf(strG);
			byte b = (byte)(int)Integer.valueOf(strB);
			byte w = (byte)(int)Integer.valueOf(strW);
			LogUtil.log(TAG+" send color r:" + strR+",g:" + strG+",b:" +strB+",w:" + strW);
			
			SLPLight light = new SLPLight();
			light.setR(r);
			light.setG(g);
			light.setB(b);
			light.setW(w);
			
			getDeviceHelper().turnOnSleepAidLight(light, brightness, 3000, new IResultCallback() {
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
			byte brightness = (byte)(int)Integer.valueOf(strBrightness);
			getDeviceHelper().setSleepAidLightBrightness(brightness, 3000, new IResultCallback() {
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
			byte volume = (byte)(int)Integer.valueOf(strVolume);
			
			String strR = etR.getText().toString();
			String strG = etG.getText().toString();
			String strB = etB.getText().toString();
			String strW = etW.getText().toString();
			String strBrightness = etBrightness.getText().toString();
			
			byte r = (byte)(int)Integer.valueOf(strR);
			byte g = (byte)(int)Integer.valueOf(strG);
			byte b = (byte)(int)Integer.valueOf(strB);
			byte w = (byte)(int)Integer.valueOf(strW);
			byte brightness = (byte)(int)Integer.valueOf(strBrightness);
			
			mActivity.showLoading();
			getDeviceHelper().sleepAidConfig(volume, brightness, r, g, b, w, aidStopDuration, 3000, new IResultCallback() {
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
			byte volume = (byte)(int)Integer.valueOf(strVolume);
			
			getDeviceHelper().setSleepAidMusicVolume(volume, 3000, new IResultCallback() {
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
		byte volume = (byte)(int)Integer.valueOf(strVolume);
		
		getDeviceHelper().turnOnMusic((byte)0,music.getMusicID(), volume, (byte)2, 3000, new IResultCallback() {
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










