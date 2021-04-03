package com.sdk902b.demo;

import com.sdk902b.demo.util.ActivityUtil;
import com.sdk902b.demo.util.Utils;
import com.sdk902b.demo.view.SelectTimeDialog;
import com.sdk902b.demo.view.SelectTimeDialog.TimeSelectedListener;
import com.sdk902b.demo.view.SelectValueDialog;
import com.sdk902b.demo.view.SelectValueDialog.ValueSelectedListener;
import com.sleepace.sdk.core.nox.domain.BleNoxAlarmInfo;
import com.sleepace.sdk.core.nox.interfs.INoxManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.SdkLog;
import com.sleepace.sdk.util.TimeUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class EditAlarmActivity extends BaseActivity {

	private View vStartTime, vRingTime, vRepeat, vMusic, vVolume, vSnooze, vSnoozeTime, vSnoozeCount;
	private TextView tvStartTime, tvRepeat, tvMusic, tvVolume, tvRingTime, tvSnoozeTime, tvSnoozeCount, tvSnoozeTips;
	private CheckBox cbLight, cbAroma, cbSnooze;
	private Button btnPreview, btnDel;

	private SelectTimeDialog timeDialog;
	private Object[] ringTimeItems = null, volumeItems = null, snoozeTimeItems = null, snoozeCountItems = null;
	private SelectValueDialog valueDialog;

	private String action;
	private BleNoxAlarmInfo alarm;
	private byte snoozeCount = 5;

	private boolean isPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_alarm);
		findView();
		initListener();
		initUI();
	}

	public void findView() {
		super.findView();
		vStartTime = findViewById(R.id.layout_start_time);
		vRingTime = findViewById(R.id.layout_ring_time);
		vRepeat = findViewById(R.id.layout_repeat);
		vMusic = findViewById(R.id.layout_music);
		vVolume = findViewById(R.id.layout_volume);
		vSnooze = findViewById(R.id.layout_snooze);
		tvStartTime = (TextView) findViewById(R.id.tv_start_time);
		tvRingTime = findViewById(R.id.tv_ring_time);
		tvRepeat = (TextView) findViewById(R.id.tv_reply);
		tvMusic = (TextView) findViewById(R.id.tv_music);
		tvVolume = (TextView) findViewById(R.id.tv_volume);
		cbLight = (CheckBox) findViewById(R.id.cb_light);
		cbAroma = (CheckBox) findViewById(R.id.cb_aroma);
		cbSnooze = (CheckBox) findViewById(R.id.cb_snooze);
		vSnoozeTime = findViewById(R.id.layout_snooze_time);
		vSnoozeCount = findViewById(R.id.layout_snooze_count);
		tvSnoozeTime = (TextView) findViewById(R.id.tv_snooze_time);
		tvSnoozeCount = (TextView) findViewById(R.id.tv_snooze_count);
		tvSnoozeTips = findViewById(R.id.tv_tips_snooze);
		btnPreview = (Button) findViewById(R.id.btn_preview);
		btnDel = (Button) findViewById(R.id.btn_del);
	}

	public void initListener() {
		super.initListener();
		tvRight.setOnClickListener(this);
		vStartTime.setOnClickListener(this);
		vRingTime.setOnClickListener(this);
		vRepeat.setOnClickListener(this);
		vMusic.setOnClickListener(this);
		vVolume.setOnClickListener(this);
		vSnoozeTime.setOnClickListener(this);
		vSnoozeCount.setOnClickListener(this);
		btnPreview.setOnClickListener(this);
		btnDel.setOnClickListener(this);
		cbLight.setOnCheckedChangeListener(onCheckedChangeListener);
		cbSnooze.setOnCheckedChangeListener(onCheckedChangeListener);
	}

	public void initUI() {
		super.initUI();
		tvRight.setText(R.string.save);

		action = getIntent().getStringExtra("action");
		if ("add".equals(action)) {
			tvTitle.setText(R.string.add_alarm);
			btnDel.setVisibility(View.INVISIBLE);
			long newId = getIntent().getLongExtra("newId", 0l);
			alarm = new BleNoxAlarmInfo();
			alarm.setAlarmID(newId);
			alarm.setOpen(true);
			alarm.setHour((byte) 8);
			alarm.setAlarmMaxTime((byte)8);
			alarm.setRepeat((byte) 0);
			alarm.setMusicID(DemoApp.ALARM_MUSIC[0][0]);
			alarm.setVolume((byte) 16);
			alarm.setBrightness((byte) 80);
			alarm.setAromaRate((byte) 2);
			alarm.setSnoozeCount(snoozeCount);
			alarm.setSnoozeLength((byte) 9);
			alarm.setTimestamp(TimeUtil.getCurrentTimeInt());
		} else {
			tvTitle.setText(R.string.edit_alarm);
			btnDel.setVisibility(View.VISIBLE);
			alarm = (BleNoxAlarmInfo) getIntent().getSerializableExtra("alarm");
			snoozeCount = alarm.getSnoozeCount();
		}

		timeDialog = new SelectTimeDialog(this, SelectTimeDialog.TYPE_START_TIME, "%02d");
		timeDialog.setTimeSelectedListener(timeListener);
		
		ringTimeItems = new Object[10];
		for(int i=0;i<ringTimeItems.length;i++) {
			ringTimeItems[i] = i + 1;
		}

		volumeItems = new Object[16];
		for (int i = 1; i <= volumeItems.length; i++) {
			volumeItems[i - 1] = i;
		}
		
		snoozeTimeItems = new Object[60];
		for(int i=0; i<snoozeTimeItems.length; i++) {
			snoozeTimeItems[i] = i + 1;
		}
		
		snoozeCountItems = new Object[] {1, 2, 3, 4, 5};

		valueDialog = new SelectValueDialog(this);
		valueDialog.setValueSelectedListener(valueSelectedListener);

		initTimeView();
		initRingTimeView();
		initRepeatView();
		initMusicView();
		initVolumeView();
		initLightView();
		initAromaView();
		initSnoozeView();
	}

	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (buttonView == cbLight) {
				alarm.setBrightness(isChecked ? (byte) 80 : 0);
			} else if (buttonView == cbAroma) {
				alarm.setAromaRate(isChecked ? INoxManager.AromaSpeed.COMMON.getValue() : INoxManager.AromaSpeed.CLOSE.getValue());
			} else if (buttonView == cbSnooze) {
				if (isChecked) {
					if(snoozeCount == 0) {
						snoozeCount = 5;
					}
					alarm.setSnoozeCount(snoozeCount);
				} else {
					alarm.setSnoozeCount((byte)0);
				}
				initSnoozeView();
			}
		}
	};

	private ValueSelectedListener valueSelectedListener = new ValueSelectedListener() {
		@Override
		public void onValueSelected(SelectValueDialog dialog, int type, int index, Object value) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG + " onValueSelected val:" + value);
			if (type == SelectValueDialog.TYPE_SNOOZE_TIME) {
				byte snoozeTime = (byte)(int)(Integer)value;
				alarm.setSnoozeLength(snoozeTime);
				initSnoozeView();
			} else if (type == SelectValueDialog.TYPE_SNOOZE_COUNT) {
				snoozeCount = (byte)(int)(Integer)value;
				alarm.setSnoozeCount(snoozeCount);
				initSnoozeView();
			} else if (type == SelectValueDialog.TYPE_VOLUME) {
				byte vol = (byte)(int)(Integer)value;
				alarm.setVolume(vol);
				initVolumeView();
			} else if(type == SelectValueDialog.TYPE_RING_TIME) {
				byte ringTime = (byte)(int)(Integer)value;
				alarm.setAlarmMaxTime(ringTime);
				initRingTimeView();
			}
		}
	};

	private TimeSelectedListener timeListener = new TimeSelectedListener() {
		@Override
		public void onTimeSelected(int type, byte hour, byte minute) {
			// TODO Auto-generated method stub
			alarm.setHour(hour);
			alarm.setMinute(minute);
			initTimeView();
		}
	};

	private void initTimeView() {
		tvStartTime.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
	}

	private void initRingTimeView() {
		tvRingTime.setText(alarm.getAlarmMaxTime() + getString(R.string.min));
	}
	
	private void initRepeatView() {
		tvRepeat.setText(Utils.getSelectDay(this, alarm.getRepeat()));
	}

	private void initMusicView() {
		int res = Utils.getAlarmMusicName(alarm.getMusicID());
		// SdkLog.log(TAG+" initMusicView mid:" + alarm.getMusicID()+",res:" + res);
		if (res > 0) {
			tvMusic.setText(res);
		} else {
			tvMusic.setText("");
		}
	}

	private void initVolumeView() {
		tvVolume.setText(String.valueOf(alarm.getVolume()));
	}

	private void initLightView() {
		cbLight.setChecked(alarm.getBrightness() > 0);
	}

	private void initAromaView() {
		cbAroma.setChecked(alarm.getAromaRate() > 0);
	}

	private void initSnoozeView() {
		cbSnooze.setChecked(alarm.getSnoozeCount() > 0);
		if(cbSnooze.isChecked()) {
			vSnooze.setVisibility(View.VISIBLE);
			tvSnoozeTime.setText(alarm.getSnoozeLength() + getString(R.string.min));
			tvSnoozeCount.setText(alarm.getSnoozeCount() + getString(R.string.totalTime));
			tvSnoozeTips.setVisibility(View.VISIBLE);
			tvSnoozeTips.setText(getString(R.string.smart_wake_turn_on2, String.valueOf(alarm.getSnoozeCount())));
		}else {
			vSnooze.setVisibility(View.GONE);
			tvSnoozeTips.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == tvRight) {
			showLoading();
			if (!cbSnooze.isChecked()) {
				alarm.setSnoozeCount((byte) 0);
			}
			mHelper.alarmConfig(alarm, 3000, new IResultCallback() {
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
								Toast.makeText(mActivity, R.string.save_succeed, Toast.LENGTH_SHORT).show();
								goBack();
							} else {
								showErrTips(cd);
							}
						}
					});
				}
			});
		} else if (v == vStartTime) {
			timeDialog.initData(SelectTimeDialog.TYPE_START_TIME, getString(R.string.cancel), getString(R.string.sa_start_time), getString(R.string.confirm), null, null);
			timeDialog.setDefaultValue(alarm.getHour(), alarm.getMinute());
			timeDialog.show();
		} else if (v == vRingTime) {
			valueDialog.initData(SelectValueDialog.TYPE_RING_TIME, getString(R.string.cancel), getString(R.string.alarmMaxTime), getString(R.string.confirm), getString(R.string.min), ringTimeItems);
			valueDialog.setDefaultIndex(alarm.getAlarmMaxTime() - 1);
			valueDialog.show();
		} else if (v == vRepeat) {
			Intent intent = new Intent(this, DataListActivity.class);
			intent.putExtra("dataType", DataListActivity.DATA_TYPE_REPEAT);
			intent.putExtra("repeat", alarm.getRepeat());
			startActivityForResult(intent, 100);
		} else if (v == vSnoozeTime) {
			valueDialog.initData(SelectValueDialog.TYPE_SNOOZE_TIME, getString(R.string.cancel), getString(R.string.snooze_duration), getString(R.string.confirm), getString(R.string.min), snoozeTimeItems);
			valueDialog.setDefaultIndex(alarm.getSnoozeLength() - 1);
			valueDialog.show();
		} else if (v == vSnoozeCount) {
			valueDialog.initData(SelectValueDialog.TYPE_SNOOZE_COUNT, getString(R.string.cancel), getString(R.string.snooze_duration), getString(R.string.confirm), getString(R.string.totalTime), snoozeCountItems);
			valueDialog.setDefaultIndex(alarm.getSnoozeCount() - 1);
			valueDialog.show();
		} else if (v == vMusic) {
			Intent intent = new Intent(this, DataListActivity.class);
			intent.putExtra("dataType", DataListActivity.DATA_TYPE_ALARM_MUSIC);
			intent.putExtra("musicId", alarm.getMusicID());
			startActivityForResult(intent, 101);
		} else if (v == vVolume) {
			valueDialog.initData(SelectValueDialog.TYPE_VOLUME, getString(R.string.cancel), getString(R.string.volume), getString(R.string.confirm), null, volumeItems);
			valueDialog.setDefaultValue(alarm.getVolume());
			valueDialog.show();
		} else if (v == btnPreview) {
			showLoading();
			if (!isPreview) {

				SdkLog.log(TAG + " preview alarm:" + alarm);
				mHelper.startAlarmPreview(alarm.getVolume(), alarm.getBrightness(), alarm.getMusicID(), 3000, new IResultCallback() {
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
									isPreview = true;
									btnPreview.setText(R.string.preview_stop);
								} else {
									showErrTips(cd);
								}
							}
						});
					}
				});
			} else {
				mHelper.stopAlarmPreview(3000, new IResultCallback() {
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
									isPreview = false;
									btnPreview.setText(R.string.preview_alarm);
								} else {
									showErrTips(cd);
								}
							}
						});
					}
				});
			}
		} else if (v == btnDel) {
			showLoading();
			mHelper.delAlarm(alarm.getAlarmID(), 3000, new IResultCallback() {
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
								Toast.makeText(mActivity, R.string.deleted, Toast.LENGTH_SHORT).show();
								goBack();
							} else {
								showErrTips(cd);
							}
						}
					});
				}
			});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			byte repeat = data.getByteExtra("repeat", (byte) 0);
			alarm.setRepeat(repeat);
			if (repeat != 0) {
				alarm.setTimestamp(0);
			}
			initRepeatView();
		} else if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
			int musicId = data.getIntExtra("musicId", 0);
			alarm.setMusicID(musicId);
			initMusicView();
		}
	}
}
