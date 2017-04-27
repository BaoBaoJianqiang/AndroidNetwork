package jianqiang.com.youngheart.activity.personcenter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import jianqiang.com.youngheart.R;
import jianqiang.com.youngheart.base.AppBaseActivity;

public class PersonCenterActivity extends AppBaseActivity {
	TextView tvPersonCenter;
	
	@Override
	protected void initVariables() {

	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_personcenter);	
		
		tvPersonCenter = (TextView)findViewById(R.id.tvPersonCenter);
	}

	
	
	@Override
	protected void loadData() {

	}
	
	public void gotoRecharge(View Source) {
		tvPersonCenter.setText("Click");
	}
}
