package com.sdk902b.demo.fragment;

import com.sdk902b.demo.BaseActivity.MyOnTouchListener;
import com.sdk902b.demo.MainActivity;
import com.sleepace.sdk.nox902b.Nox902BHelper;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment implements OnClickListener{
	
	protected String TAG = getClass().getSimpleName();
	protected MainActivity mActivity;
	private Nox902BHelper mHelper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivity = (MainActivity) getActivity();
		mHelper = Nox902BHelper.getInstance(mActivity);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public Nox902BHelper getDeviceHelper() {
		return mHelper;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
	}


	protected void initListener() {
		// TODO Auto-generated method stub
	}

	protected void initUI() {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public void startActivity(Class clazz) {
		Intent intent = new Intent(mActivity, clazz);
		startActivity(intent);
	}
	
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		mActivity.overridePendingTransition(0, 0);
	}
	
	public void startActivityForResult(Class clazz, int requestCode) {
		Intent intent = new Intent(mActivity, clazz);
		startActivityForResult(intent, requestCode);
	}
	
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		mActivity.overridePendingTransition(0, 0);
	}
	
	public void registerTouchListener(MyOnTouchListener myOnTouchListener) {
		mActivity.registerTouchListener(myOnTouchListener);
    }
	
    public void unregisterTouchListener(MyOnTouchListener myOnTouchListener) {
    	mActivity.unregisterTouchListener(myOnTouchListener);
    }
	
}



