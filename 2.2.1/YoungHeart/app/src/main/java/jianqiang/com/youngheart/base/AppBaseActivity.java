package jianqiang.com.youngheart.base;

import android.support.v7.app.AlertDialog;

import jianqiang.com.mylibrary.activity.BaseActivity;
import jianqiang.com.mylibrary.net.RequestCallback;

public abstract class AppBaseActivity extends BaseActivity {

    public abstract class AbstractRequectCallback
            implements RequestCallback {
        @Override
        public void onFail(String errorMessage) {
            new AlertDialog.Builder(AppBaseActivity.this)
                    .setTitle("出错啦")
                    .setMessage(errorMessage)
                    .setPositiveButton("确定", null)
                    .show();

        }
    }
}