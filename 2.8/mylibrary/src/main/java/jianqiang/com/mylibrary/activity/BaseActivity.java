package jianqiang.com.mylibrary.activity;

import android.app.Activity;
import android.os.Bundle;

import jianqiang.com.mylibrary.net.RemoteService;

public abstract class BaseActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initVariables();
		initViews(savedInstanceState);
		loadData();
	}

	protected abstract void initVariables();

	protected abstract void initViews(Bundle savedInstanceState);

	protected abstract void loadData();

	@Override
	public void onPause() {
		super.onPause();

		RemoteService.getInstance().cancelAllRequest();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		RemoteService.getInstance().cancelAllRequest();
	}
}