package com.sdk902b.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sdk902b.demo.util.ActivityUtil;
import com.sdk902b.demo.util.Utils;
import com.sleepace.sdk.core.nox.domain.BleNoxTimeMission;
import com.sleepace.sdk.core.nox.domain.BleNoxWorkStatus;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.ble.BleHelper;
import com.sleepace.sdk.nox902b.Nox902BHelper.WorkStatusListener;
import com.sleepace.sdk.util.SdkLog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TimerTaskListActivity extends BaseActivity {
	private View dataView;
	private TextView tvTips;
	private ListView listView;
	private LayoutInflater inflater;
	private AlarmAdapter adapter;
	
	private List<BleNoxTimeMission> list = new ArrayList<BleNoxTimeMission>();
	private static final Byte[] TIMER_IDS = new Byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		findView();
		initListener();
		initUI();
	}

	public void findView() {
		super.findView();
		listView = (ListView) findViewById(R.id.list);
		dataView = findViewById(R.id.layout_data);
		tvTips = (TextView) findViewById(R.id.tv_tips);
	}

	public void initListener() {
		super.initListener();
		ivRight.setOnClickListener(this);
		listView.setOnItemClickListener(onItemClickListener);
		mHelper.addWorkStatusListener(workStatusListener);
	}

	public void initUI() {
		inflater = getLayoutInflater();
		tvTitle.setText(R.string.timer_task);
		ivRight.setImageResource(R.drawable.device_btn_add_nor);
		
//		BleNoxTimeMission timer = new BleNoxTimeMission();
//		timer.setOpen(true);
//		timer.setStartHour((byte)21);
//		timer.setStartMinute((byte)30);
//		timer.setEndHour((byte)23);
//		timer.setEndMinute((byte)45);
//		timer.setRepeat((byte) 3);
//		SLPLight light = new SLPLight();
//		light.setR((byte)255);
//		light.setG((byte)35);
//		light.setB((byte)0);
//		light.setW((byte)0);
//		timer.setLight(light);
//		timer.setMusicID((short)DemoApp.SLEEPAID_MUSIC[0][0]);
//		timer.setValid((byte)1);
//		timer.setVolume((byte) 6);
//		timer.setBrightness((byte) 30);
//		list.add(timer);
		
		adapter = new AlarmAdapter();
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		showLoading();
		mHelper.getTimeMissionList(3000, new IResultCallback<List<BleNoxTimeMission>>() {
			@Override
			public void onResultCallback(final CallbackData<List<BleNoxTimeMission>> cd) {
				// TODO Auto-generated method stub
				if(!ActivityUtil.isActivityAlive(mActivity)) {
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						hideLoading();
						if(cd.isSuccess()) {
							list.clear();
							List<BleNoxTimeMission> tempList = cd.getResult();
							for(BleNoxTimeMission timer : tempList) {
								if(timer.getValid() != 0) {
									list.add(timer);
								}
							}
//							list.addAll(tempList);
							adapter.notifyDataSetChanged();
						}else {
							showErrTips(cd);
						}
					}
				});
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHelper.removeWorkStatusListener(workStatusListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BleHelper.REQCODE_OPEN_BT) {

		}
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			BleNoxTimeMission timer = adapter.getItem(position);
			Intent intent = new Intent(mActivity, EditTimerTaskActivity.class);
			intent.putExtra("action", "edit");
			intent.putExtra("timer", timer);
			startActivity(intent);
		}
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v == ivRight) {
			if(list.size() >= 10) {
				Toast.makeText(this, R.string.timer_limit, Toast.LENGTH_SHORT).show();
			}else {
				Intent intent = new Intent(this, EditTimerTaskActivity.class);
				intent.putExtra("action", "add");
				byte newId = getNewTimerId();
				intent.putExtra("action", "add");
				intent.putExtra("newId", newId);
				startActivityForResult(intent, 100);
			}
		}
	}
	
	private WorkStatusListener workStatusListener = new WorkStatusListener() {
		@Override
		public void onWorkStatusChanged(final BleNoxWorkStatus workStatus) {
			// TODO Auto-generated method stub
			if(!ActivityUtil.isActivityAlive(mActivity)) {
				return;
			}
			
			runOnUiThread(new Runnable() {
				public void run() {
					
				}
			});
		}
	};
	
	private byte getNewTimerId() {
		byte newId = 0;
		List<Byte> allIdList = Arrays.asList(TIMER_IDS);
		List<Byte> curIdList = new ArrayList<Byte>();
		for(BleNoxTimeMission timer : list) {
			curIdList.add(timer.getTimeID());
		}
		
		for(Byte id : allIdList) {
			if(!curIdList.contains(id)) {
				newId = id;
				break;
			}
		}
		
		SdkLog.log(TAG+" getNewTimerId:" + newId+",curIdList:" + curIdList);
		
		return newId;
	}
	
	
	private BleNoxTimeMission getTimerTask(byte taskId) {
		for(BleNoxTimeMission info : list) {
			if(info.getTimeID() == taskId) {
				return info;
			}
		}
		return null;
	}
	
	private void closeSameTask(BleNoxTimeMission task) {
		for(BleNoxTimeMission info : list) {
			if(info.getRepeat() == 0 && info.getStartHour() == task.getStartHour() && info.getStartMinute() == task.getStartMinute() && 
					info.getEndHour() == task.getEndHour() && info.getEndMinute() == task.getEndMinute()) {
				info.setOpen(false);
			}
		}
	}
	
	class AlarmAdapter extends BaseAdapter {
		
		AlarmAdapter(){
		}

		class ViewHolder {
			TextView tvMode;
			TextView tvTime;
			TextView tvRepeat;
			TextView tvMusic;
			CheckBox cbSwitch;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public BleNoxTimeMission getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_timertask_item, null);
				holder = new ViewHolder();
				holder.tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
				holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
				holder.tvRepeat = (TextView) convertView.findViewById(R.id.tv_repeat);
				holder.tvMusic = (TextView) convertView.findViewById(R.id.tv_music);
				holder.cbSwitch = (CheckBox) convertView.findViewById(R.id.cb_swtich);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			final BleNoxTimeMission item = getItem(position);
			holder.tvMode.setText(item.getMode() == 0 ? R.string.timer_mode_aid : R.string.timer_mode_light);
			holder.tvTime.setText(String.format("%02d:%02d", item.getStartHour(), item.getStartMinute()) + "~" + String.format("%02d:%02d", item.getEndHour(), item.getEndMinute()));
			holder.tvRepeat.setText(Utils.getSelectDay(mActivity, item.getRepeat()));
			int nameRes = Utils.getSleepAidMusicName(item.getMusicID());
			String musicName = null;
			if(nameRes > 0) {
				musicName = getString(nameRes);
			}
			holder.tvMusic.setText(musicName);
			holder.cbSwitch.setTag(item.getTimeID());
			holder.cbSwitch.setChecked(item.isOpen());
			
			holder.cbSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
//					LogUtil.log(TAG+" alarm open changed:" + isChecked+",tag:" + buttonView.getTag()+",item:" + item);
					if(buttonView.getTag() != null) {
						long timerId = Long.valueOf(buttonView.getTag().toString());
						if(timerId != item.getTimeID() || isChecked == item.isOpen()) {
							return;
						}
					}
					
					SdkLog.log(TAG+" alarm open changed:" + isChecked);
					item.setOpen(isChecked);
					mHelper.timeMissionConfig(item, 3000, new IResultCallback() {
						@Override
						public void onResultCallback(CallbackData cd) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			});
			
			return convertView;
		}
		
		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			if(getCount() > 0) {
				dataView.setVisibility(View.VISIBLE);
				tvTips.setVisibility(View.GONE);
				super.notifyDataSetChanged();
			}else {
				dataView.setVisibility(View.GONE);
				tvTips.setVisibility(View.VISIBLE);
				tvTips.setText(R.string.sa_no_timer);
			}
		}
	}
	
	
	
	
}




