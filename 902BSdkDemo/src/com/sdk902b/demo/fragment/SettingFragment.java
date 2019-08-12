package com.sdk902b.demo.fragment;

import java.lang.reflect.Field;

import com.sdk902b.demo.AlarmListActivity;
import com.sdk902b.demo.EditAlarmActivity;
import com.sdk902b.demo.FloatStopActivity;
import com.sdk902b.demo.GestureSettingActivity;
import com.sdk902b.demo.LightSet2Activity;
import com.sdk902b.demo.R;
import com.sdk902b.demo.SmallNightLightActivity;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingFragment extends BaseFragment {
	private View maskView;
	private View vAlarm, mTvSmallLight, mTvGesture,mTvFloatStop;
	private Button btnRestore;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_setting, null);
		// LogUtil.log(TAG + " onCreateView-----------");
		findView(view);
		initListener();
		initUI();
		return view;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		maskView = root.findViewById(R.id.mask);
		vAlarm = root.findViewById(R.id.tv_alarm);
		mTvSmallLight = root.findViewById(R.id.tv_small_light);
		mTvGesture = root.findViewById(R.id.tv_gesture);
		mTvFloatStop = root.findViewById(R.id.tv_float_stop);
		btnRestore = (Button) root.findViewById(R.id.btn_restore);
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getDeviceHelper().addConnectionStateCallback(stateCallback);
		vAlarm.setOnClickListener(this);
		mTvSmallLight.setOnClickListener(this);
		mTvGesture.setOnClickListener(this);
		btnRestore.setOnClickListener(this);
		mTvFloatStop.setOnClickListener(this);
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
		btnRestore.setEnabled(isConnected);
	}
	
	private void setPageEnable(boolean enable){
		if(enable) {
			maskView.setVisibility(View.GONE);
		}else {
			maskView.setVisibility(View.VISIBLE);
		}
	}

	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.setting);
	}

	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager, final CONNECTION_STATE state) {
			// TODO Auto-generated method stub

			if (!isAdded()) {
				return;
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					btnRestore.setEnabled(state == CONNECTION_STATE.CONNECTED);
				}
			});
		}
	};

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		getDeviceHelper().removeConnectionStateCallback(stateCallback);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == vAlarm) {
		
			Intent intent = new Intent(mActivity, EditAlarmActivity.class);
			intent.putExtra("action", "add");
			startActivity(intent);
		}else if(v == mTvSmallLight) {
			Intent intent = new Intent(mActivity, SmallNightLightActivity.class);
			startActivity(intent);
		}else if(v == mTvGesture) {
			Intent intent = new Intent(mActivity, GestureSettingActivity.class);
			startActivity(intent);
		}else if (v == btnRestore) {
			getDeviceHelper().deviceInit(3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}else if(v==mTvFloatStop){
			Intent intent = new Intent(mActivity, FloatStopActivity.class);
			startActivity(intent);
			
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
