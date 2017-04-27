package jianqiang.com.youngheart.activity.others;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import jianqiang.com.youngheart.R;
import jianqiang.com.youngheart.activity.personcenter.PersonCenterActivity;
import jianqiang.com.youngheart.base.AppBaseActivity;
import jianqiang.com.youngheart.engine.AppConstants;


public class LoginNewActivity extends AppBaseActivity {
	private int loginTimes;
	private String strEmail;

	private EditText etPassword;
	private EditText etEmail;
	private Button btnLogin;

	@Override
	protected void initVariables() {
		loginTimes = -1;

		Bundle bundle = getIntent().getExtras();
		strEmail = bundle.getString(AppConstants.Email);
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);

		etEmail = (EditText)findViewById(R.id.email);
		etEmail.setText(strEmail);
		etPassword = (EditText)findViewById(R.id.password);

		//登录事件
		btnLogin = (Button)findViewById(R.id.sign_in_button);
		btnLogin.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						gotoLoginActivity();
					}
				});
	}

	@Override
	protected void loadData() {
		//获取2个MobileAPI，获取天气数据，获取城市数据
		loadWeatherData();
		loadCityData();
	}

	private void gotoLoginActivity() {
		Intent intent = new Intent(LoginNewActivity.this,
				PersonCenterActivity.class);
		startActivity(intent);
	}

	private void loadWeatherData() {
		//发起网络请求，代码从略
	}

	private void loadCityData() {
		//发起网络请求，代码从略
	}
}