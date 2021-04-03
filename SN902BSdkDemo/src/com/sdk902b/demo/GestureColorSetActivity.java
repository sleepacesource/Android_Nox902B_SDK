package com.sdk902b.demo;

import java.util.List;

import com.sdk902b.demo.util.ActivityUtil;
import com.sdk902b.demo.util.Utils;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.core.nox.domain.WaveCustomColor;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GestureColorSetActivity extends BaseActivity {
	private LinearLayout colorLayout;
	private int colorCount = 6;
	private TextView[] tvLabel;
	private EditText[] etR, etG, etB, etW;
	private Button[] btnPreview, btnClear;
	private List<WaveCustomColor> list;
	private int customColorCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_color_set1);
		findView();
		initListener();
		initUI();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showLoading();
		mHelper.getWaveColorList(3000, new IResultCallback<List<WaveCustomColor>>() {
			@Override
			public void onResultCallback(final CallbackData<List<WaveCustomColor>> cd) {
				// TODO Auto-generated method stub
				if(!ActivityUtil.isActivityAlive(mActivity)) {
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						hideLoading();
						if(cd.isSuccess()) {
							list = cd.getResult();
							customColorCount = 0;
							initRGBWView();
						}else {
							showErrTips(cd);
						}
					}
				});
			}
		});
	}

	public void initUI() {
		tvTitle.setText(getString(R.string.setLight));
		tvRight.setText(getString(R.string.save));
		for(int i=0; i<colorCount; i++) {
			tvLabel[i].setText(getString(R.string.color) + (i + 1));
		}
	}

	public void findView() {
		super.findView();
		colorLayout = findViewById(R.id.layout_color);
		tvLabel = new TextView[colorCount];
		etR = new EditText[colorCount];
		etG = new EditText[colorCount];
		etB =  new EditText[colorCount];
		etW =  new EditText[colorCount];
		btnPreview = new Button[colorCount];
		btnClear = new Button[colorCount];
		LayoutInflater inflater = getLayoutInflater();
		for(int i=0; i<colorCount; i++) {
			View view = inflater.inflate(R.layout.list_gesture_color_item, null);
			tvLabel[i] = view.findViewById(R.id.tv_color_num);
			etR[i] = view.findViewById(R.id.et_r);
			etG[i] = view.findViewById(R.id.et_g);
			etB[i] = view.findViewById(R.id.et_b);
			etW[i] = view.findViewById(R.id.et_w);
			btnPreview[i] = view.findViewById(R.id.btn_preview);
			btnClear[i] = view.findViewById(R.id.btn_clear);
			colorLayout.addView(view, i);
		}
	}

	public void initListener() {
		super.initListener();
		tvRight.setOnClickListener(this);
		for(int i=0;i<colorCount;i++) {
			etR[i].addTextChangedListener(rgbwWatcher);
			etG[i].addTextChangedListener(rgbwWatcher);
			etB[i].addTextChangedListener(rgbwWatcher);
			etW[i].addTextChangedListener(rgbwWatcher);
			btnPreview[i].setOnClickListener(new ButtonClickListener(i));
			btnClear[i].setOnClickListener(new ButtonClickListener(i));
		}
	}
	
	private void initRGBWView() {
		if(list != null && list.size() > 0) {
			for(WaveCustomColor color : list) {
				byte colorId = color.getColorId();
				if(colorId != colorCount) {//最后一个是流光
					if(color.getValid() == 1) {
						etR[colorId].setText(String.valueOf(color.getLight().getR() & 0xFF));
						etG[colorId].setText(String.valueOf(color.getLight().getG() & 0xFF));
						etB[colorId].setText(String.valueOf(color.getLight().getB() & 0xFF));
						etW[colorId].setText(String.valueOf(color.getLight().getW() & 0xFF));
					}else {
						etR[colorId].setText("");
						etG[colorId].setText("");
						etB[colorId].setText("");
						etW[colorId].setText("");
					}
				}
				
				if(color.getValid() == 1) {
					customColorCount++;
				}
			}
		}
	}
	
	private class ButtonClickListener implements View.OnClickListener {
		private int colorId;
		ButtonClickListener(int colorId){
			this.colorId = colorId;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == btnPreview[colorId]) {
				if(Utils.inputTips(etR[colorId], 255)) {
					return;
				}
				if(Utils.inputTips(etG[colorId], 120)) {
					return;
				}
				if(Utils.inputTips(etB[colorId], 255)) {
					return;
				}
				if(Utils.inputTips(etW[colorId], 255)) {
					return;
				}
				
				SLPLight light = new SLPLight();
				String strR = etR[colorId].getText().toString();
				String strG = etG[colorId].getText().toString();
				String strB = etB[colorId].getText().toString();
				String strW = etW[colorId].getText().toString();
				
				byte r = (byte)(int)Integer.valueOf(strR);
				byte g = (byte)(int)Integer.valueOf(strG);
				byte b = (byte)(int)Integer.valueOf(strB);
				byte w = (byte)(int)Integer.valueOf(strW);
				light.setR(r);
				light.setG(g);
				light.setB(b);
				light.setW(w);
				mHelper.previewCustomColor((byte)1, light, 3000, new IResultCallback() {
					@Override
					public void onResultCallback(CallbackData arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}else if(v == btnClear[colorId]) {
				Dialog dialog = createYesNoDialog(GestureColorSetActivity.this, getString(R.string.clear_light_color), getString(R.string.clear_light_color_msg), null, null, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(customColorCount > 3) {
							customColorCount--;
							etR[colorId].setText("");
							etG[colorId].setText("");
							etB[colorId].setText("");
							etW[colorId].setText("");
							//确定清空灯光颜色
							WaveCustomColor color = getCustomColor(colorId);
							color.setValid((byte)0);
						}else {
							Toast.makeText(mActivity, R.string.at_Least_3, Toast.LENGTH_SHORT).show();
						}
					}
				}, null);
				dialog.show();
			}
		}
	}
	
	private WaveCustomColor getCustomColor(int colorId) {
		WaveCustomColor color = null;
		if(list != null && list.size() > 0) {
			for(WaveCustomColor wcc : list) {
				if(wcc.getColorId() == colorId) {
					color = wcc;
					break;
				}
			}
		}
		return color;
	}

	private TextWatcher rgbwWatcher = new TextWatcher() {

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
				int rgbw = Integer.valueOf(str);
				if (rgbw > 255) {
					Toast.makeText(mActivity, R.string.input_0_255, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == tvRight) {
			if(list != null && list.size() > 0) {
				customColorCount = 0;
				for(int i=0; i<colorCount; i++) {
					int colorId = i;
					WaveCustomColor color = getCustomColor(colorId);
					
					SLPLight light = new SLPLight();
					String strR = etR[colorId].getText().toString();
					String strG = etG[colorId].getText().toString();
					String strB = etB[colorId].getText().toString();
					String strW = etW[colorId].getText().toString();
					
					if(!TextUtils.isEmpty(strR) || !TextUtils.isEmpty(strG) || !TextUtils.isEmpty(strB) || !TextUtils.isEmpty(strW)) {
						color.setValid((byte)1);
						customColorCount++;
					}else {
						color.setValid((byte)0);
					}
					
					if(TextUtils.isEmpty(strR)) {
						strR = "0";
					}
					
					if(TextUtils.isEmpty(strG)) {
						strG = "0";
					}
					
					if(TextUtils.isEmpty(strB)) {
						strB = "0";
					}
					
					if(TextUtils.isEmpty(strW)) {
						strW = "0";
					}
					
					int r = Integer.valueOf(strR);
					int g = Integer.valueOf(strG);
					int b = Integer.valueOf(strB);
					int w = Integer.valueOf(strW);
					
					if(r > 255 || g > 255 || b > 255 || w > 255) {
						Toast.makeText(mActivity, R.string.input_0_255, Toast.LENGTH_SHORT).show();
						return;
					}
					
					light.setR((byte)r);
					light.setG((byte)g);
					light.setB((byte)b);
					light.setW((byte)w);
					color.setLight(light);
				}
				
				if(customColorCount < 3) {
					Toast.makeText(mActivity, R.string.at_Least_3, Toast.LENGTH_SHORT).show();
					return;
				}
				
				showLoading();
				mHelper.waveColorListConfig(list, 3000, new IResultCallback() {
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
									goBack();
								}else {
									showErrTips(cd);
								}
							}
						});
					}
				});
			}
		}
	}
}
