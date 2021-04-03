package com.sdk902b.demo;

import com.sdk902b.demo.util.Utils;
import com.sdk902b.demo.view.SelectValueDialog;
import com.sdk902b.demo.view.SelectValueDialog.ValueSelectedListener;
import com.sleepace.sdk.core.nox.domain.BleNoxTimeMission;
import com.sleepace.sdk.util.SdkLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MusicSetActivity extends BaseActivity {
	private View vLoopMode, vMusic, vVolume;
	private TextView tvLoopMode, tvMusic, tvVolume;
//	private EditText etVolume;
//	private Button btnSend;
	
	private SelectValueDialog valueDialog;
	private Object[] loopModeItems = null, volumeItems = null;
	private BleNoxTimeMission timer;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_set);
		timer = (BleNoxTimeMission) getIntent().getSerializableExtra("timer");
		findView();
		initListener();
		initUI();
	}

	public void initUI() {
		tvTitle.setText(getString(R.string.timer_music));
		tvRight.setText(getString(R.string.save));
		initLoopMode();
		initMusicView();
		initVolumeView();
//		etVolume.setText(String.valueOf(timer.getVolume()));
		
		loopModeItems = new Object[3];
		loopModeItems[0] = getString(R.string.loop_order);
		loopModeItems[1] = getString(R.string.loop_random);
		loopModeItems[2] = getString(R.string.loop_single);
		
		volumeItems = new Object[16];
		for (int i = 1; i <= volumeItems.length; i++) {
			volumeItems[i - 1] = i;
		}
		
		valueDialog = new SelectValueDialog(mActivity);
		valueDialog.setValueSelectedListener(valueSelectedListener);
	}

	public void findView() {
		super.findView();
		vLoopMode = findViewById(R.id.layout_loop_mode);
		vMusic = findViewById(R.id.layout_music);
		tvLoopMode = (TextView) findViewById(R.id.tv_loop_mode);
		tvMusic = (TextView) findViewById(R.id.tv_music);
		vVolume = findViewById(R.id.layout_volume);
		tvVolume = (TextView) findViewById(R.id.tv_volume);
	}

	public void initListener() {
		super.initListener();
		tvRight.setOnClickListener(this);
		vLoopMode.setOnClickListener(this);
		vMusic.setOnClickListener(this);
		vVolume.setOnClickListener(this);
	}
	
	private ValueSelectedListener valueSelectedListener = new ValueSelectedListener() {
		@Override
		public void onValueSelected(SelectValueDialog dialog, int type, int index, Object value) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onValueSelected type:" + type+",idx:" + index+",val:" + value);
			if(type == SelectValueDialog.TYPE_LOOP_MODE) {
				timer.setPlayMode((byte) index);
				initLoopMode();
			}else if(type == SelectValueDialog.TYPE_VOLUME) {
				timer.setVolume((byte)(int)(Integer) value);
				initVolumeView();
			}
		}
	};
	
	private void initLoopMode() {
		if(timer.getPlayMode() == 0) {
			tvLoopMode.setText(R.string.loop_order);
		}else if(timer.getPlayMode() == 1) {
			tvLoopMode.setText(R.string.loop_random);
		}else if(timer.getPlayMode() == 2) {
			tvLoopMode.setText(R.string.loop_single);
		}
	}

	private TextWatcher volumeWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String str = s.toString();
			if (!TextUtils.isEmpty(str)) {
				int volume = Integer.valueOf(str);
				if (volume > 16) {
					Toast.makeText(mActivity, R.string.input_0_16, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == tvRight) {
			SdkLog.log(TAG+" right click----------");
			Intent data = new Intent();
			data.putExtra("loopMode", timer.getPlayMode());
			data.putExtra("musicId", timer.getMusicID());
			data.putExtra("volume", timer.getVolume());
			setResult(RESULT_OK, data);
			goBack();
		}else if(v == vLoopMode) {
			valueDialog.initData(SelectValueDialog.TYPE_LOOP_MODE, getString(R.string.cancel), "", getString(R.string.confirm), null, loopModeItems);
			valueDialog.setDefaultIndex(timer.getPlayMode());
			valueDialog.show();
		}else if(v == vMusic) {
			Intent intent = new Intent(mActivity, DataListActivity.class);
			intent.putExtra("dataType", DataListActivity.DATA_TYPE_TIMER_MUSIC);
			startActivityForResult(intent, 100);
		}else if(v == vVolume) {
			valueDialog.initData(SelectValueDialog.TYPE_VOLUME, getString(R.string.cancel), "", getString(R.string.confirm), null, volumeItems);
			valueDialog.setDefaultIndex(timer.getVolume() - 1);
			valueDialog.show();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 100 && resultCode == Activity.RESULT_OK) {
			short musicId = (short)data.getIntExtra("musicId", 0);
			timer.setMusicID(musicId);
			initMusicView();
		}
	}
	
	private void initMusicView() {
		int nameRes = Utils.getSleepAidMusicName(timer.getMusicID());
		String musicName = null;
		if(nameRes > 0) {
			musicName = getString(nameRes);
		}
		tvMusic.setText(musicName);
	}
	
	private void initVolumeView() {
		tvVolume.setText(String.valueOf(timer.getVolume()));
	}

}
