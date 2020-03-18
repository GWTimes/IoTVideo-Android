package com.tencentcs.iotvideo.http;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求头添加基础参数
 */
class TencentcsPublicInterceptor implements Interceptor {
    private static final String TAG = "TencentcsPublicInterceptor";

    //Host
    static final String HOST = "Host";
    //Payload
    static final String PAYLOAD = "Payload";
    //操作的接口名称
    static final String XTCAction = "X-TC-Action";
    //地域参数，用来标识希望操作哪个地域的数据
    static final String XTCRegion = "X-TC-Region";
    //当前 UNIX 时间戳，可记录发起 API 请求的时间
    static final String XTCTimestamp = "X-TC-Timestamp";
    //操作的 API 的版本。取值参考接口文档中入参公共参数 Version 的说明。
    static final String XTCVersion = "X-TC-Version";
    /**
     * HTTP 标准身份认证头部字段，例如：
     * TC3-HMAC-SHA256 Credential=AKIDEXAMPLE/Date/service/tc3_request, SignedHeaders=content-type;host, Signature=fe5f80f77d5fa3beca038a248ff027d0445342fe2855ddc963176630326f1024
     * 其中，
     * - TC3-HMAC-SHA256：签名方法，目前固定取该值；
     * - Credential：签名凭证，AKIDEXAMPLE 是 SecretId；Date 是 UTC 标准时间的日期，取值需要和公共参数 X-TC-Timestamp 换算的 UTC 标准时间日期一致；
     *   service 为产品名，通常为域名前缀，例如域名 cvm.tencentcloudapi.com 意味着产品名是 cvm。本产品取值为 cvm；
     * - SignedHeaders：参与签名计算的头部信息，content-type 和 host 为必选头部；
     * - Signature：签名摘要。
     */
    static final String Authorization = "Authorization";
    //临时证书所用的 Token ，需要结合临时密钥一起使用
    static final String XTCToken = "X-TC-Token";

    private String secretId;
    private String secretKey;
    private String token;

    TencentcsPublicInterceptor(String secretId, String secretKey, String token) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.token = token;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = addPublicHeader(chain.request());
        request = addAuthorizationHeader(request);
        Response response;
        response = chain.proceed(request);
        return response;
    }

    private Request addPublicHeader(Request request) {
        Request.Builder builder = request.newBuilder();
        builder.addHeader(HOST, "iotvideo.tencentcloudapi.com");
        builder.addHeader(XTCRegion, "ap-guangzhou");
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        builder.addHeader(XTCTimestamp, timestamp);
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader(XTCToken, token);
        }

        return builder.build();
    }

    private Request addAuthorizationHeader(Request request) {
        Request.Builder builder = request.newBuilder();
        String authorization = "";
        try {
            authorization = TencentcsSign.sign(secretId, secretKey, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.addHeader(Authorization, authorization);

        return builder.build();
    }
}
