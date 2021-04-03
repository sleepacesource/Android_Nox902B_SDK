package com.sdk902b.demo;

import com.sdk902b.demo.util.ActivityUtil;
import com.sdk902b.demo.util.Utils;
import com.sdk902b.demo.view.SelectTimeDialog;
import com.sdk902b.demo.view.SelectTimeDialog.TimeSelectedListener;
import com.sdk902b.demo.view.SelectValueDialog;
import com.sdk902b.demo.view.SelectValueDialog.ValueSelectedListener;
import com.sleepace.sdk.core.nox.domain.BleNoxTimeMission;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.core.nox.domain.WaveCustomColor;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.SdkLog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class EditTimerTaskActivity extends BaseActivity {
	
	private View vStartTime, vCloseTime, vRepeat, vMode, vLight, vMusic;
	private TextView tvStartTime, tvCloseTime, tvRepeat, tvMode;
	private Button btnDel;
	private View layoutMusic;
	private SelectTimeDialog timeDialog;
	private SelectValueDialog valueDialog;
	private Object[] modeItems = null;
	
	private String action;
	private BleNoxTimeMission timer;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timertask);
        findView();
        initListener();
        initUI();
    }

    public void findView() {
    	super.findView();
    	vStartTime = findViewById(R.id.layout_start_time);
    	vCloseTime = findViewById(R.id.layout_end_time);
    	vRepeat = findViewById(R.id.layout_repeat);
    	vMode = findViewById(R.id.layout_mode);
    	vLight = findViewById(R.id.layout_light);
    	layoutMusic = findViewById(R.id.layout_music);
    	vMusic = findViewById(R.id.tv_music);
    	tvStartTime = (TextView) findViewById(R.id.tv_start_time);
    	tvCloseTime = (TextView) findViewById(R.id.tv_end_time);
    	tvRepeat = (TextView) findViewById(R.id.tv_reply);
    	tvMode = (TextView) findViewById(R.id.tv_mode);
    	btnDel = (Button) findViewById(R.id.btn_del);
    }

    public void initListener() {
    	super.initListener();
    	tvRight.setOnClickListener(this);
    	vStartTime.setOnClickListener(this);
    	vCloseTime.setOnClickListener(this);
    	vRepeat.setOnClickListener(this);
    	vMode.setOnClickListener(this);
    	vLight.setOnClickListener(this);
    	vMusic.setOnClickListener(this);
    	btnDel.setOnClickListener(this);
    }

    public void initUI() {
    	super.initUI();
    	tvRight.setText(R.string.save);
    	
    	action = getIntent().getStringExtra("action");
    	if("add".equals(action)) {
    		tvTitle.setText(R.string.add_timer);
    		btnDel.setVisibility(View.INVISIBLE);
    		byte newId = getIntent().getByteExtra("newId", (byte)0);
    		timer = new BleNoxTimeMission();
    		timer.setTimeID(newId);
    		timer.setOpen(true);
    		timer.setStartHour((byte)20);
    		timer.setStartMinute((byte)0);
    		timer.setEndHour((byte)21);
    		timer.setEndMinute((byte)0);
    		timer.setRepeat((byte)0);
    		timer.setMode((byte)1);// 模式  0助眠模式 1照明模式
    		timer.setPlayMode((byte)0);
    		timer.setValid((byte)1);
    		timer.setTimeStamp((int) (System.currentTimeMillis() / 1000));
    	}else {
    		tvTitle.setText(R.string.edit_timer);
    		btnDel.setVisibility(View.VISIBLE);
    		timer = (BleNoxTimeMission) getIntent().getSerializableExtra("timer");
    	}
        
        timeDialog = new SelectTimeDialog(this, "%02d");
        timeDialog.setTimeSelectedListener(timeListener);
        
        modeItems = new Object[2];
        modeItems[0] = getString(R.string.timer_mode_aid);
        modeItems[1] = getString(R.string.timer_mode_light);
        
        valueDialog = new SelectValueDialog(this);
        valueDialog.initData(SelectValueDialog.TYPE_TIMER_MODE, getString(R.string.cancel), "", getString(R.string.confirm), null, modeItems);
        valueDialog.setDefaultIndex(timer.getMode());
        valueDialog.setValueSelectedListener(valueSelectedListener);
		
        initStartTimeView();
        initCloseTimeView();
        initRepeatView();
        initModeView(timer.getMode());
    }
    
    
    private ValueSelectedListener valueSelectedListener = new ValueSelectedListener() {
		@Override
		public void onValueSelected(SelectValueDialog dialog, int type, int index, Object value) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onValueSelected type:" + type+",idx:" + index+",val:" + value);
			if(type == SelectValueDialog.TYPE_TIMER_MODE) {
				byte mode = (byte)index;
				timer.setMode(mode);
				initModeView(mode);
			}
		}
	};

    private TimeSelectedListener timeListener = new TimeSelectedListener() {
		@Override
		public void onTimeSelected(int type, byte hour, byte minute) {
			// TODO Auto-generated method stub
			if(type == SelectTimeDialog.TYPE_START_TIME) {
				timer.setStartHour(hour);
				timer.setStartMinute(minute);
				initStartTimeView();
			}else if(type == SelectTimeDialog.TYPE_END_TIME) {
				timer.setEndHour(hour);
				timer.setEndMinute(minute);
				initCloseTimeView();
			}
		}
	};
	
	private void initStartTimeView() {
    	tvStartTime.setText(String.format("%02d:%02d", timer.getStartHour(), timer.getStartMinute()));
    }
	
	private void initCloseTimeView() {
		tvCloseTime.setText(String.format("%02d:%02d", timer.getEndHour(), timer.getEndMinute()));
	}
	
	private void initRepeatView() {
		tvRepeat.setText(Utils.getSelectDay(this, timer.getRepeat()));
	}
	
	private void initMusicView() {
//		int res = Utils.getAlarmMusicName(alarm.getMusicID());
//		SdkLog.log(TAG+" initMusicView mid:" + alarm.getMusicID()+",res:" + res);
		
	}
	
	private void initModeView(byte mode) {
		tvMode.setText(mode == 0 ? R.string.timer_mode_aid : R.string.timer_mode_light);
		SLPLight light = timer.getLight();
		if(light == null) {
			light = new SLPLight();
		}
		if(mode == 1) {//照明模式
			SdkLog.log(TAG+" initModeView light----------" + mode);
			layoutMusic.setVisibility(View.GONE);
    		light.setR((byte)155);
    		light.setG((byte)32);
    		light.setB((byte)93);
    		light.setW((byte)255);
    		timer.setLight(light);
    		timer.setBrightness((byte)100);
		}else {
			SdkLog.log(TAG+" initModeView sleep aid----------" + mode);
			layoutMusic.setVisibility(View.VISIBLE);
			light.setR((byte)255);
    		light.setG((byte)35);
    		light.setB((byte)0);
    		light.setW((byte)0);
    		timer.setLight(light);
    		timer.setBrightness((byte)30);
    		timer.setMusicID((short)DemoApp.SLEEPAID_MUSIC[0][0]);
    		timer.setVolume((byte)6);
		}
	}
	
	private void initLightView() {
//		cbLight.setChecked(alarm.getBrightness() > 0);
	}
	
	private void initAromaView() {
//		cbAroma.setChecked(alarm.getAromaRate() > 0);
	}
	

	
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    public void onClick(View v) {
    	super.onClick(v);
    	if(v == tvRight) {
    		showLoading();
    		mHelper.timeMissionConfig(timer, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					if(!ActivityUtil.isActivityAlive(mActivity)) {
						return;
					}
					
					runOnUiThread(new Runnable() {
						public void run() {
							hideLoading();
							if(cd.isSuccess()) {
								Toast.makeText(mActivity, R.string.save_succeed, Toast.LENGTH_SHORT).show();
								goBack();
							}else {
								showErrTips(cd);
							}
						}
					});
				}
			});
    	}else if(v == vStartTime) {
    		timeDialog.initData(SelectTimeDialog.TYPE_START_TIME, getString(R.string.cancel), getString(R.string.start_time), getString(R.string.confirm), null, null);
    		timeDialog.setDefaultValue(timer.getStartHour(), timer.getStartMinute());
    		timeDialog.show();
    	}else if(v == vCloseTime) {
    		timeDialog.initData(SelectTimeDialog.TYPE_END_TIME, getString(R.string.cancel), getString(R.string.close_time), getString(R.string.confirm), null, null);
    		timeDialog.setDefaultValue(timer.getEndHour(), timer.getEndMinute());
    		timeDialog.show();
    	}else if(v == vRepeat) {
    		Intent intent = new Intent(this, DataListActivity.class);
			intent.putExtra("dataType", DataListActivity.DATA_TYPE_REPEAT);
			intent.putExtra("repeat", timer.getRepeat());
			startActivityForResult(intent, 100);
    	}else if(v == vMode) {
    		valueDialog.show();
    	}else if(v == vLight) {
    		Intent intent = new Intent(this, LightSet2Activity.class);
    		if(timer.getMode() == 0) {//助眠
    			intent.putExtra("type", LightSet2Activity.TYPE_TIMER_SLEEPAID);
    		}else if(timer.getMode() == 1) {//照明
    			intent.putExtra("type", LightSet2Activity.TYPE_TIMER_LGIHT);
    		}
    		intent.putExtra("brightness", timer.getBrightness());
    		intent.putExtra("light", timer.getLight());
			startActivityForResult(intent, 101);
    	}else if(v == vMusic) {
    		Intent intent = new Intent(this, MusicSetActivity.class);
    		intent.putExtra("timer", timer);
			startActivityForResult(intent, 102);
    	}else if(v == btnDel) {
    		Dialog dialog = createYesNoDialog(EditTimerTaskActivity.this, getString(R.string.delete_timer_task), getString(R.string.tips_delete_timer_task), null, null, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//确定清空灯光颜色
					showLoading();
		    		timer.setValid((byte)0);
		    		mHelper.timeMissionConfig(timer, 3000, new IResultCallback() {
						@Override
						public void onResultCallback(final CallbackData cd) {
							// TODO Auto-generated method stub
							if(!ActivityUtil.isActivityAlive(mActivity)) {
								return;
							}
							
							runOnUiThread(new Runnable() {
								public void run() {
									hideLoading();
									if(cd.isSuccess()) {
										Toast.makeText(mActivity, R.string.deleted, Toast.LENGTH_SHORT).show();
										goBack();
									}else {
										showErrTips(cd);
									}
								}
							});
						}
					});
				}
			}, null);
			dialog.show();
    		
    	}
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK) {
			if(requestCode == 100) {//重复
				byte repeat = data.getByteExtra("repeat", (byte)0);		
				timer.setRepeat(repeat);
				if(repeat!=0){
					timer.setTimeStamp(0);
				}
				initRepeatView();
			}else if(requestCode == 101) {//灯光
				SLPLight light = (SLPLight) data.getSerializableExtra("light");
				byte brightness = data.getByteExtra("brightness", (byte)0);
				timer.setLight(light);
				timer.setBrightness(brightness);
			}else if(requestCode == 102) {//音乐
				byte loopMode = data.getByteExtra("loopMode", (byte)0);
				short musicId = data.getShortExtra("musicId", (short)0);
				byte volume = data.getByteExtra("volume", (byte)0);
				timer.setPlayMode(loopMode);
				timer.setMusicID(musicId);
				timer.setVolume(volume);
			}
		}
	}
}












