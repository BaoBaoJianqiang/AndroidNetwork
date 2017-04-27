package jianqiang.com.mylibrary.net;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jianqiang.com.mylibrary.cache.CacheManager;
import jianqiang.com.mylibrary.engine.GlobalApplication;
import jianqiang.com.mylibrary.mockdata.MockService;
import jianqiang.com.mylibrary.utils.BaseUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.attr.tag;

/**
 * Created by jianqiang on 16/11/13.
 */
public class RemoteService {
    private static String serverName = "http://10.0.2.2:8888";        //模拟器
    //private static String serverName = "http://192.168.3.4:8888";       //真机,同一个网段

    // 区分get还是post的枚举
    public static final String REQUEST_GET = "get";
    public static final String REQUEST_POST = "post";

    //Tag，用于cancel，标记一类请求
    public static final String Tag = "tag";

    private static RemoteService service = null;
    private static OkHttpClient mOkHttpClient = null;
    private List<RequestParameter> parameter = null;


    protected Handler handler;

    private RemoteService() {
        handler = new Handler();
    }

    public static synchronized RemoteService getInstance() {
        if (service == null) {
            service = new RemoteService();
            mOkHttpClient = new OkHttpClient();

            //设置超时时间
            //参见：OkHttp3超时设置和超时异常捕获
            //http://blog.csdn.net/do168/article/details/51848895
            mOkHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
            mOkHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
            mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        }
        return service;
    }

    public void invoke(final String apiKey) {
        invoke(apiKey, null, null, false, -100);
    }

    public void invoke(final String apiKey,
                       final ArrayList<RequestParameter> params) {
        invoke(apiKey, params, null, false, -100);
    }

    public void invoke(final String apiKey, final RequestCallback callBack) {
        invoke(apiKey, null, callBack, false, -100);
    }

    public void invoke(final String apiKey, final ArrayList<RequestParameter> params, final RequestCallback callBack) {
        invoke(apiKey, params, callBack, false, -100);
    }

    public void invoke(final String apiKey,
                       final ArrayList<RequestParameter> params,
                       final RequestCallback callBack,
                       final boolean forceUpdate) {
        invoke(apiKey, params, callBack, forceUpdate, -100);
    }

