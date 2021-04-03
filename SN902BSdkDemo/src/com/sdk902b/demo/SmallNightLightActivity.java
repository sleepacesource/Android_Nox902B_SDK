package com.sdk902b.demo;

import java.util.Calendar;

import com.sdk902b.demo.util.ActivityUtil;
import com.sleepace.sdk.core.nox.domain.BleNoxNightLightInfo;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.SdkLog;
import com.sleepace.sdk.util.StringUtil;
import com.sleepace.sdk.util.TimeUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SmallNightLightActivity extends BaseActivity {

	private CheckBox mCheckBox;
	private TextView mTvLightSetting;
	private RelativeLayout mLayoutTimeSetting;
	private BleNoxNightLightInfo bleNoxLight;
	private LinearLayout mLayoutLightSet;
	private TextView mTvTime;
	public static final int REQUEST_CODE_GET_LIGHT = 1000;
	public static final int REQUEST_CODE_GET_TIME = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_small_light);
		findView();
		initListener();
		initUI();
		
		showLoading();
		mHelper.nigthLightConfigGet(3000, new IResultCallback<BleNoxNightLightInfo>() {
			@Override
			public void onResultCallback(final CallbackData<BleNoxNightLightInfo> cd) {
				// TODO Auto-generated method stub
				if (!ActivityUtil.isActivityAlive(mActivity)) {
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						hideLoading();
						if(cd.isSuccess()) {
							bleNoxLight = cd.getResult();
							SLPLight light = bleNoxLight.getLight();
							//出厂时，设置小夜灯默认值
							if(!bleNoxLight.isEnable() && light.getR() == 0 && light.getG() == 0 && light.getB() == 0 && light.getW() == 0) {
								startHour = 23; 
								startMin = 0; 
								endHour = 6; 
								endMin = 0;
								bleNoxLight.setBrightness((byte) 38);
								bleNoxLight.setDuration(getContinueTime(startHour, startMin, endHour, endMin));
								light.setR((byte) 255);
								light.setG((byte) 35);
								light.setB((byte) 0);
								light.setW((byte) 0);
								bleNoxLight.setLight(light);
								bleNoxLight.setStartHour(startHour);
								bleNoxLight.setStartMinute(startMin);
							}
							
							startHour = bleNoxLight.getStartHour();
							startMin = bleNoxLight.getStartMinute();
							Calendar cal = Calendar.getInstance();
							cal.set(Calendar.HOUR_OF_DAY, startHour);
							cal.set(Calendar.MINUTE, startMin);
							cal.add(Calendar.MINUTE, bleNoxLight.getDuration());
							endHour = (byte) cal.get(Calendar.HOUR_OF_DAY);
							endMin = (byte) cal.get(Calendar.MINUTE);
							mCheckBox.setChecked(bleNoxLight.isEnable());
							setSmallLightTime(startHour, startMin, endHour, endMin);
						}
					}
				});
			}
		});
	}
	
	public void initUI() {
		tvTitle.setText(R.string.nightLight);
		tvRight.setText(R.string.save);
//		setSmallLightTime(startHour, startMin, endHour, endMin);
	}

	public void findView() {
		super.findView();
		mCheckBox = (CheckBox) findViewById(R.id.cb_small_light);
		mTvLightSetting = (TextView) findViewById(R.id.tv_set_light);
		mLayoutTimeSetting = (RelativeLayout) findViewById(R.id.rl_time_setting);
		mLayoutLightSet = (LinearLayout) findViewById(R.id.ll_set_light);
		mTvTime = (TextView) findViewById(R.id.tv_light_time);
	}


	public void initListener() {
		super.initListener();
		mTvLightSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SLPLight light = bleNoxLight.getLight();
				Intent intent = new Intent(SmallNightLightActivity.this, LightSet2Activity.class);
				intent.putExtra("type", LightSet2Activity.TYPE_SMALLNIGHT);
				intent.putExtra("light", light);
				intent.putExtra("brightness", bleNoxLight.getBrightness());
				startActivityForResult(intent, REQUEST_CODE_GET_LIGHT);
			}
		});

		mLayoutTimeSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SmallNightLightActivity.this, SelectStartEndTimeActivity.class);
				intent.putExtra(SelectStartEndTimeActivity.EXTRA_START_HOUR, startHour);
				intent.putExtra(SelectStartEndTimeActivity.EXTRA_START_MINUTE, startMin);
				intent.putExtra(SelectStartEndTimeActivity.EXTRA_END_HOUR, endHour);
				intent.putExtra(SelectStartEndTimeActivity.EXTRA_END_MINUTE, endMin);
				startActivityForResult(intent, REQUEST_CODE_GET_TIME);
			}
		});
		
		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SdkLog.log(TAG+" onCheckedChanged checked:" + isChecked);
				bleNoxLight.setEnable(isChecked);
				if (isChecked) {
					mLayoutTimeSetting.setVisibility(View.VISIBLE);
					mLayoutLightSet.setVisibility(View.VISIBLE);
				} else {
					mLayoutTimeSetting.setVisibility(View.GONE);
					mLayoutLightSet.setVisibility(View.GONE);
				}
			}
		});

		tvRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHelper.nigthLightConfigSet(bleNoxLight, 3000, new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
						if (!ActivityUtil.isActivityAlive(mActivity)) {
							return;
						}
						closeLight();

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

	private void closeLight() {
		mHelper.turnOffLight(3000, new IResultCallback() {

			@Override
			public void onResultCallback(CallbackData cd) {
				// TODO Auto-generated method stub

			}

		});
	}
	
	private byte startHour = 0, startMin = 0, endHour = 0, endMin = 0;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_GET_LIGHT) {
				SLPLight light = (SLPLight) data.getSerializableExtra("light");
				byte brightness = data.getByteExtra("brightness", (byte)0);
				bleNoxLight.setLight(light);
				bleNoxLight.setBrightness(brightness);
			} else if (requestCode == REQUEST_CODE_GET_TIME) {
				startHour = data.getByteExtra(SelectStartEndTimeActivity.EXTRA_START_HOUR, (byte) 0);
				startMin = data.getByteExtra(SelectStartEndTimeActivity.EXTRA_START_MINUTE, (byte) 0);
				endHour = data.getByteExtra(SelectStartEndTimeActivity.EXTRA_END_HOUR, (byte) 0);
				endMin = data.getByteExtra(SelectStartEndTimeActivity.EXTRA_END_MINUTE, (byte) 0);
				SdkLog.log(TAG+" onActivityResult sH:" + startHour+",sM:" + startMin+",eH:" + endHour+",eM:" + endMin);
				bleNoxLight.setStartHour(startHour);
				bleNoxLight.setStartMinute(startMin);
				bleNoxLight.setDuration(getContinueTime(startHour, startMin, endHour, endMin));
				setSmallLightTime(startHour, startMin, endHour, endMin);
			}
		}
	}

	/**
	 * 返回持续时长，单位分钟
	 * 
	 * @return
	 */
	private short getContinueTime(byte startHour, byte startMin, byte endHour, byte endMin) {
		Calendar c1 = TimeUtil.getCalendar(-100);
		c1.set(Calendar.HOUR_OF_DAY, startHour);
		c1.set(Calendar.MINUTE, startMin);
		c1.set(Calendar.SECOND, 0);
		long stime = c1.getTimeInMillis();

		c1.set(Calendar.HOUR_OF_DAY, endHour);
		c1.set(Calendar.MINUTE, endMin);
		long etime = c1.getTimeInMillis();

		if (etime - stime <= 0) {
			etime += 24 * 60 * 60 * 1000;
		}

		return (short) ((etime - stime) / 1000 / 60);
	}

	private void setSmallLightTime(byte startHour, byte startMin, byte endHour, byte endMin) {
		String time = null;
		if (true) {
			time = String.format("%02d:%02d", startHour, startMin) + "-" + String.format("%02d:%02d", endHour, endMin);
		} else {
			String sTimeUnit = "", eTimeUnit = "";
			// sTimeUnit = TimeUtill.isAM(startHour, startMin) ? getString(R.string.am)
			// : getString(R.string.pm);
			// eTimeUnit = TimeUtill.isAM(endHour, endMin) ? getString(R.string.am)
			// : getString(R.string.pm);

			time = TimeUtil.getHour12(startHour) + ":" + StringUtil.DF_2.format(startMin) + sTimeUnit + "-" + TimeUtil.getHour12(endHour) + ":" + StringUtil.DF_2.format(endMin) + eTimeUnit;
		}
		SdkLog.log(TAG+" setSmallLightTime sH:" + startHour+",sM:" +startMin+",eH:" + endHour+",eM:" + endMin+",time:" + time);
		mTvTime.setText(time);
	}

}
