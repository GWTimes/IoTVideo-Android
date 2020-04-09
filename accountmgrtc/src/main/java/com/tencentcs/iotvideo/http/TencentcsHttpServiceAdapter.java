package com.tencentcs.iotvideo.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.tencentcs.iotvideo.accountmgr.HttpService;
import com.tencentcs.iotvideo.data.BaseResult;
import com.tencentcs.iotvideo.data.CreateAppUsrResponse;
import com.tencentcs.iotvideo.data.CreateAppUsrResult;
import com.tencentcs.iotvideo.data.CreateBindingResponse;
import com.tencentcs.iotvideo.data.CreateBindingResult;
import com.tencentcs.iotvideo.data.CreateShareTokenResult;
import com.tencentcs.iotvideo.data.CreateStorageResponse;
import com.tencentcs.iotvideo.data.CreateStorageResult;
import com.tencentcs.iotvideo.data.CreateUsrTokenResponse;
import com.tencentcs.iotvideo.data.CreateUsrTokenResult;
import com.tencentcs.iotvideo.data.DeleteBindingResponse;
import com.tencentcs.iotvideo.data.DeleteBindingResult;
import com.tencentcs.iotvideo.data.DescribeBindDevResponse;
import com.tencentcs.iotvideo.data.DescribeBindDevResult;
import com.tencentcs.iotvideo.data.DescribeBindUsrResponse;
import com.tencentcs.iotvideo.data.DescribeBindUsrResult;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.Observer;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TencentcsHttpServiceAdapter implements HttpService {
    private static final String TAG = "TencentcsHttpServiceAdapter";

    //    private static final String TENCENTCS_API_URL = "http://14.22.4.147:80";
    private static final String TENCENTCS_API_URL = "https://iotvideo.tencentcloudapi.com";

    private static Gson mGson;
    private TencentcsHttpServiceFactory mTencentcsHttpServiceFactory;
    private TencentcsHttpInterface mHttpInterface;
    private static String mAccessId;

    /**
     * 注册 OkHttp和Retrofit
     */
    public void init(String accessId, String secretKey, String token) {
        mGson = new Gson();
        mTencentcsHttpServiceFactory = new TencentcsHttpServiceFactory(accessId, secretKey, token);
        mHttpInterface = mTencentcsHttpServiceFactory.createService(TencentcsHttpInterface.class, TENCENTCS_API_URL);
    }

    public static void setAccessId(String accessId) {
        mAccessId = accessId;
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

    private JsonObject toJson(Object object) {
        String jsonString = mGson.toJson(object);
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(jsonString).getAsJsonObject();
    }

    private <T> T toEntity(String jsonData, Class<T> type) {
        T result = null;
        try {
            result = mGson.fromJson(jsonData, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void mobileCheckCode(String mobileArea, String mobile, Integer flag, SubscriberListener subscriberListener) {

    }

    @Override
    public void emailCheckCode(String email, Integer flag, SubscriberListener subscriberListener) {

    }

    @Override
    public void mobileRegister(String mobileArea, String mobile, String pwd, String vcode, final SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CunionId", mobile);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "CreateAppUsr", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(new SubscriberListener() {
            @Override
            public void onStart() {
                subscriberListener.onStart();
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                String responseJson = response.getAsJsonObject("Response").toString();
                CreateAppUsrResponse createAppUsrResponse = toEntity(responseJson, CreateAppUsrResponse.class);
                CreateAppUsrResult createAppUsrResult = new CreateAppUsrResult();
                createAppUsrResult.setCode(0);
                createAppUsrResult.setMsg("Success");
                CreateAppUsrResult.DataBean dataBean = new CreateAppUsrResult.DataBean();
                dataBean.setAccessId(createAppUsrResponse.getAccessId());
                createAppUsrResult.setData(dataBean);
                mAccessId = createAppUsrResponse.getAccessId();
                LogUtils.i(TAG, "mAccessId = " + mAccessId);
                subscriberListener.onSuccess(toJson(createAppUsrResult));
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                subscriberListener.onFail(e);
            }
        });
        toSubscribe(observable, subscriber);
    }

    @Override
    public void emailRegister(String email, String pwd, String vcode, final SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CunionId", email);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "CreateAppUsr", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(new SubscriberListener() {
            @Override
            public void onStart() {
                subscriberListener.onStart();
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                String responseJson = response.getAsJsonObject("Response").toString();
                CreateAppUsrResponse createAppUsrResponse = toEntity(responseJson, CreateAppUsrResponse.class);
                if (createAppUsrResponse.getError() != null) {
                    subscriberListener.onFail(new Throwable(createAppUsrResponse.getError().toString()));
                    return;
                }
                CreateAppUsrResult createAppUsrResult = new CreateAppUsrResult();
                createAppUsrResult.setCode(0);
                createAppUsrResult.setMsg("Success");
                CreateAppUsrResult.DataBean dataBean = new CreateAppUsrResult.DataBean();
                dataBean.setAccessId(createAppUsrResponse.getAccessId());
                createAppUsrResult.setData(dataBean);
                mAccessId = createAppUsrResponse.getAccessId();
                LogUtils.i(TAG, "mAccessId = " + mAccessId);
                subscriberListener.onSuccess(toJson(createAppUsrResult));
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                subscriberListener.onFail(e);
            }
        });
        toSubscribe(observable, subscriber);
    }

    @Override
    public void accountLogin(String account, String pwd, String uniqueId, final SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", mAccessId);
        jsonObject.addProperty("UniqueId", uniqueId);
        jsonObject.addProperty("TtlMinutes", 30 * 24 * 60);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "CreateUsrToken", "2019-11-26", jsonObject);
        LogUtils.i(TAG, "accountLogin jsonObject = " + jsonObject.toString());
        final Observer<JsonObject> subscriber = new Observer<>(new SubscriberListener() {
            @Override
            public void onStart() {
                subscriberListener.onStart();
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                String responseJson = response.getAsJsonObject("Response").toString();
                CreateUsrTokenResponse createUsrTokenResponse = toEntity(responseJson, CreateUsrTokenResponse.class);
                if (createUsrTokenResponse.getError() != null) {
                    subscriberListener.onFail(new Throwable(createUsrTokenResponse.getError().toString()));
                    return;
                }
                CreateUsrTokenResult createUsrTokenResult = new CreateUsrTokenResult();
                createUsrTokenResult.setCode(0);
                createUsrTokenResult.setMsg("Success");
                CreateUsrTokenResult.DataBean dataBean = new CreateUsrTokenResult.DataBean();
                dataBean.setAccessToken(createUsrTokenResponse.getAccessToken());
                dataBean.setAccessId(createUsrTokenResponse.getAccessId());
                dataBean.setExpireTime(createUsrTokenResponse.getExpireTime());
                createUsrTokenResult.setData(dataBean);
                subscriberListener.onSuccess(toJson(createUsrTokenResult));
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                subscriberListener.onFail(e);
            }
        });
        toSubscribe(observable, subscriber);
    }

    @Override
    public void thirdLogin(Integer thirdType, String code, SubscriberListener subscriberListener) {

    }

    @Override
    public void thirdBindAccount(Integer thirdType, String code, String account, String pwd, SubscriberListener subscriberListener) {

    }

    @Override
    public void thirdUnbind(Integer authType, SubscriberListener subscriberListener) {

    }

    @Override
    public void getAccessToken(String uniqueId, SubscriberListener subscriberListener) {

    }

    @Override
    public void logout(SubscriberListener subscriberListener) {
        subscriberListener.onSuccess(toJson(new BaseResult()));
    }

    @Override
    public void mobileResetPwd(String mobileArea, String mobile, String pwd, String vcode, SubscriberListener subscriberListener) {

    }

    @Override
    public void emailResetPwd(String email, String pwd, String vcode, SubscriberListener subscriberListener) {

    }

    @Override
    public void modifyPwd(String oldPwd, String pwd, SubscriberListener subscriberListener) {

    }

    @Override
    public void modifyInfo(Map<String, String> infoMap, SubscriberListener subscriberListener) {

    }

    @Override
    public void queryInfo(SubscriberListener subscriberListener) {

    }

    @Override
    public void replaceToken(String uniqueId, SubscriberListener subscriberListener) {

    }

    @Override
    public void findUser(String mobileArea, String condition, final SubscriberListener subscriberListener) {
        subscriberListener.onFail(new Throwable("暂不支持用户名分享，请输入AccessId"));
    }

    @Override
    public void pushTokenBind(String xingeToken, SubscriberListener subscriberListener) {

    }

    @Override
    public void queryUrl(SubscriberListener subscriberListener) {

    }

    @Override
    public void uploadLog(File logfile, SubscriberListener subscriberListener) {

    }

    @Override
    public void getAuthInfo(SubscriberListener subscriberListener) {

    }

    @Override
    public void feedbackSubmit(Integer type, String content, String url, String logUrl, SubscriberListener subscriberListener) {

    }

    @Override
    public void feedbackList(SubscriberListener subscriberListener) {

    }

    @Override
    public void feedbackDetail(String feedbackId, SubscriberListener subscriberListener) {

    }

    @Override
    public void noticeList(Integer currentPage, Integer pageSize, SubscriberListener subscriberListener) {

    }

    @Override
    public void noticeDetail(String noticeId, SubscriberListener subscriberListener) {

    }

    @Override
    public void listSharedUsers(final String devId, final SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", mAccessId);
        jsonObject.addProperty("Tid", devId);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "DescribeBindUsr", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(new SubscriberListener() {
            @Override
            public void onStart() {
                subscriberListener.onStart();
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                String responseJson = response.getAsJsonObject("Response").toString();
                DescribeBindUsrResponse describeBindUsrResponse = toEntity(responseJson, DescribeBindUsrResponse.class);
                LogUtils.i(TAG, "describeBindUsrResponse = " + describeBindUsrResponse.toString());
                if (describeBindUsrResponse.getError() != null) {
                    subscriberListener.onFail(new Throwable(describeBindUsrResponse.getError().toString()));
                    return;
                }
                DescribeBindUsrResult describeBindUsrResult = new DescribeBindUsrResult();
                describeBindUsrResult.setCode(0);
                describeBindUsrResult.setMsg("Success");
                List<DescribeBindUsrResult.DataBean.User> users = new ArrayList<>();
                if (describeBindUsrResponse.getData() != null) {
                    for (DescribeBindUsrResponse.DataBean device : describeBindUsrResponse.getData()) {
                        DescribeBindUsrResult.DataBean.User user = new DescribeBindUsrResult.DataBean.User();
                        user.setAccessId(device.getAccessId());
                        users.add(user);
                    }
                }
                DescribeBindUsrResult.DataBean dataBean = new DescribeBindUsrResult.DataBean();
                dataBean.setUsers(users);
                describeBindUsrResult.setData(dataBean);
                subscriberListener.onSuccess(toJson(describeBindUsrResult));
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                subscriberListener.onFail(e);
            }
        });
        toSubscribe(observable, subscriber);
    }

    @Override
    public void accountShare(String shareId, String devId, final SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", shareId);
        jsonObject.addProperty("Tid", devId);
        jsonObject.addProperty("Role", "guest");
        jsonObject.addProperty("ForceBind", false);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "CreateBinding", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(new SubscriberListener() {
            @Override
            public void onStart() {
                subscriberListener.onStart();
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                String responseJson = response.getAsJsonObject("Response").toString();
                CreateBindingResponse createBindingResponse = toEntity(responseJson, CreateBindingResponse.class);
                if (createBindingResponse.getError() != null) {
                    subscriberListener.onFail(new Throwable(createBindingResponse.getError().toString()));
                    return;
                }
                CreateBindingResult createBindingResult = new CreateBindingResult();
                createBindingResult.setCode(0);
                createBindingResult.setMsg("Success");
                CreateBindingResult.DataBean dataBean = new CreateBindingResult.DataBean();
                dataBean.setDevToken(createBindingResponse.getAccessToken());
                createBindingResult.setData(dataBean);
                subscriberListener.onSuccess(toJson(createBindingResult));
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                subscriberListener.onFail(e);
            }
        });
        toSubscribe(observable, subscriber);
    }

    @Override
    public void acceptShare(String ownerId, String devId, Integer accept, SubscriberListener subscriberListener) {

    }

    @Override
    public void genShareQrcode(String devId, String deviceName, String userName, SubscriberListener subscriberListener) {
        CreateShareTokenResult createShareTokenResult = new CreateShareTokenResult();
        CreateShareTokenResult.DataBean dataBean = new CreateShareTokenResult.DataBean();
        dataBean.setQrcodeToken(Long.valueOf(mAccessId));
        createShareTokenResult.setCode(0);
        createShareTokenResult.setMsg("Success");
        createShareTokenResult.setData(dataBean);
        subscriberListener.onSuccess(toJson(createShareTokenResult));
    }

    @Override
    public void scanShareQrcode(String qrcodeToken, SubscriberListener subscriberListener) {

    }

    @Override
    public void cancelShare(String devId, String targetId, SubscriberListener subscriberListener) {

    }

    @Override
    public void listHotValue(String countryCode, SubscriberListener subscriberListener) {

    }

    @Override
    public void vasList(String countryCode, String serviceType, SubscriberListener subscriberListener) {

    }

    @Override
    public void vasServiceOutline(String devId, SubscriberListener subscriberListener) {

    }

    @Override
    public void vasServiceList(String devId, SubscriberListener subscriberListener) {

    }

    @Override
    public void vasOrderGenerate(String packageNo, String devId, Integer timezone, String couponCode, SubscriberListener subscriberListener) {

    }

    @Override
    public void vasOrderQuery(String orderId, SubscriberListener subscriberListener) {

    }

    @Override
    public void vasOrderList(String devId, Integer orderStatus, SubscriberListener subscriberListener) {

    }

    @Override
    public void vasOrderOverview(String devId, SubscriberListener subscriberListener) {

    }

    @Override
    public void vasPaymentGenerate(String orderId, String payType, SubscriberListener subscriberListener) {

    }

    @Override
    public void vasOrderResult(String orderId, SubscriberListener subscriberListener) {

    }

    @Override
    public void cloudStorageList(String orderId, Integer timeZone, SubscriberListener subscriberListener) {

    }

    @Override
    public void cloudStoragePlayback(String devId, Integer timeZone, long startTime, long endTime, SubscriberListener subscriberListener) {

    }

    @Override
    public void cloudStorageSpeedPlay(String devId, long startTime, Integer speed, SubscriberListener subscriberListener) {

    }

    @Override
    public void cloudStorageDownload(String devId, Integer timeZone, long dateTime, SubscriberListener subscriberListener) {

    }

    @Override
    public void cloudStorageCreate(Integer cid, String tid, String pkgId, Integer type, int startTime, int endTime, int storageLen, final SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("PkgId", pkgId);
        jsonObject.addProperty("Tid", tid);
        jsonObject.addProperty("UserTag", mAccessId);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "CreateStorage", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(new SubscriberListener() {
            @Override
            public void onStart() {
                subscriberListener.onStart();
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                String responseJson = response.getAsJsonObject("Response").toString();
                CreateStorageResponse createStorageResponse = toEntity(responseJson, CreateStorageResponse.class);
                if (createStorageResponse.getError() != null) {
                    subscriberListener.onFail(new Throwable(createStorageResponse.getError().toString()));
                    return;
                }
                CreateStorageResult createStorageResult = new CreateStorageResult();
                createStorageResult.setCode(0);
                createStorageResult.setMsg("Success");
                subscriberListener.onSuccess(toJson(createStorageResult));
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                subscriberListener.onFail(e);
            }
        });
        toSubscribe(observable, subscriber);
    }

    @Override
    public void couponOwnerList(SubscriberListener subscriberListener) {

    }

    @Override
    public void couponPopPromotion(String promotionId, SubscriberListener subscriberListener) {

    }

    @Override
    public void couponPopAssignedCouponList(String couponIds, SubscriberListener subscriberListener) {

    }

    @Override
    public void couponPopReceive(String couponIds, SubscriberListener subscriberListener) {

    }

    @Override
    public void couponAvailableList(String packageNo, SubscriberListener subscriberListener) {

    }

    @Override
    public void couponVoucherPackInfo(String voucherCode, SubscriberListener subscriberListener) {

    }

    @Override
    public void couponVoucherExchange(String voucherCode, String devId, Integer timezone, SubscriberListener subscriberListener) {

    }

    @Override
    public void eventAlarmList(String devId, long startTime, long endTime, Integer lastId, Integer pageSize, SubscriberListener subscriberListener) {

    }

    @Override
    public void eventAlarmDelete(String eventIds, SubscriberListener subscriberListener) {

    }

    @Override
    public void prePositionList(String devId, SubscriberListener subscriberListener) {

    }

    @Override
    public void prePositionAdd(String devId, Integer serialNumber, String positionName, String positionUrl, String x, String y, SubscriberListener subscriberListener) {

    }

    @Override
    public void prePositionModify(String positionId, String positionName, SubscriberListener subscriberListener) {

    }

    @Override
    public void prePositionDelete(String positionId, SubscriberListener subscriberListener) {

    }

    @Override
    public void deviceBind(String devId, boolean forceBind, final SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", mAccessId);
        jsonObject.addProperty("Tid", devId);
        jsonObject.addProperty("Role", "owner");
        jsonObject.addProperty("ForceBind", true);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "CreateBinding", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(new SubscriberListener() {
            @Override
            public void onStart() {
                subscriberListener.onStart();
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                String responseJson = response.getAsJsonObject("Response").toString();
                CreateBindingResponse createBindingResponse = toEntity(responseJson, CreateBindingResponse.class);
                if (createBindingResponse.getError() != null) {
                    subscriberListener.onFail(new Throwable(createBindingResponse.getError().toString()));
                    return;
                }
                CreateBindingResult createBindingResult = new CreateBindingResult();
                createBindingResult.setCode(0);
                createBindingResult.setMsg("Success");
                CreateBindingResult.DataBean dataBean = new CreateBindingResult.DataBean();
                dataBean.setDevToken(createBindingResponse.getAccessToken());
                createBindingResult.setData(dataBean);
                subscriberListener.onSuccess(toJson(createBindingResult));
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                subscriberListener.onFail(e);
            }
        });
        toSubscribe(observable, subscriber);
    }

    @Override
    public void deviceUnbind(String devId, final SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", mAccessId);
        jsonObject.addProperty("Tid", devId);
        jsonObject.addProperty("Role", "owner");
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "DeleteBinding", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(new SubscriberListener() {
            @Override
            public void onStart() {
                subscriberListener.onStart();
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                String responseJson = response.getAsJsonObject("Response").toString();
                DeleteBindingResponse deleteBindingResponse = toEntity(responseJson, DeleteBindingResponse.class);
                if (deleteBindingResponse.getError() != null) {
                    subscriberListener.onFail(new Throwable(deleteBindingResponse.getError().toString()));
                    return;
                }
                DeleteBindingResult deleteBindingResult = new DeleteBindingResult();
                deleteBindingResult.setCode(0);
                deleteBindingResult.setMsg("Success");
                subscriberListener.onSuccess(toJson(deleteBindingResult));
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                subscriberListener.onFail(e);
            }
        });
        toSubscribe(observable, subscriber);
    }

    @Override
    public void deviceList(final SubscriberListener subscriberListener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AccessId", mAccessId);
        final Observable<JsonObject> observable = mHttpInterface.tencentcsApi(
                "DescribeBindDev", "2019-11-26", jsonObject);
        final Observer<JsonObject> subscriber = new Observer<>(new SubscriberListener() {
            @Override
            public void onStart() {
                subscriberListener.onStart();
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                String responseJson = response.getAsJsonObject("Response").toString();
                DescribeBindDevResponse describeBindDevResponse = toEntity(responseJson, DescribeBindDevResponse.class);
                if (describeBindDevResponse.getError() != null) {
                    subscriberListener.onFail(new Throwable(describeBindDevResponse.getError().toString()));
                    return;
                }
                DescribeBindDevResult describeBindDevResult = new DescribeBindDevResult();
                describeBindDevResult.setCode(0);
                describeBindDevResult.setMsg("Success");
                List<DescribeBindDevResult.DataBean> dataBeanList = new ArrayList<>();
                if (describeBindDevResponse.getData() != null) {
                    for (DescribeBindDevResponse.Device device : describeBindDevResponse.getData()) {
                        DescribeBindDevResult.DataBean dataBean = new DescribeBindDevResult.DataBean();
                        dataBean.setDeviceMode(device.getDeviceModel());
                        dataBean.setDeviceName(device.getDeviceName());
                        dataBean.setDevId(device.getTid());
                        dataBean.setShareType(device.getRole());
                        dataBeanList.add(dataBean);
                    }
                }
                describeBindDevResult.setData(dataBeanList);
                subscriberListener.onSuccess(toJson(describeBindDevResult));
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                subscriberListener.onFail(e);
            }
        });
        toSubscribe(observable, subscriber);
    }
}
