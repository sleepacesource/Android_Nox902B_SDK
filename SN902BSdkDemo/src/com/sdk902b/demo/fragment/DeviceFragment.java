package com.sdk902b.demo.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import com.sdk902b.demo.DemoApp;
import com.sdk902b.demo.MainActivity;
import com.sdk902b.demo.R;
import com.sdk902b.demo.SearchBleDeviceActivity;
import com.sleepace.sdk.core.nox.domain.BleNoxDeviceInfo;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.core.nox.interfs.INoxManager;
import com.sleepace.sdk.core.nox.interfs.ISleepAidManager;
import com.sleepace.sdk.core.nox.util.Constants;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.ble.BleHelper;
import com.sleepace.sdk.util.SdkLog;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceFragment extends BaseFragment {
	private Button btnConnectDevice, btnDeviceName, btnDeviceId, btnVersion, btnStartSleepAid, btnStopSleepAid, btnUpgrade;
	private TextView tvDeviceName, tvDeviceId, tvVersion;
	private boolean upgrading = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_device, null);
		// LogUtil.log(TAG+" onCreateView-----------");
		findView(root);
		initListener();
		initUI();
		return root;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		tvDeviceName = (TextView) root.findViewById(R.id.tv_device_name);
		tvDeviceId = (TextView) root.findViewById(R.id.tv_device_id);
		tvVersion = (TextView) root.findViewById(R.id.tv_device_version);
		btnConnectDevice = (Button) root.findViewById(R.id.btn_connect_device);
		btnDeviceName = (Button) root.findViewById(R.id.btn_get_device_name);
		btnDeviceId = (Button) root.findViewById(R.id.btn_get_device_id);
		btnVersion = (Button) root.findViewById(R.id.btn_device_version);
		btnStartSleepAid = (Button) root.findViewById(R.id.btn_start_sleepaid);
		btnStopSleepAid = (Button) root.findViewById(R.id.btn_stop_sleepaid);
		btnUpgrade = (Button) root.findViewById(R.id.btn_upgrade_fireware);
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getDeviceHelper().addConnectionStateCallback(stateCallback);
		btnConnectDevice.setOnClickListener(this);
		btnDeviceName.setOnClickListener(this);
		btnDeviceId.setOnClickListener(this);
		btnVersion.setOnClickListener(this);
		btnStartSleepAid.setOnClickListener(this);
		btnStopSleepAid.setOnClickListener(this);
		btnUpgrade.setOnClickListener(this);
	}

	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.device);
		tvDeviceName.setText(MainActivity.deviceName);
		tvDeviceId.setText(MainActivity.deviceId);
		tvVersion.setText(MainActivity.version);
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
		if (!isConnected) {
			tvDeviceName.setText(null);
			tvDeviceId.setText(null);
			tvVersion.setText(null);
		}
	}

	private void initBtnConnectState(boolean isConnected) {
		if (isConnected) {
			btnConnectDevice.setText(R.string.disconnect);
			btnConnectDevice.setTag("disconnect");
		} else {
			btnConnectDevice.setText(R.string.connect_device);
			btnConnectDevice.setTag("connect");
		}
	}

	private void setPageEnable(boolean enable) {
		btnDeviceName.setEnabled(enable);
		btnDeviceId.setEnabled(enable);
		btnVersion.setEnabled(enable);
		btnUpgrade.setEnabled(enable);
		btnStartSleepAid.setEnabled(enable);
		btnStopSleepAid.setEnabled(enable);
	}

	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager,
				final CONNECTION_STATE state) {
			// TODO Auto-generated method stub

			if (!isAdded()) {
				return;
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					initPageState(state == CONNECTION_STATE.CONNECTED);

					if (state == CONNECTION_STATE.DISCONNECT) {

						if (upgrading) {
							upgrading = false;
							mActivity.hideUpgradeDialog();
							mActivity.setUpgradeProgress(0);
						}

					} else if (state == CONNECTION_STATE.CONNECTED) {

						if (upgrading) {
							upgrading = false;
							btnUpgrade.setEnabled(true);
							mActivity.hideUpgradeDialog();
						}
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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == btnConnectDevice) {
			Object tag = v.getTag();
			if (tag == null || "connect".equals(tag)) {
				if (!BleHelper.getInstance(mActivity).isBluetoothOpen()) {
					Intent enabler = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enabler, BleHelper.REQCODE_OPEN_BT);
				} else {
					Intent intent = new Intent(mActivity,
							SearchBleDeviceActivity.class);
					startActivity(intent);
				}
			} else {// 断开设备
				getDeviceHelper().disconnect();
			}
		}else if (v == btnUpgrade) {
	
			final File file = new File(mActivity.getExternalFilesDir(null), DemoApp.FIRMWARE_NAME);			
			if (!file.exists()) {
				return;
			}

			btnUpgrade.setEnabled(false);

			mActivity.setUpgradeProgress(0);
			mActivity.showUpgradeDialog();
			upgrading=true;
			new Thread(new Runnable() {
				@Override
				public void run() {			
					// TODO Auto-generated method stub
					getDeviceHelper().deviceUpgrade(file,new IResultCallback<Integer>() {
						@Override
						public void onResultCallback(
								final CallbackData<Integer> cd) {
							// TODO Auto-generated method stub
							// LogUtil.log(TAG+" upgradeDevice " + cd);			
							mActivity.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if(cd.isSuccess()){
										int  progress= cd.getResult();
										//upgradeDialog.setProgress(progress);
										mActivity.setUpgradeProgress(progress);
										if(progress == 100){
											//upgradeDialog.dismiss();
											mActivity.hideUpgradeDialog();
											Toast.makeText(mActivity,
													R.string.up_success,
													5000).show();
											getDeviceHelper().disconnect();
										}
										
									}else{
										//upgradeDialog.dismiss();
										mActivity.hideUpgradeDialog();
										Toast.makeText(mActivity,
												R.string.up_failed,
												5000).show();
										btnUpgrade.setEnabled(getDeviceHelper()
										.isConnected());
									}
								}
							});

						}
					});
				}
			}).start();
	

		} else if (v == btnDeviceName) {
			tvDeviceName.setText(MainActivity.deviceName);
		} else if (v == btnDeviceId) {
			tvDeviceId.setText(MainActivity.deviceId);
		} else if (v == btnVersion) {
			tvVersion.setText(MainActivity.version);
			SdkLog.log(TAG+" get version-----");
			getDeviceHelper().getDeviceInfo(3000, new IResultCallback<BleNoxDeviceInfo>() {
				@Override
				public void onResultCallback(final CallbackData<BleNoxDeviceInfo> cd) {
					// TODO Auto-generated method stub
					SdkLog.log(TAG+" getDeviceInfo-----" + cd);
					if(cd.isSuccess()) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								BleNoxDeviceInfo deviceInfo = cd.getResult();
								tvVersion.setText(deviceInfo.getFirmwareVersion());
							}
						});
					}
				}
			});
		}else if(v == btnStartSleepAid) {
			getDeviceHelper().sleepAidControl((byte)0, (byte)1, (byte)1, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					SdkLog.log(TAG+" start sleepAid-----" + cd);
				}
			});
		}else if(v == btnStopSleepAid) {
			getDeviceHelper().sleepAidControl((byte)2, (byte)0, (byte)0, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					SdkLog.log(TAG+" stop sleepAid-----" + cd);
				}
			});
		}
	}

	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
