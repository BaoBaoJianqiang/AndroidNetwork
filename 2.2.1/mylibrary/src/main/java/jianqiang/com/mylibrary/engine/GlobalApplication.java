package jianqiang.com.mylibrary.engine;

import android.app.Application;
import android.content.Context;

/**
 * Created by jianqiang on 16/11/22.
 */
public class GlobalApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }

    public static Context getContextObject(){
        return context;
    }
}
