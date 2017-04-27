package jianqiang.com.youngheart.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import jianqiang.com.mylibrary.activity.BaseActivity;
import jianqiang.com.mylibrary.net.RequestCallback;
import jianqiang.com.youngheart.R;

public abstract class AppBaseActivity extends BaseActivity {

    protected ProgressDialog dlg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化ProgressDialog
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getResources().getString(R.string.str_loading));
        dialog.setCanceledOnTouchOutside(false);
        dlg = dialog;
    }

    public abstract class AbstractRequectCallback implements RequestCallback {
        @Override
        public void onFail(String errorMessage) {
            dlg.dismiss();

            new AlertDialog.Builder(AppBaseActivity.this)
                    .setTitle("出错啦")
                    .setMessage(errorMessage)
                    .setPositiveButton("确定", null)
                    .show();

        }

        @Override
        public void showDialog() {
            dlg.show();
        }

        @Override
        public void onSuccess(String content) {
            dlg.dismiss();
        }
    }
}