package com.sdk902b.demo;

import java.util.Calendar;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.sdk902b.demo.R;
import com.sdk902b.demo.util.ActivityUtil;
import com.sdk902b.demo.util.LogUtil;
import com.sdk902b.demo.util.TimeUtill;
import com.sleepace.sdk.core.nox.domain.BleNoxNightLightInfo;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.StringUtil;

public class SmallNightLightActivity extends BaseActivity {

	private CheckBox mCheckBox;
	private TextView mTvLightSetting;
	private RelativeLayout mLayoutTimeSetting;
	private boolean lightOpen;
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
		initUI();
		initData();
		initListener();

	}

	public void initUI() {
		tvTitle.setText(R.string.nightLight);
		tvRight.setText(R.string.save);
		setSmallLightTime((byte)23, (byte)0, (byte)6, (byte)0);
		
	}

	public void findView() {
		super.findView();
		mCheckBox = (CheckBox) findViewById(R.id.cb_small_light);
		mTvLightSetting = (TextView) findViewById(R.id.tv_set_light);
		mLayoutTimeSetting = (RelativeLayout) findViewById(R.id.rl_time_setting);
		mLayoutLightSet = (LinearLayout) findViewById(R.id.ll_set_light);
		mTvTime = (TextView) findViewById(R.id.tv_light_time);

	}

	public void initData() {
		bleNoxLight = new BleNoxNightLightInfo();
		bleNoxLight.setBrightness((byte) 100);
		bleNoxLight.setDuration(getContinueTime((byte)23, (byte)0, (byte)6, (byte)0));
		bleNoxLight.setEnable(false);
		SLPLight light = new SLPLight();
		light.setR((byte) 0);
		light.setG((byte) 0);
		light.setB((byte) 0);
		light.setW((byte) 0);
		bleNoxLight.setLight(light);
		bleNoxLight.setStartHour((byte) 23);
		bleNoxLight.setStartMinute((byte) 0);

	}

	public void initListener() {
		super.initListener();
		mTvLightSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SmallNightLightActivity.this,
						LightSet2Activity.class);

				startActivityForResult(intent, REQUEST_CODE_GET_LIGHT);
			}
		});

		mLayoutTimeSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(SmallNightLightActivity.this,
						SelectStartEndTimeActivity.class);

				startActivityForResult(intent, REQUEST_CODE_GET_TIME);

			}
		});
		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				lightOpen = isChecked;
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
				mHelper.nigthLightConfig(bleNoxLight, 3000,
						new IResultCallback() {
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
	private void closeLight(){
		mHelper.turnOffLight(3000, new IResultCallback() {

			@Override
			public void onResultCallback(CallbackData cd) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_GET_LIGHT) {
				SLPLight light = (SLPLight) data
						.getSerializableExtra(LightSet2Activity.EXTRA_LIGHT_CONFIG);			
				bleNoxLight.setLight(light);
				bleNoxLight.setBrightness((byte)100);
			} else if (requestCode == REQUEST_CODE_GET_TIME) {
				Byte startHour = data.getByteExtra(
						SelectStartEndTimeActivity.EXTRA_START_HOUR, (byte) 0);
				Byte startMin = data
						.getByteExtra(
								SelectStartEndTimeActivity.EXTRA_START_MINUTE,
								(byte) 0);
				Byte endHour = data.getByteExtra(
						SelectStartEndTimeActivity.EXTRA_END_HOUR, (byte) 0);
				Byte endMin = data.getByteExtra(
						SelectStartEndTimeActivity.EXTRA_END_MINUTE, (byte) 0);
				bleNoxLight.setStartHour(startHour);
				bleNoxLight.setStartMinute(startMin);
				bleNoxLight.setDuration(getContinueTime(startHour, startMin,
						endHour, endMin));
				bleNoxLight.setBrightness((byte)100);
				setSmallLightTime(startHour, startMin, endHour, endMin);
			}
		} else {
			finish();
		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 返回持续时长，单位分钟
	 * 
	 * @return
	 */
	private short getContinueTime(Byte startHour, Byte startMin, Byte endHour,
			Byte endMin) {
		Calendar c1 = TimeUtill.getCalendar(-100);
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

	private void setSmallLightTime(Byte startHour, Byte startMin, Byte endHour,
			Byte endMin) {
		String time = null;
		if (TimeUtill.HourIs24(this)) {
			time = TimeUtill.formatMinute(startHour, startMin) + "-"
					+ TimeUtill.formatMinute(endHour, endMin);
		} else {
			String sTimeUnit = "", eTimeUnit = "";
			sTimeUnit = TimeUtill.isAM(startHour, startMin) ? getString(R.string.am)
					: getString(R.string.pm);
			eTimeUnit = TimeUtill.isAM(endHour, endMin) ? getString(R.string.am)
					: getString(R.string.pm);

			time = TimeUtill.getHour12(startHour) + ":"
					+ StringUtil.DF_2.format(startMin) + sTimeUnit + "-"
					+ TimeUtill.getHour12(endHour) + ":"
					+ StringUtil.DF_2.format(endMin) + eTimeUnit;
		}
		mTvTime.setText(time);
	}

}
