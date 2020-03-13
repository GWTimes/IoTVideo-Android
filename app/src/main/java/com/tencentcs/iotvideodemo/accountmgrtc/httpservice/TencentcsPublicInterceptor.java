package com.tencentcs.iotvideodemo.accountmgrtc.httpservice;

import android.text.TextUtils;

import com.tencentcs.iotvideo.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;

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
        Response response;
        response = chain.proceed(doRequestWithTC3(request));
        return response;
    }

    private Request addPublicHeader(Request request) {
        Request.Builder builder = request.newBuilder();
        builder.addHeader(HOST, "iotvideo.tencentcloudapi.com");
        builder.addHeader(XTCRegion, "ap-guangzhou");
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader(XTCToken, token);
        }

        return builder.build();
    }

    private Request doRequestWithTC3(Request request) {
        String endpoint = "iotvideo.tencentcloudapi.com";
        String httpRequestMethod = request.method();
        String contentType = "application/json; charset=utf-8";
        byte [] requestPayload = "{}".getBytes();
        TencentcsModel tencentcsModel = new TencentcsModel();
        HashMap<String, String> params = new HashMap<String, String>();
        tencentcsModel.toMap(params, "");
        LogUtils.i(TAG, "requestPayload = " + toJsonString(request));
        String canonicalUri = "/";
        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:" + contentType + "\nhost:" + endpoint + "\n";
        String signedHeaders = "content-type;host";

        String hashedRequestPayload = "";
        boolean isUnsignedPayload = false;
        if (isUnsignedPayload) {
            hashedRequestPayload = Sign.sha256Hex("UNSIGNED-PAYLOAD".getBytes());
        } else {
            hashedRequestPayload = Sign.sha256Hex(requestPayload);
        }
        String canonicalRequest = httpRequestMethod + "\n" + canonicalUri + "\n" + canonicalQueryString + "\n"
                + canonicalHeaders + "\n" + signedHeaders + "\n" + hashedRequestPayload;

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = sdf.format(new Date(Long.valueOf(timestamp + "000")));
        String service = endpoint.split("\\.")[0];
        String credentialScope = date + "/" + service + "/" + "tc3_request";
        String hashedCanonicalRequest = Sign.sha256Hex(canonicalRequest.getBytes());
        String stringToSign = "TC3-HMAC-SHA256\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;

        byte[] secretDate = Sign.hmac256(("TC3" + secretKey).getBytes(), date);
        byte[] secretService = Sign.hmac256(secretDate, service);
        byte[] secretSigning = Sign.hmac256(secretService, "tc3_request");
        String signature = Sign.byteToHex(Sign.hmac256(secretSigning, stringToSign)).toLowerCase();
        String authorization = "TC3-HMAC-SHA256 " + "Credential=" + secretId + "/" + credentialScope + ", "
                + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;

        Request.Builder requestBuilder = request.newBuilder();
        requestBuilder.addHeader(XTCTimestamp, timestamp);
        requestBuilder.addHeader(Authorization, authorization);
        return requestBuilder.build();
    }

    private String toJsonString(Request request) {
        TreeMap<String, Object> params = new TreeMap<>(); // TreeMap可以自动排序
        params.put(XTCAction, request.header(XTCAction));
        params.put(XTCVersion, request.header(XTCVersion));
        params.put(HOST, "iotvideo.tencentcloudapi.com");
        params.put(XTCRegion, "ap-guangzhou");
        if (!TextUtils.isEmpty(token)) {
            params.put(XTCToken, token);
        }

        HttpUrl httpUrl = request.url();
        Set<String> paramKeys = httpUrl.queryParameterNames();
        for (String key : paramKeys) {
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(httpUrl.queryParameter(key))) {
                params.put(key, httpUrl.queryParameter(key));
            }
        }

        //请求参数
        String method = request.method();
        if (method.equals("POST") || method.equals("PUT")) {
            RequestBody requestBody = request.body();
            String requestPayload = "";

            try {
                BufferedSink buffer = new Buffer();
                requestBody.writeTo(buffer);
                requestPayload = buffer.getBuffer().readUtf8();
                requestPayload = Sign.sha256Hex(requestPayload);
            } catch (Exception e) {
                e.printStackTrace();
            }

            params.put(PAYLOAD, requestPayload);
        }
        return map2String(params);
    }

    private static String map2String(TreeMap<String, Object> params) {
        StringBuilder s2s = new StringBuilder();
        // 签名时要求对参数进行字典排序，此处用TreeMap保证顺序
        for (String k : params.keySet()) {
            s2s.append(k).append(":").append(params.get(k).toString()).append("\n");
        }
        String str;
        str = s2s.toString().substring(0, s2s.length() - 1);
        return str;
    }
}
