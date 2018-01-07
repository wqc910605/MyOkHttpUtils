package com.wwf.myapplication.okhttputil;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Dell on 2017/12/4.
 */

public class OkHttpUtils {
    private static OkHttpUtils sOkHttpUtils = null;
    private Context mContext;
    private OkHttpClient mOkHttpClient = null;
    private Call mCall;
    //默认缓存时间30分钟
    private static int mCacheTime = 30 * 60;//单位是秒
    //判断url是否合法
    private boolean isUrl = true;//默认合法
    private OkHttpUtils(Context context) {
        this.mContext = context;
        mOkHttpClient = new OkHttpClient();
    }

    public static OkHttpUtils init(Context context) {
        if (sOkHttpUtils == null) {
            synchronized (OkHttpUtils.class) {
                if (sOkHttpUtils == null) {
                    sOkHttpUtils = new OkHttpUtils(context);
                }
            }
        }
        return sOkHttpUtils;
    }

    /**
     * @param context
     * @param cacheTime json数据缓存时间, 单位秒
     * @return
     */
    public static OkHttpUtils init(Context context, int cacheTime) {
        mCacheTime = cacheTime;
        init(context);
        return sOkHttpUtils;
    }

    //get有参数或者无参数
    public OkHttpUtils get(String url) {
        get(url, null);
        return this;
    }

    //get有参数形式 例如: https://snail-stg1.zysnail.com/snail/queryIdentities.do,后面的参数在map集合中添加
    public OkHttpUtils get(String url, Map<String, String> params) {
        if (!checkUrl(url)) return this;
        Request.Builder builder = new Request.Builder();
        if (params != null && params.entrySet() != null && params.entrySet().size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(url).append("?");
            for (Map.Entry<String, String> entrySet : params.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                sb.append(key).append("=").append(value).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
            Request request = builder.url(sb.toString()).build();
            mCall = mOkHttpClient.newCall(request);
            return this;
        } else {
            Request request = builder.url(url).build();
            mCall = mOkHttpClient.newCall(request);
            return this;
        }
    }

    //检查url合法性
    private boolean checkUrl(String url) {
        boolean b = url.startsWith("http://") || url.startsWith("https://");
        if (!TextUtils.isEmpty(url) && b) {
            return true;
        } else {
            Toast.makeText(mContext, "无效的请求地址, 请以http://开头或者https://开头", Toast.LENGTH_SHORT).show();
            isUrl = false;
        }
        return isUrl;
    }

    //post无参数请求
    public OkHttpUtils post(String url) {
        post(url, null);
        return this;
    }

    //post有参数请求, 请求参数使用map来封装

    /**
     * @param url    请求地址
     * @param params map集合封装的请求参数
     * @return
     */
    public OkHttpUtils post(String url, Map<String, String> params) {
        if (!checkUrl(url)) return this;
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.entrySet() != null && params.entrySet().size() > 0) {
            for (Map.Entry<String, String> entrySet : params.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                builder.add(key, value);
            }
            Request request = new Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .build();
            mCall = mOkHttpClient.newCall(request);
            return this;
        } else {
            Request request = new Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .build();
            mCall = mOkHttpClient.newCall(request);
            return this;
        }
    }

    //post上传json数据

    /**
     * @param url  请求地址
     * @param json 上传json数据
     * @return
     */
    public OkHttpUtils postJson(String url, String json) {
        if (!checkUrl(url)) return this;
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonType, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        mCall = mOkHttpClient.newCall(request);
        return this;
    }

    //post上传文件

    /**
     * @param url           请求路径
     * @param fileName      上传文件的文件名
     * @param filePath      上传文件的绝对路径
     * @param fileType      文件类型如图片 "img"
     * @param fileMideaType 上传文件的类型 例如:图片 image/png
     * @return
     */

    public OkHttpUtils postFile(String url, String fileName, String filePath, String fileType, String fileMideaType) {
        if (!checkUrl(url)) return this;
        File file = new File(filePath);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(fileType, fileName,
                        RequestBody.create(MediaType.parse(fileMideaType), file));

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        mCall = mOkHttpClient.newCall(request);
        return this;
    }

    /**
     * @param url      请求路径
     * @param fileName 文件名
     * @param filePath 文件绝对路径
     * @return
     */
    public OkHttpUtils postImg(String url, String fileName, String filePath) {
        if (!checkUrl(url)) return this;
        postFile(url, fileName, filePath, "img", "image/png");
        return this;
    }

    //网络请求在子线程, 返回的json数据是在主线程
    public void execute(final NetObserver netObserver) {
        //如果不是合法的url地址
        if (!isUrl || mCall == null) {
            return;
        }
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ThreadUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "亲, 网络不好:", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String json = response.body().string();
                    //缓存json数据, 默认保存时间为30分钟
                    ACache.get(mContext, "json").put("json", json, mCacheTime);
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //添加观察者
                            NetObservable.getInstance().addObserver(netObserver);
                            NetObservable.getInstance().setData(json);
                            //数据传过去之后, 将缓存数据置为空
                            NetObservable.getInstance().removeObserver(netObserver);
                        }
                    });
                } else {//请求码不正确
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "请求码不正确", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


}
