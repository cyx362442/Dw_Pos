package com.duowei.dw_pos.httputils;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * Created by Administrator on 2017-04-18.
 */

public class NetUtils {
    private static final String TAG = "NetUtils";

    public static final String STATE_NETWORK_ERROR = "网络错误";
    public static final String STATE_LOAD_SUCCESS = "加载成功";

    private static OkHttpClient mClient = null;

    private NetUtils() {
    }

    public static void init() {
        if (mClient == null) {
            mClient = new OkHttpClient();
        }
    }

    private static void post(String url, String state, String sql, Callback callback) {
        if (url == null) {
            Log.w(TAG, "url为空");
            return;
        }

        RequestBody formBody = new FormBody.Builder()
                .add("State", state)
                .add("Ssql", toBase64(sql))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        if (callback != null) {
            mClient.newCall(request).enqueue(callback);
        } else {
            mClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                }
            });
        }
    }

    private static String toBase64(String sql) {
        return Base64.encodeToString(sql.replaceAll("\n", "").getBytes(), Base64.DEFAULT);
    }

    public static void post6(String url, String sql, Callback callback) {
        post(url, "6", sql, callback);
    }

    public static void post7(String url, String sql, Callback callback) {
        post(url, "7", sql, callback);
    }
}
