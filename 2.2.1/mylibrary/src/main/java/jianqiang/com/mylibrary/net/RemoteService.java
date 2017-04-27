package jianqiang.com.mylibrary.net;

import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

import jianqiang.com.mylibrary.activity.BaseActivity;
import jianqiang.com.mylibrary.utils.Utils;

/**
 * Created by jianqiang on 16/11/13.
 */
public class RemoteService {
    //private static String serverName = "http://10.0.2.2:8888";        //模拟器
    private static String serverName = "http://192.168.3.4:8888";       //真机,同一个网段
    private static RemoteService service = null;
    private static OkHttpClient mOkHttpClient = null;

    protected Handler handler;

    private RemoteService() {
        handler = new Handler();
    }

    public static synchronized RemoteService getInstance() {
        if (service == null) {
            service = new RemoteService();
            mOkHttpClient = new OkHttpClient();
        }
        return service;
    }

    public void invoke(final BaseActivity activity,
                       final String apiKey,
                       final ArrayList<RequestParameter> parameter,
                       final RequestCallback callBack) {
        final URLData urlData = UrlConfigManager.findURL(apiKey);

        String newUrl = serverName + urlData.getUrl();




        Callback internalCallback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFail("网络错误");
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final NetworkEntity networkEntity = JSON.parseObject(response.body().string(),
                        NetworkEntity.class);
                if(networkEntity.isError == 1) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFail(networkEntity.errorMessage);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(networkEntity.result);
                        }
                    });
                }
            }
        };



        if (urlData.getNetType().equals("get")) {
            // 添加参数
            final StringBuffer paramBuffer = new StringBuffer();
            if ((parameter != null) && (parameter.size() > 0)) {
                for (final RequestParameter p : parameter) {
                    if (paramBuffer.length() == 0) {
                        paramBuffer.append(p.getName() + "="
                                + Utils.UrlEncodeUnicode(p.getValue()));
                    } else {
                        paramBuffer.append("&" + p.getName() + "=" + Utils.UrlEncodeUnicode(p.getValue()));
                    }
                }

                newUrl = newUrl + "?" + paramBuffer.toString();
            }

            final Request request = new Request.Builder()
                    .url(newUrl)
                    .build();

            Call call = mOkHttpClient.newCall(request);
            call.enqueue(internalCallback);

        } else if (urlData.getNetType().equals("post")) {
            FormEncodingBuilder builder = new FormEncodingBuilder();

            // 添加参数
            if ((parameter != null) && (parameter.size() > 0)) {
                for (final RequestParameter p : parameter) {
                    builder.add(p.getName (), p.getValue());
                }
            }

            RequestBody requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(newUrl)
                    .post(requestBody)
                    .build();

            Call call = mOkHttpClient.newCall(request);
            call.enqueue(internalCallback);
        } else {
            //do something
        }
    }
}