package com.tencentcs.iotvideo.http;

import com.tencentcs.iotvideo.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;

/**
 * 日志拦截器
 */
class TencentcsLogInterceptor implements Interceptor {
    private static final String TAG = "TencentcsLogInterceptor";

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        //Chain 里包含了request和response
        Request request = chain.request();
        long t1 = System.nanoTime();//请求发起的时间
        String bodyContent = "";
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            BufferedSink buffer = new Buffer();
            requestBody.writeTo(buffer);
            bodyContent = buffer.getBuffer().readUtf8();
        }
        LogUtils.i(TAG, String.format("REQUEST %s %s%n%sbody:%s",
                request.method(), request.url(), request.headers(), bodyContent));
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();//收到响应的时间
        //不能直接使用response.body（）.string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，
        //我们需要创建出一个新的response给应用层处理
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        LogUtils.i(TAG, String.format("RESPONSE：[%s]%njson:%s  %.1fms%n%s",
                response.request().url(),
                responseBody.string(),
                (t2 - t1) / 1e6d,
                response.headers()
        ));
        return response;
    }
}
