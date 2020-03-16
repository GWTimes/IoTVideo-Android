package com.tencentcs.iotvideo.http;

import android.os.Build;

import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.RequiresApi;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class TencentcsHttpServiceFactory {
    private static final String TAG = "TencentcsHttpServiceFactory";

    private static final int DEFAULT_CONNECT_TIMEOUT = 10;//默认超时时间10s
    private static final int DEFAULT_WRITE_TIMEOUT = 20;//默认超时时间20s
    private static final int DEFAULT_READ_TIMEOUT = 20;//默认超时时间20s

    private OkHttpClient.Builder mHttpClientBuilder;
    private TencentcsLogInterceptor mTencentcsLogInterceptor;
    private TencentcsPublicInterceptor mTencentcsPublicInterceptor;

    TencentcsHttpServiceFactory(String accessId, String secretKey, String token) {
        mHttpClientBuilder = new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)//连接超时时间
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)//读取超时时间设置
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)//写入超时时间设置
                .retryOnConnectionFailure(true);//错误重连

        mTencentcsPublicInterceptor = new TencentcsPublicInterceptor(accessId, secretKey, token);
        mTencentcsLogInterceptor = new TencentcsLogInterceptor();
        mHttpClientBuilder.addInterceptor(mTencentcsPublicInterceptor);
        mHttpClientBuilder.addInterceptor(mTencentcsLogInterceptor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mHttpClientBuilder
                    .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts24())
                    .hostnameVerifier(new TrustAllHostnameVerifier());
        } else {
            mHttpClientBuilder
                    .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
                    .hostnameVerifier(new TrustAllHostnameVerifier());
        }
    }

    <T> T createService(Class<T> service, String url) {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().
                client(mHttpClientBuilder.build()).
                addConverterFactory(GsonConverterFactory.create()).//设置Json数据的转换器为Gson
                addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加RxJava适配器
                .client(mHttpClientBuilder.build())
                .baseUrl(url);//添加服务器地址

        Retrofit retrofit = retrofitBuilder.build();
        T apiService;
        apiService = retrofit.create(service);
        return apiService;
    }

    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static class TrustAllCerts24 extends X509ExtendedTrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        /**
         * OKHTTP Android Q+要求实现该方法，{@link android.net.http.X509TrustManagerExtensions} 66行
         */
        public List<X509Certificate> checkServerTrusted(X509Certificate[] chain, String authType, String host) throws CertificateException {
            return Arrays.asList(getAcceptedIssuers());
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sslContext.init(null, new TrustManager[]{new TrustAllCerts24()}, new SecureRandom());
            } else {
                sslContext.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            }

            ssfFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ssfFactory;
    }
}
