package com.sdk902b.demo.fragment;

import com.sdk902b.demo.BaseActivity.MyOnTouchListener;
import com.sdk902b.demo.R;
import com.sdk902b.demo.util.ActivityUtil;
import com.sdk902b.demo.util.Utils;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IMonitorManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.SdkLog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class LightFragment extends BaseFragment {
	private View maskView;
	private EditText etR, etG, etB, etW, etBrightness;
	private Button btnSendColor, btnSendBrightness, btnCloseLight;
	private static byte cR = (byte)155, cG = 32, cB = 93, cW = (byte)255, bri = 100;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_light, null);
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
		etR = (EditText) root.findViewById(R.id.et_r);
		etG = (EditText) root.findViewById(R.id.et_g); 
		etB = (EditText) root.findViewById(R.id.et_b); 
		etW = (EditText) root.findViewById(R.id.et_w);
		etBrightness = (EditText) root.findViewById(R.id.et_brightness);
		btnSendColor = (Button) root.findViewById(R.id.btn_w);
		btnSendBrightness = (Button) root.findViewById(R.id.btn_brightness);
		btnCloseLight = (Button) root.findViewById(R.id.btn_close_light);
	}


	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getDeviceHelper().addConnectionStateCallback(stateCallback);
		btnSendColor.setOnClickListener(this);
		btnSendBrightness.setOnClickListener(this);
		btnCloseLight.setOnClickListener(this);
		etR.addTextChangedListener(new RGBWWatcher(etR));
		etG.addTextChangedListener(new RGBWWatcher(etG));
		etB.addTextChangedListener(new RGBWWatcher(etB));
		etW.addTextChangedListener(new RGBWWatcher(etW));
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
	
	private class RGBWWatcher implements TextWatcher {
		private EditText et;
		RGBWWatcher(EditText et){
			this.et = et;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String str = s.toString().trim();
			if(!TextUtils.isEmpty(str) && !Utils.inputTips(et, 255)) {
				if(et == etR) {
					cR = (byte)(int)Integer.valueOf(str);
				}else if(et == etG) {
					cG = (byte)(int)Integer.valueOf(str);
				}else if(et == etB) {
					cB = (byte)(int)Integer.valueOf(str);
				}else if(et == etW) {
					cW = (byte)(int)Integer.valueOf(str);
				}
			}
		}
		
	}
	
	
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
			String str = s.toString().trim();
			if(!TextUtils.isEmpty(str) && !Utils.inputTips(etBrightness, 100)) {
				bri = (byte)(int)Integer.valueOf(str);
			}
		}
	};

	protected void initUI() {
		// TODO Auto-generated method stub
		etR.setText(String.valueOf(cR & 0xff));
		etG.setText(String.valueOf(cG & 0xff));
		etB.setText(String.valueOf(cB & 0xff));
		etW.setText(String.valueOf(cW & 0xff));
		etBrightness.setText(String.valueOf(bri));
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
		btnSendColor.setEnabled(isConnected);
		btnSendBrightness.setEnabled(isConnected);
		btnCloseLight.setEnabled(isConnected);
	}
	
	private void setPageEnable(boolean enable){
		if(enable) {
			maskView.setVisibility(View.GONE);
		}else {
			maskView.setVisibility(View.VISIBLE);
		}
	}
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
//			AlarmSettingActivity.alarmConfig.setEnable(isChecked);
//			getBinatoneHelper().setAlarm(AlarmSettingActivity.alarmConfig.isEnable(), AlarmSettingActivity.alarmConfig.getHour(), AlarmSettingActivity.alarmConfig.getMinute(), AlarmSettingActivity.alarmConfig.getDuration(), 3000, setAlarmCallback);
		}
	};
	
	private IResultCallback<Void> setAlarmCallback = new IResultCallback<Void>() {
		@Override
		public void onResultCallback(CallbackData<Void> cd) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onResultCallback " + cd);
			if(cd.getCallbackType() == IMonitorManager.METHOD_ALARM_SET) {
				if(cd.isSuccess()) {
					
				}else {
					
				}
			}
		}
	};
	
//	private IResultCallback<AlarmConfig> getAlarmCallback = new IResultCallback<AlarmConfig>() {
//		@Override
//		public void onResultCallback(CallbackData<AlarmConfig> cd) {
//			// TODO Auto-generated method stub
//			if(!isAdded()) {
//				return;
//			}
//			SdkLog.log(TAG+" onResultCallback " + cd);
//			if(cd.getCallbackType() == IMonitorManager.METHOD_ALARM_GET) {
//				if(cd.isSuccess()) {
//					AlarmSettingActivity.alarmConfig = cd.getResult();
//					mActivity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							cbAlarmSwitch.setChecked(AlarmSettingActivity.alarmConfig.isEnable());
//						}
//					});
//				}else {
//					
//				}
//			}
//		}
//	};
	
	
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
		if(v == btnSendColor) {
			
			if(Utils.inputTips(etR, 255)) {
				return;
			}
			if(Utils.inputTips(etG, 255)) {
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
			
			String strBrightness = etBrightness.getText().toString();
			bri = (byte)(int)Integer.valueOf(strBrightness);
			
			byte r = (byte)(int)Integer.valueOf(strR);
			byte g = (byte)(int)Integer.valueOf(strG);
			byte b = (byte)(int)Integer.valueOf(strB);
			byte w = (byte)(int)Integer.valueOf(strW);
			
			SLPLight light = new SLPLight();
			light.setR(r);
			light.setG(g);
			light.setB(b);
			light.setW(w);
			
			cR = r;
			cG = g;
			cB = b;
			cW = w;
			
			getDeviceHelper().turnOnColorLight(light, bri, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}else if(v == btnSendBrightness) {
			
			if(Utils.inputTips(etBrightness, 100)) {
				return;
			}
			
			String strBrightness = etBrightness.getText().toString();
			bri = (byte)(int)Integer.valueOf(strBrightness);
			
			getDeviceHelper().lightBrightness(bri, 3000, new IResultCallback() {
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










