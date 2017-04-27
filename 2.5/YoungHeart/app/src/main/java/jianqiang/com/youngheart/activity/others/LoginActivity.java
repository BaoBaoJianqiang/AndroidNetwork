package jianqiang.com.youngheart.activity.others;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import jianqiang.com.youngheart.R;
import jianqiang.com.youngheart.activity.personcenter.PersonCenterActivity;
import jianqiang.com.youngheart.engine.AppConstants;
import jianqiang.com.youngheart.engine.GlobalVariables;
import jianqiang.com.youngheart.entity.CinemaBean;

public class LoginActivity extends Activity {
	private int loginTimes;
	private String strEmail;
	private String userName;

	private EditText etPassword;
	private EditText etEmail;

	private String cinemaName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginTimes = -1;

		Bundle bundle = getIntent().getExtras();
		strEmail = bundle.getString(AppConstants.Email);

		// 使用全局变量的方式传值
		CinemaBean cinema = GlobalVariables.Cinema;
		if (cinema != null) {
			cinemaName = cinema.getCinemaName();
		} else {
			cinemaName = "";
		}

		etEmail = (EditText) findViewById(R.id.email);
		etEmail.setText(strEmail);
		etPassword = (EditText) findViewById(R.id.password);

		// 登录事件
		Button btnLogin = (Button) findViewById(R.id.sign_in_button);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoLoginActivity();
			}
		});

		// 获取2个MobileAPI，获取天气数据，获取城市数据
		loadWeatherData();
		loadCityData();
	}

	private void gotoLoginActivity() {
		Intent intent = new Intent(LoginActivity.this,
				PersonCenterActivity.class);
		startActivity(intent);
	}

	private void loadWeatherData() {
		// 发起网络请求，代码从略
	}

	private void loadCityData() {
		// 发起网络请求，代码从略
	}
}