package com.sdk902b.demo.fragment;

import java.lang.reflect.Field;

import com.sdk902b.demo.AlarmListActivity;
import com.sdk902b.demo.FloatStopActivity;
import com.sdk902b.demo.GestureSettingActivity;
import com.sdk902b.demo.R;
import com.sdk902b.demo.SmallNightLightActivity;
import com.sdk902b.demo.TimerTaskListActivity;
import com.sdk902b.demo.util.ActivityUtil;
import com.sdk902b.demo.view.SelectValueDialog;
import com.sdk902b.demo.view.SelectValueDialog.ValueSelectedListener;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SettingFragment extends BaseFragment {
	private View maskView;
	private View vAlarm, mTvSmallLight, vTimerTask, vDelayClose, mTvGesture, mTvFloatStop;
	private TextView tvDelayClose;
	private Button btnRestore;
	private SelectValueDialog valueDialog;
	/**
	 * 临时的延时关闭时间
	 * 正式时间[0, 30, 60, 90, ... , 360]
	 * 临时时间[0, 1, 2, 3, 4, ... , 12] 临时时间短，方便测试
	 */
	private boolean tempDelayCloseTime = false;

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
		vTimerTask = root.findViewById(R.id.tv_timer_task);
		vDelayClose = root.findViewById(R.id.layout_delay_close);
		tvDelayClose = root.findViewById(R.id.tv_delay_close);
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
		vTimerTask.setOnClickListener(this);
		vDelayClose.setOnClickListener(this);
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
		
		if(isConnected) {
			getDeviceHelper().getDelayCloseInfo(3000, new IResultCallback<Short>() {
				@Override
				public void onResultCallback(final CallbackData<Short> cd) {
					// TODO Auto-generated method stub
					if(!isAdded() || !ActivityUtil.isActivityAlive(mActivity)) {
						return;
					}
					
					mActivity.runOnUiThread(new Runnable() {
						public void run() {
							if(cd.isSuccess()) {
								short delayTime = cd.getResult(); //单位分钟
								int idx = 0;
								if(tempDelayCloseTime) {
									idx = delayTime;
								}else {
									idx = delayTime / 30;
								}
								valueDialog.setDefaultIndex(idx);
								initDelayCloseView(delayTime);
							}
						}
					});
				}
			});
		}
	}
	
	private void initPageState(boolean isConnected) {
//		isConnected = true;
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
		
		Object[] items = new Object[13];
		items[0] = getString(R.string.delay_close_off);
		if(tempDelayCloseTime) {
			for(int i=1;i<items.length;i++) {
				items[i] = String.valueOf(i);
			}
		}else {
			for(int i=1;i<items.length;i++) {
				if(i % 2 == 0) {
					items[i] = String.valueOf((int)(0.5 * i));
				}else {
					items[i] = String.valueOf(0.5 * i);
				}
			}
		}
		valueDialog = new SelectValueDialog(mActivity);
		if(tempDelayCloseTime) {
			valueDialog.initData(SelectValueDialog.TYPE_DELAY_CLOSE, getString(R.string.cancel), getString(R.string.delay_close), getString(R.string.confirm), getString(R.string.min), items);
		}else {
			valueDialog.initData(SelectValueDialog.TYPE_DELAY_CLOSE, getString(R.string.cancel), getString(R.string.delay_close), getString(R.string.confirm), getString(R.string.hour), items);
		}
		valueDialog.setDefaultIndex(0);
		valueDialog.setValueSelectedListener(valueSelectedListener);
	}
	
	private ValueSelectedListener valueSelectedListener = new ValueSelectedListener() {
		@Override
		public void onValueSelected(SelectValueDialog dialog, int type, int index, Object value) {
			// TODO Auto-generated method stub
			if(type == SelectValueDialog.TYPE_DELAY_CLOSE) {
				short delayTime = 0;
				if(tempDelayCloseTime) {
					delayTime = (short)index;
				}else {
					delayTime = (short) (index * 30);
				}
				initDelayCloseView(delayTime);
				getDeviceHelper().delayCloseTimeConfig(delayTime, 3000, new IResultCallback() {
					@Override
					public void onResultCallback(CallbackData arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		}
	};
	
	private void initDelayCloseView(int delayTime) {
		if(delayTime == 0) {
			tvDelayClose.setText(R.string.close1);
		}else {
			tvDelayClose.setText(delayTime + getString(R.string.min));
		}
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
			startActivity(AlarmListActivity.class);
		}else if(v == mTvSmallLight) {
			startActivity(SmallNightLightActivity.class);
		}else if(v == vTimerTask) {
			startActivity(TimerTaskListActivity.class);
		}else if(v == vDelayClose) {
			valueDialog.show();
		}else if(v == mTvGesture) {
			startActivity(GestureSettingActivity.class);
		}else if (v == btnRestore) {
			getDeviceHelper().deviceInit(3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					
				}
			});
		}else if(v==mTvFloatStop){
			startActivity(FloatStopActivity.class);
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
