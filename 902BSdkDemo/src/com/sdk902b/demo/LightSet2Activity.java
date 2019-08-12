package com.sdk902b.demo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sdk902b.demo.R;
import com.sdk902b.demo.util.Utils;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;

public class LightSet2Activity extends BaseActivity {
	private EditText etR, etG, etB, etW, etBrightness;
	private Button btnSendColor, btnSendBrightness, btnCloseLight;

    public static final String EXTRA_LIGHT_CONFIG = "extra_light_config";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_light_set);
		findView();
		initUI();
		initListener();

	}

	public void initUI() {
		tvTitle.setText(getString(R.string.setLight));

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
		btnCloseLight = (Button) findViewById(R.id.btn_close_light);

	}

	public void initListener() {
		btnSendColor.setOnClickListener(this);
		btnSendBrightness.setOnClickListener(this);
		btnCloseLight.setOnClickListener(this);
		etR.addTextChangedListener(rgbwWatcher);
		etG.addTextChangedListener(rgbwWatcher);
		etB.addTextChangedListener(rgbwWatcher);
		etW.addTextChangedListener(rgbwWatcher);
		etBrightness.addTextChangedListener(brightnessWatcher);
	
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
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
				byte r = (byte) (int) Integer.valueOf(strR);
				byte g = (byte) (int) Integer.valueOf(strG);
				byte b = (byte) (int) Integer.valueOf(strB);
				byte w = (byte) (int) Integer.valueOf(strW);
				byte brightness = (byte) (int) Integer.valueOf(strBrightness);
				SLPLight light = new SLPLight();
				light.setR(r);
				light.setG(g);
				light.setB(b);
				light.setW(w);
				Intent data = new Intent();			
				data.putExtra(EXTRA_LIGHT_CONFIG, light);			
				setResult(RESULT_OK, data);
				finish();
			}
		});
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
					Toast.makeText(mActivity, R.string.input_0_255,
							Toast.LENGTH_SHORT).show();
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
		if (v == btnSendColor) {

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

			byte brightness = 50;
			String strBrightness = etBrightness.getText().toString();
			if (!TextUtils.isEmpty(strBrightness)) {
				brightness = (byte) (int) Integer.valueOf(strBrightness);
			}

			byte r = (byte) (int) Integer.valueOf(strR);
			byte g = (byte) (int) Integer.valueOf(strG);
			byte b = (byte) (int) Integer.valueOf(strB);
			byte w = (byte) (int) Integer.valueOf(strW);

			SLPLight light = new SLPLight();
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
			byte brightness = (byte) (int) Integer.valueOf(strBrightness);

			mHelper.lightBrightness(brightness, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub

				}
			});

		} else if (v == btnCloseLight) {
			mHelper.turnOffLight(3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub

				}
			});
		}

	}

}
