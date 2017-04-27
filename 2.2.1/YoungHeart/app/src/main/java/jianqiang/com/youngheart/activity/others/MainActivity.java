package jianqiang.com.youngheart.activity.others;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

import jianqiang.com.mylibrary.net.RemoteService;
import jianqiang.com.mylibrary.net.RequestParameter;
import jianqiang.com.youngheart.R;
import jianqiang.com.youngheart.activity.personcenter.ListDemoActivity;
import jianqiang.com.youngheart.base.AppBaseActivity;
import jianqiang.com.youngheart.engine.AppConstants;
import jianqiang.com.youngheart.engine.GlobalVariables;
import jianqiang.com.youngheart.entity.CinemaBean;
import jianqiang.com.youngheart.entity.Movie;

public class MainActivity extends AppBaseActivity {
	private Button btnLogin;
	private Button btnLoginNew;

	private Button btnListDemo;

	private Button btnTestOKHttpGet;
	private Button btnTestOKHttpPost;

	private TextView tvMovie;

	@Override
	protected void initVariables() {

	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						LoginActivity.class);
				intent.putExtra(AppConstants.Email, "jianqiang.bao@qq.com");

				CinemaBean cinema = new CinemaBean();
				cinema.setCinemaId("1");
				cinema.setCinemaName("星美");

				//使用全局变量的方式传递参数
				GlobalVariables.Cinema = cinema;

				startActivity(intent);
			}
		});

		btnLoginNew = (Button) findViewById(R.id.btnLoginNew);
		btnLoginNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						LoginNewActivity.class);
				intent.putExtra(AppConstants.Email, "jianqiang.bao@qq.com");

				CinemaBean cinema = new CinemaBean();
				cinema.setCinemaId("1");
				cinema.setCinemaName("星美");

				//使用intent上挂可序列化实体的方式传递参数
				intent.putExtra(AppConstants.Cinema, cinema);

				startActivity(intent);
			}
		});


		btnListDemo = (Button) findViewById(R.id.btnListDemo);
		btnListDemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						ListDemoActivity.class);
				startActivity(intent);
			}
		});

		btnTestOKHttpGet = (Button) findViewById(R.id.btnTestOKHttpGet);
		btnTestOKHttpGet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testHttpGet();
			}
		});


		btnTestOKHttpPost = (Button) findViewById(R.id.btnTestOKHttpPost);
		btnTestOKHttpPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testHttpPost();
			}
		});

		tvMovie = (TextView)findViewById(R.id.tvMovie);
	}

	@Override
	protected void loadData() {

	}

	void testHttpGet() {
		ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
		RequestParameter rp1 = new RequestParameter("movieId", "1");
		params.add(rp1);

		RemoteService.getInstance().invoke(this, "getMovieDetail", params,
				new AbstractRequectCallback() {

			@Override
			public void onSuccess(String content) {
				Movie movie = JSON.parseObject(content, Movie.class);
				tvMovie.setText(movie.movieName);
			}
		});
	}

	void testHttpPost() {
		ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
		RequestParameter rp1 = new RequestParameter("username", "baobao");
		params.add(rp1);
		RequestParameter rp2 = new RequestParameter("password", "123456");
		params.add(rp2);

		RemoteService.getInstance().invoke(this, "registerUser", params, new AbstractRequectCallback() {

			@Override
			public void onSuccess(String content) {
				//dosomething
				String a = "";
			}

			@Override
			public void onFail(String errorMessage) {
				//有自己的特殊逻辑,不需要继承AppBaseActivity的onFail方法
			}
		});
	}
}