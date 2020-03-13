package com.tencentcs.iotvideodemo.accountmgrtc.httpservice;

import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.utils.rxjava.Observer;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

class TencentcsHttpServiceAdapter implements TencentcsHttpService {
    private static final String TAG = "TencentcsHttpServiceAdapter";

    private static final String TENCENTCS_API_URL = "http://14.22.4.147:80";
//    private static final String TENCENTCS_API_URL = "https://iotvideo.tencentcloudapi.com";

    private TencentcsHttpServiceFactory mTencentcsHttpServiceFactory;
    private TencentcsHttpInterface mHttpInterface;

    /**
     * 注册 OkHttp和Retrofit
     */
    void init(String accessId, String secretKey, String token) {
        mTencentcsHttpServiceFactory = new TencentcsHttpServiceFactory(accessId, secretKey, token);
        mHttpInterface = mTencentcsHttpServiceFactory.createService(TencentcsHttpInterface.class, TENCENTCS_API_URL);
    }

    /**
     * 统一线程处理 HTTP 请求
     *
     * @param o   被观察的对象
     * @param s   观察者
     * @param <T> 数据类型
     */
    private <T> void toSubscribe(final Observable<T> o, final Observer<T> s) {
        o.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {

            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    @Override
    public void CreateAppUsr(String CunionId, SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CunionId", CunionId);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "CreateAppUsr", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(subscriberListener);
        toSubscribe(observable, subscriber);
    }

    @Override
    public void CreateUsrToken(String AccessId, String UniqueId, Integer TtlMinutes, SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", AccessId);
        jsonObject.addProperty("UniqueId", UniqueId);
        jsonObject.addProperty("TtlMinutes", TtlMinutes);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "CreateUsrToken", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(subscriberListener);
        toSubscribe(observable, subscriber);
    }

    @Override
    public void CreateBinding(String AccessId, String Tid, String Role, Boolean ForceBind, SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", AccessId);
        jsonObject.addProperty("Tid", Tid);
        jsonObject.addProperty("Role", Role);
        jsonObject.addProperty("ForceBind", ForceBind);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "CreateBinding", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(subscriberListener);
        toSubscribe(observable, subscriber);
    }

    @Override
    public void DeleteBinding(String AccessId, String Tid, String Role, SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", AccessId);
        jsonObject.addProperty("Tid", Tid);
        jsonObject.addProperty("Role", Role);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "DeleteBinding", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(subscriberListener);
        toSubscribe(observable, subscriber);
    }

    @Override
    public void DescribeBindDev(String AccessId, SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", AccessId);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "DescribeBindDev", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(subscriberListener);
        toSubscribe(observable, subscriber);
    }

    @Override
    public void DescribeBindUsr(String AccessId, String Tid, SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", AccessId);
        jsonObject.addProperty("Tid", Tid);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "DescribeBindUsr", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(subscriberListener);
        toSubscribe(observable, subscriber);
    }

    @Override
    public void CreateDevToken(String AccessId, String[] Tids, Integer TtlMinutes, SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", AccessId);
        jsonObject.addProperty("Tids.N", "");
        jsonObject.addProperty("TtlMinutes", TtlMinutes);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "CreateAppUsr", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(subscriberListener);
        toSubscribe(observable, subscriber);
    }
}
