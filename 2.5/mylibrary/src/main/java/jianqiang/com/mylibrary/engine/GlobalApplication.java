package jianqiang.com.mylibrary.engine;

import android.app.Application;
import android.content.Context;

import jianqiang.com.mylibrary.cache.CacheManager;

/**
 * Created by jianqiang on 16/11/22.
 */
public class GlobalApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        CacheManager.getInstance().initCacheDir();
    }

    public static Context getContextObject(){
        return context;
    }
}
