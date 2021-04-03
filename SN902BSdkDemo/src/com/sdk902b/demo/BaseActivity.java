package com.sdk902b.demo;

import java.util.ArrayList;
import java.util.List;

import com.sdk902b.demo.util.ActivityUtil;
import com.sdk902b.demo.view.LoadingDialog;
import com.sleepace.sdk.constant.StatusCode;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.nox902b.Nox902BHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity implements OnClickListener {
	protected final String TAG = this.getClass().getSimpleName();
	protected ImageView ivBack;
	protected TextView tvTitle, tvRight;
	protected ImageView ivRight;
	protected BaseActivity mActivity;
	protected Nox902BHelper mHelper;
	private LoadingDialog mLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mActivity = this;
		mHelper = Nox902BHelper.getInstance(getApplicationContext());
	}

	protected void findView() {
		ivBack = (ImageView) findViewById(R.id.iv_back);
		ivRight = (ImageView) findViewById(R.id.iv_right);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvRight = (TextView) findViewById(R.id.tv_right);
	};

	protected void initListener() {
		if(ivBack != null){
			ivBack.setOnClickListener(this);
		}
	};

	protected void initUI() {
		
	};
	
	@Override
	public void onClick(View v) {
		if(v == ivBack){
			goBack();
		}
	}
	
	public void goBack() {
		finish();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(0, 0);
	}
	
	public void startActivity(Class clazz) {
		Intent intent = new Intent(mActivity, clazz);
		startActivity(intent);
	}
	
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(0, 0);
	}
	
	public void startActivityForResult(Class clazz, int requestCode) {
		Intent intent = new Intent(mActivity, clazz);
		startActivityForResult(intent, requestCode);
	}
	
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(0, 0);
	}
	
	public boolean showErrTips(CallbackData cd){
		if(!cd.isSuccess()){
			
			if(cd.getStatus() == StatusCode.DISCONNECT) {
				
				Dialog dialog = createErrorMsgDialog(this, getString(R.string.device_connect_fail), getString(R.string.device_w_ble_connect_failed_tip));
				dialog.show();
				
			}else {
				String errMsg = getErrMsg(cd.getStatus());
				Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
			}
			
//			new AlertDialog.Builder(this)
//			.setIcon(android.R.drawable.ic_dialog_info)
////			.setTitle(R.string.device_connect_fail)
//			.setMessage(errMsg)
//			.setPositiveButton(android.R.string.ok, null).create().show();
			
			return false;
		}
		return true;
	}
	
	
	public static Dialog createErrorMsgDialog(final Context context, final String title, final String msg) {
        final Dialog dialog = new Dialog(context, R.style.myDialog);
        dialog.setContentView(R.layout.dialog_error_msg);

        final TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        final TextView tvMsg = (TextView) dialog.findViewById(R.id.tv_msg);
        final Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);

        final OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnOk) {
                    dialog.dismiss();
                }
            }
        };

        tvTitle.setText(title);
        tvMsg.setText(msg);
        btnOk.setOnClickListener(clickListener);

        Window win = dialog.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
	
	public static Dialog createYesNoDialog(final Context context, final String title, final String msg, final String btnLeftLabel, final String btnRightLabel, final View.OnClickListener leftBtnListener, final View.OnClickListener rightBtnListener) {
		final Dialog dialog = new Dialog(context, R.style.myDialog);
		dialog.setContentView(R.layout.dialog_yes_no);
		
		final TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
		final TextView tvMsg = (TextView) dialog.findViewById(R.id.tv_msg);
		final Button btnLeft = (Button) dialog.findViewById(R.id.btn_left);
		final Button btnRight = (Button) dialog.findViewById(R.id.btn_right);
		
		final OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == btnLeft) {
					dialog.dismiss();
					if(leftBtnListener != null) {
						leftBtnListener.onClick(v);
					}
				}else if(v == btnRight) {
					dialog.dismiss();
					if(rightBtnListener != null) {
						rightBtnListener.onClick(v);
					}
				}
			}
		};
		
		tvTitle.setText(title);
		tvMsg.setText(msg);
		if(!TextUtils.isEmpty(btnLeftLabel)) {
			btnLeft.setText(btnLeftLabel);
		}
		if(!TextUtils.isEmpty(btnRightLabel)) {
			btnRight.setText(btnRightLabel);
		}
		btnLeft.setOnClickListener(clickListener);
		btnRight.setOnClickListener(clickListener);
		
		Window win = dialog.getWindow();
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		win.setAttributes(lp);
		
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}
	
	public String getErrMsg(int status){
		if(status == StatusCode.NOT_ENABLE){
			return getString(R.string.phone_bluetooth_not_open);
		}
		
		if(status == StatusCode.DISCONNECT){
			return getString(R.string.device_connect_fail);
		}
		
//		if(status == StatusCode.TIMEOUT){
//			return getString(R.string.opt_time_out);
//		}
//		
//		if(status == StatusCode.FAIL){
//			return getString(R.string.opt_fail);
//		}
		return "";
	}
	
	
	public void showLoading() {
        String tips = "";
        showLoading(tips);
    }

    public void showLoading(int resId) {
        String tips = getString(resId);
        showLoading(tips);
    }

    public void showLoading(String tips) {
        if (!ActivityUtil.isActivityAlive(this)) {
            return;
        }

        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
        }
        
        mLoadingDialog.show();
    }

    public boolean isLoadingDialogShowing() {
        return mLoadingDialog != null && mLoadingDialog.isShowing();
    }

    public void hideLoading() {
        if (!ActivityUtil.isActivityAlive(this)) {
            return;
        }
        if (isLoadingDialogShowing()) {
            mLoadingDialog.dismiss();
        }
    }
    
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			for(MyOnTouchListener touchListener : touchListeners) {
				touchListener.onTouch(ev);
			}
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private List<MyOnTouchListener> touchListeners = new ArrayList<BaseActivity.MyOnTouchListener>();
	
	public void registerTouchListener(MyOnTouchListener myOnTouchListener) {
		if(myOnTouchListener != null && !touchListeners.contains(myOnTouchListener)) {
			touchListeners.add(myOnTouchListener);
		}
    }
	
    public void unregisterTouchListener(MyOnTouchListener myOnTouchListener) {
    	touchListeners.remove(myOnTouchListener) ;
    }
    
    public interface MyOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }
}


















