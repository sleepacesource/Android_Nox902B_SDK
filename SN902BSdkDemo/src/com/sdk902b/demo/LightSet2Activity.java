package com.sdk902b.demo;

import com.sdk902b.demo.util.Utils;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.SdkLog;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LightSet2Activity extends BaseActivity {
	private EditText etR, etG, etB, etW, etBrightness;
	private Button btnSendColor, btnSendBrightness;
    public static final int TYPE_SMALLNIGHT = 1;
    public static final int TYPE_TIMER_LGIHT = 2;
    public static final int TYPE_TIMER_SLEEPAID =3;
    private int type = 0;
    private SLPLight light;
    private byte brightness;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_light_set);
		type = getIntent().getIntExtra("type", 0);
		light = (SLPLight) getIntent().getSerializableExtra("light");
		brightness = getIntent().getByteExtra("brightness", (byte)0);
		findView();
		initListener();
		initUI();
	}

	public void initUI() {
		tvTitle.setText(getString(R.string.setLight));
		tvRight.setText(getString(R.string.save));
		etR.setText(String.valueOf(light.getR() & 0xff));
		etG.setText(String.valueOf(light.getG() & 0xff));
		etB.setText(String.valueOf(light.getB() & 0xff));
		etW.setText(String.valueOf(light.getW() & 0xff));
		etBrightness.setText(String.valueOf(brightness));
		SdkLog.log(TAG+" initUI type:" + type);
		if(type == TYPE_TIMER_SLEEPAID) {
			etR.setFocusable(false);
			etR.setBackgroundResource(R.drawable.execute_progress_win_bg);
			etB.setFocusable(false);
			etB.setBackgroundResource(R.drawable.execute_progress_win_bg);
			etW.setFocusable(false);
			etW.setBackgroundResource(R.drawable.execute_progress_win_bg);
		}
	}

	public void findView() {
		super.findView();
		etR = (EditText) findViewById(R.id.et_r);
		etG = (EditText) findViewById(R.id.et_g);
		etB = (EditText) findViewById(R.id.et_b);
		etW = (EditText) findViewById(R.id.et_w);
		etBrightness = (EditText) findViewById(R.id.et_brightness);
		btnSendColor = (Button) findViewById(R.id.btn_w);
		btnSendBrightness = (Button) findViewById(R.id.btn_brightness);
	}

	public void initListener() {
		super.initListener();
		tvRight.setOnClickListener(this);
		btnSendColor.setOnClickListener(this);
		btnSendBrightness.setOnClickListener(this);
		etR.addTextChangedListener(rgbwWatcher);
		etG.addTextChangedListener(rgbwWatcher);
		etB.addTextChangedListener(rgbwWatcher);
		etW.addTextChangedListener(rgbwWatcher);
		etBrightness.addTextChangedListener(brightnessWatcher);
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
				if(type == TYPE_TIMER_SLEEPAID) {
					if (rgbw > 120) {
						Toast.makeText(mActivity, R.string.input_0_120, Toast.LENGTH_SHORT).show();
					}
				}else {
					if (rgbw > 255) {
						Toast.makeText(mActivity, R.string.input_0_255, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	};

	private TextWatcher brightnessWatcher = new TextWatcher() {

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
				Utils.inputTips(etBrightness, 100);
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == tvRight) {
			if(type == TYPE_TIMER_SLEEPAID && Utils.inputTips(etG, 120)) {
				return;
			}
			
			String strR = etR.getText().toString();
			String strG = etG.getText().toString();
			String strB = etB.getText().toString();
			String strW = etW.getText().toString();
			String strBrightness = etBrightness.getText().toString();
			
			if(TextUtils.isEmpty(strR)){
				strR="0";
			}
			if(TextUtils.isEmpty(strG)){
				strG="0";
			}
			if(TextUtils.isEmpty(strB)){
				strB="0";
			}
			if(TextUtils.isEmpty(strW)){
				strW="0";
			}
			if(TextUtils.isEmpty(strBrightness)){
				strBrightness="0";
			}
			
			int r = (int) Integer.valueOf(strR);
			int g = (int) Integer.valueOf(strG);
			int b = (int) Integer.valueOf(strB);
			int w = (int) Integer.valueOf(strW);
			int bri = (int) Integer.valueOf(strBrightness);
			if(r > 255 || g > 255 || b > 255 || w > 255) {
				Toast.makeText(mActivity, R.string.input_0_255, Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(bri > 100) {
				Toast.makeText(mActivity, R.string.input_0_100, Toast.LENGTH_SHORT).show();
				return;
			}
			
			light.setR((byte)r);
			light.setG((byte)g);
			light.setB((byte)b);
			light.setW((byte)w);
			brightness = (byte)bri;
			
			Intent data = new Intent();			
			data.putExtra("light", light);			
			data.putExtra("brightness", brightness);			
			setResult(RESULT_OK, data);
			goBack();
		}else if (v == btnSendColor) {
			if(type == TYPE_TIMER_SLEEPAID && Utils.inputTips(etG, 120)) {
				return;
			}
			
			if (Utils.inputTips(etR, 255)) {
				return;
			}
			if (Utils.inputTips(etG, 255)) {
				return;
			}
			if (Utils.inputTips(etB, 255)) {
				return;
			}
			if (Utils.inputTips(etW, 255)) {
				return;
			}

			String strR = etR.getText().toString();
			String strG = etG.getText().toString();
			String strB = etB.getText().toString();
			String strW = etW.getText().toString();

			String strBrightness = etBrightness.getText().toString();
			if (!TextUtils.isEmpty(strBrightness)) {
				brightness = (byte) (int) Integer.valueOf(strBrightness);
			}
			
			if(brightness > 100) {
				Toast.makeText(mActivity, R.string.input_0_100, Toast.LENGTH_SHORT).show();
				return;
			}

			byte r = (byte) (int) Integer.valueOf(strR);
			byte g = (byte) (int) Integer.valueOf(strG);
			byte b = (byte) (int) Integer.valueOf(strB);
			byte w = (byte) (int) Integer.valueOf(strW);

			light.setR(r);
			light.setG(g);
			light.setB(b);
			light.setW(w);

			mHelper.turnOnColorLight(light, brightness, 3000,
					new IResultCallback() {
						@Override
						public void onResultCallback(CallbackData cd) {
							// TODO Auto-generated method stub

						}
					});
		} else if (v == btnSendBrightness) {

			if (Utils.inputTips(etBrightness, 100)) {
				return;
			}

			String strBrightness = etBrightness.getText().toString();
			brightness = (byte) (int) Integer.valueOf(strBrightness);

			mHelper.lightBrightness(brightness, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub

				}
			});

		}

	}

}