    public void invoke(final String apiKey,
                       final ArrayList<RequestParameter> params,
                       final RequestCallback callBack,
                       final boolean forceUpdate,
                       final int retryTimes) {

        //自动转菊花
        if (callBack != null)
            callBack.showDialog();

        final int retryTimesInRecusive[] = new int[1];   //又要final,又要后期可修改,只好用数组
        retryTimesInRecusive[0] = retryTimes;

        final URLData urlData = UrlConfigManager.findURL(apiKey);

        //MockService
        if (urlData.getMockClass() != null) {
            try {
                MockService mockService = (MockService) Class.forName(urlData.getMockClass()).newInstance();
                String strResponse = mockService.getJsonData();
                final NetworkEntity entity = JSON.parseObject(strResponse, NetworkEntity.class);
                if (callBack != null) {
                    if (entity.isError == 0) {
                        callBack.onSuccess(entity.result);
                    } else {
                        callBack.onFail(entity.errorMessage);
                    }
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return;
        }

        //强制更新
        if (forceUpdate) {
            urlData.setExpires(0);
        }

        int retryTimesInConfig = urlData.getRetryTimes();
        //第一次进来(-100)，并且有重试机制
        if (retryTimesInRecusive[0] == -100 && retryTimesInConfig > 0) {
            retryTimesInRecusive[0] = retryTimesInConfig;
        }

        final String newUrl[] = new String[2];   //又要final,又要后期可修改,只好用数组
        newUrl[0] = serverName + urlData.getUrl();

        parameter = params;

        Callback internalCallback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //retry
                if (retryTimesInConfig > 0 && retryTimesInRecusive[0] > 0) {
                    invoke(apiKey, params, callBack, forceUpdate, --retryTimesInRecusive[0]);
                }

                if (callBack == null)
                    return;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFail("网络错误");
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String strServerTime = response.header("Date");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date serverDateUAT = format.parse(strServerTime);
                    GlobalApplication.delta = serverDateUAT.getTime() - System.currentTimeMillis();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                final NetworkEntity networkEntity = JSON.parseObject(response.body().string(),
                        NetworkEntity.class);
                if (networkEntity.isError == 1) {

                    if (callBack == null)
                        return;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFail(networkEntity.errorMessage);
                        }
                    });
                } else {
                    if (callBack == null)
                        return;

                    // 把成功获取到的数据记录到缓存
                    if (urlData.getNetType().equals(REQUEST_GET) && urlData.getExpires() > 0) {
                        CacheManager.getInstance().putFileCache(getFinalUrl(newUrl), networkEntity.result, urlData.getExpires());
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(networkEntity.result);
                        }
                    });
                }
            }
        };

        if (urlData.getNetType().equals(REQUEST_GET)) {
            // 添加参数
            final StringBuffer paramBuffer = new StringBuffer();
            if ((parameter != null) && (parameter.size() > 0)) {

                // 这里要对key进行排序
                sortKeys();

                for (final RequestParameter p : parameter) {
                    if (paramBuffer.length() == 0) {
                        paramBuffer.append(p.getName() + "="
                                + BaseUtils.UrlEncodeUnicode(p.getValue()));
                    } else {
                        paramBuffer.append("&" + p.getName() + "=" + BaseUtils.UrlEncodeUnicode(p.getValue()));
                    }
                }

                newUrl[1] = "?" + paramBuffer.toString();
            }


            //缓存逻辑，RxJava
            Observable<String> disk = Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {

                    if (urlData.getExpires() > 0) {
                        final String content = CacheManager.getInstance().getFileCache(getFinalUrl(newUrl));
                        if (content != null) {
                            if (callBack != null){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callBack.onSuccess(content);
                                    }
                                });
                            }

                            subscriber.onNext("");
                        }
                    }

                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io());

            Observable<String> network = Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    final Request request = new Request.Builder()
                            .url(getFinalUrl(newUrl)).tag(Tag)
                            .build();

                    Call call = mOkHttpClient.newCall(request);
                    call.enqueue(internalCallback);

                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io());

            //主要就是靠concat operator来实现
            Observable.concat(disk, network)
                    .first()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                                   @Override
                                   public void onCompleted() {
                                       int a = 1;
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       int a = 1;
                                   }

                                   @Override
                                   public void onNext(String s) {
                                       int a = 1;
                                   }
                               }
                    );

        } else if (urlData.getNetType().equals(REQUEST_POST)) {
            FormEncodingBuilder builder = new FormEncodingBuilder();

            // 添加参数
            if ((parameter != null) && (parameter.size() > 0)) {
                for (final RequestParameter p : parameter) {
                    builder.add(p.getName(), p.getValue());
                }
            }

            RequestBody requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(getFinalUrl(newUrl)).tag(Tag)
                    .post(requestBody)
                    .build();

            Call call = mOkHttpClient.newCall(request);
            call.enqueue(internalCallback);
        } else {
            //do something
        }
    }

    String getFinalUrl(String[] arrUrl) {
        if (arrUrl[1] != null) {
            return arrUrl[0] + arrUrl[1];
        } else {
            return arrUrl[0];
        }
    }

    void sortKeys() {
        for (int i = 1; i < parameter.size(); i++) {
            for (int j = i; j > 0; j--) {
                RequestParameter p1 = parameter.get(j - 1);
                RequestParameter p2 = parameter.get(j);
                if (compare(p1.getName(), p2.getName())) {
                    // 交互p1和p2这两个对象，写的超级恶心
                    String name = p1.getName();
                    String value = p1.getValue();

                    p1.setName(p2.getName());
                    p1.setValue(p2.getValue());

                    p2.setName(name);
                    p2.setValue(value);
                }
            }
        }
    }

    // 返回true说明str1大，返回false说明str2大
    boolean compare(String str1, String str2) {
        String uppStr1 = str1.toUpperCase();
        String uppStr2 = str2.toUpperCase();

        boolean str1IsLonger = true;
        int minLen = 0;

        if (str1.length() < str2.length()) {
            minLen = str1.length();
            str1IsLonger = false;
        } else {
            minLen = str2.length();
            str1IsLonger = true;
        }

        for (int index = 0; index < minLen; index++) {
            char ch1 = uppStr1.charAt(index);
            char ch2 = uppStr2.charAt(index);
            if (ch1 != ch2) {
                if (ch1 > ch2) {
                    return true; // str1大
                } else {
                    return false; // str2大
                }
            }
        }

        return str1IsLonger;
    }

    //可以通过tags来同时取消多个请求。当你构建一请求时，使用RequestBuilder.tag(tag)来分配一个标签。
    // 之后你就可以用OkHttpClient.cancel(tag)来取消所有带有这个tag的call。
    public void cancelAllRequest() {
        mOkHttpClient.cancel(Tag);
    }
}