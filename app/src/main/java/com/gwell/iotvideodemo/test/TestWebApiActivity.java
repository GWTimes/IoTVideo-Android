package com.gwell.iotvideodemo.test;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.accountmgr.HttpService;
import com.gwell.iotvideo.http.annotation.HttpApi;
import com.gwell.iotvideo.utils.JSONUtils;
import com.gwell.iotvideo.utils.rxjava.SubscriberListener;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.accountmgr.AccountSPUtils;
import com.gwell.iotvideodemo.base.BaseActivity;
import com.gwell.iotvideodemo.utils.Utils;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TestWebApiActivity extends BaseActivity {
    private static final String TAG = "TestWebApiActivity";

    private Spinner mWebApiSpinner;
    private TextView mHttpStateTextView;
    private TextView mParamTipTextView;
    private RecyclerView mInputList;
    private Button mBtnConfirm;
    private ArrayAdapter<String> mSpinnerAdapter;
    private String mCurrentHttpWebApi;
    private WebInfo mWebInfo;
    private List<InputInfo> mInputInfoList;
    private RecyclerView.Adapter<InputItemHolder> mAdapter;
    private Map<String, String> mHttpFunctionMap;
    private List<String> mHttpFunctionList;
    private HttpService mHttpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_web_api);
        mHttpService = AccountMgr.getHttpService();
        initView();
        initData();
    }

    private void initView() {
        mWebApiSpinner = findViewById(R.id.spinner_api_list);
        mHttpStateTextView = findViewById(R.id.http_state);
        mParamTipTextView = findViewById(R.id.tv_param_tip);
        mInputList = findViewById(R.id.input_info_list);
        mBtnConfirm = findViewById(R.id.confirm);
        mWebApiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCurrentHttpWebApi = mHttpFunctionMap.get(mHttpFunctionList.get(i));
                LogUtils.i(TAG, "mCurrentHttpWebApi = " + mCurrentHttpWebApi);
                webApiTemplate(mCurrentHttpWebApi);
                mHttpStateTextView.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mInputList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAdapter = new RecyclerView.Adapter<InputItemHolder>() {
            @NonNull
            @Override
            public InputItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(TestWebApiActivity.this).inflate(R.layout.item_input_info, parent, false);
                InputItemHolder holder = new InputItemHolder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull InputItemHolder holder, int position) {
                final InputInfo inputInfo = mInputInfoList.get(position);
                holder.tvDisplayName.setText(inputInfo.displayName);
                holder.tvInputType.setText(inputInfo.infoType.getSimpleName());
                holder.etValue.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        inputInfo.value = editable.toString();
                    }
                });
                holder.etValue.setText(inputInfo.value);
                if (inputInfo.infoType == Integer.class || inputInfo.infoType == long.class) {
                    holder.etValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    holder.etValue.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                holder.etValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            mParamTipTextView.setText(mWebInfo.getParamDepict(inputInfo.displayName));
                        } else {
                            mParamTipTextView.setText(null);
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mInputInfoList.size();
            }
        };
        mInputList.setAdapter(mAdapter);
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConnect();
            }
        });
    }

    private void initData() {
        mHttpFunctionMap = new TreeMap<>();
        getAllHttpFunction(mHttpFunctionMap);
        mHttpFunctionList = new ArrayList<>();
        mHttpFunctionList.addAll(mHttpFunctionMap.keySet());
        mSpinnerAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, mHttpFunctionList);
        mWebApiSpinner.setAdapter(mSpinnerAdapter);
        mWebInfo = new WebInfo();
        mWebInfo.initDefaultValue();
        mInputInfoList = new ArrayList<>();
    }

    /**
     * 获取所有的Http接口
     */
    private void getAllHttpFunction(Map<String, String> functions) {
        if (functions == null || functions.size() > 0) {
            return;
        }
        Method[] methods = mHttpService.getClass().getMethods();
        for (Method method : methods) {
            Annotation[] methodAnnotations = method.getAnnotations();
            for (Annotation annotation : methodAnnotations) {
                if (annotation instanceof HttpApi) {
                    functions.put(((HttpApi) annotation).value(), method.getName());
                    break;
                }
            }
        }
    }

    /**
     * 根据方法名获取方法
     *
     * @return 方法
     */
    public Method getHttpMethod(String methodName) {
        Method[] methods = mHttpService.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    /**
     * 获取相应方法的参数名称列表
     *
     * @param methodName 方法名
     */
    public void getHttpMethodParams(String methodName, Map<String, Class> params) {
        LogUtils.i(TAG, "getHttpMethodParams methodName = " + methodName);
        if (params == null || params.size() > 0) {
            return;
        }
        Method[] methods = mHttpService.getClass().getMethods();
        Method targetMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                targetMethod = method;
                break;
            }
        }
        if (targetMethod != null) {
            Annotation[][] annotationsArray = targetMethod.getParameterAnnotations();
            for (Annotation[] annotations : annotationsArray) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof com.gwell.iotvideo.http.annotation.Field) {
                        params.put(((com.gwell.iotvideo.http.annotation.Field) annotation).value(), ((com.gwell.iotvideo.http.annotation.Field) annotation).type());
                    }
                }
            }
        } else {
            LogUtils.i(TAG, "can not find target method");
        }
    }

    private SubscriberListener mSubscriberListener = new SubscriberListener() {
        @Override
        public void onStart() {
            String logText = "正在 " + mCurrentHttpWebApi + " ...";
            mHttpStateTextView.setText(logText);
            LogUtils.i(TAG, "onStart " + logText);
        }

        @Override
        public void onSuccess(@NonNull JsonObject jsonObject) {
            if (jsonObject != null) {
                String logText = "请求成功：\n" + jsonObject.toString();
                mHttpStateTextView.setText(logText);
                LogUtils.i(TAG, "onSuccess " + logText);
                JsonElement dataElement = jsonObject.get("data");
                if (dataElement.isJsonObject()) {
                    JsonObject dataJson = dataElement.getAsJsonObject();
                    WebInfo webInfo = JSONUtils.JsonToEntity(dataJson.toString(), WebInfo.class);
                    if (webInfo != null) {
                        mWebInfo.copyFrom(webInfo);
                    }
                }
            }
        }

        @Override
        public void onFail(@NonNull Throwable e) {
            String logText = "请求失败：\n" + e.getMessage();
            mHttpStateTextView.setText(logText);
            LogUtils.i(TAG, "onFail = " + logText);
        }
    };

    private void webApiTemplate(String methodName) {
        Map<String, Class> params = new LinkedHashMap<>();
        getHttpMethodParams(methodName, params);
        LogUtils.i(TAG, "webApiTemplate = " + params);
        mInputInfoList.clear();
        for (String key : params.keySet()) {
            mInputInfoList.add(new InputInfo(key, params.get(key)));
        }
        inputParams();
    }

    private void inputParams() {
        mAdapter.notifyDataSetChanged();
    }

    private void startConnect() {
        LogUtils.i(TAG, "startConnect params = " + mInputInfoList);
        Method method = getHttpMethod(mCurrentHttpWebApi);
        if (method == null) {
            LogUtils.e(TAG, "not has such method");
            return;
        }
        try {
            int paramSize = mInputInfoList.size();
            if (paramSize == 0) {
                method.invoke(mHttpService, mSubscriberListener);
            } else if (paramSize == 1) {
                method.invoke(mHttpService,
                        mInputInfoList.get(0).getValue(),
                        mSubscriberListener);
            } else if (paramSize == 2) {
                method.invoke(mHttpService,
                        mInputInfoList.get(0).getValue(),
                        mInputInfoList.get(1).getValue(),
                        mSubscriberListener);
            } else if (paramSize == 3) {
                method.invoke(mHttpService,
                        mInputInfoList.get(0).getValue(),
                        mInputInfoList.get(1).getValue(),
                        mInputInfoList.get(2).getValue(),
                        mSubscriberListener);
            } else if (paramSize == 4) {
                method.invoke(mHttpService,
                        mInputInfoList.get(0).getValue(),
                        mInputInfoList.get(1).getValue(),
                        mInputInfoList.get(2).getValue(),
                        mInputInfoList.get(3).getValue(),
                        mSubscriberListener);
            } else if (paramSize == 5) {
                method.invoke(mHttpService,
                        mInputInfoList.get(0).getValue(),
                        mInputInfoList.get(1).getValue(),
                        mInputInfoList.get(2).getValue(),
                        mInputInfoList.get(3).getValue(),
                        mInputInfoList.get(4).getValue(),
                        mSubscriberListener);
            } else if (paramSize == 6) {
                method.invoke(mHttpService,
                        mInputInfoList.get(0).getValue(),
                        mInputInfoList.get(1).getValue(),
                        mInputInfoList.get(2).getValue(),
                        mInputInfoList.get(3).getValue(),
                        mInputInfoList.get(4).getValue(),
                        mInputInfoList.get(5).getValue(),
                        mSubscriberListener);
            } else if (paramSize == 7) {
                method.invoke(mHttpService,
                        mInputInfoList.get(0).getValue(),
                        mInputInfoList.get(1).getValue(),
                        mInputInfoList.get(2).getValue(),
                        mInputInfoList.get(3).getValue(),
                        mInputInfoList.get(4).getValue(),
                        mInputInfoList.get(5).getValue(),
                        mInputInfoList.get(6).getValue(),
                        mSubscriberListener);
            } else if (paramSize == 8) {
                method.invoke(mHttpService,
                        mInputInfoList.get(0).getValue(),
                        mInputInfoList.get(1).getValue(),
                        mInputInfoList.get(2).getValue(),
                        mInputInfoList.get(3).getValue(),
                        mInputInfoList.get(4).getValue(),
                        mInputInfoList.get(5).getValue(),
                        mInputInfoList.get(6).getValue(),
                        mInputInfoList.get(7).getValue(),
                        mSubscriberListener);
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        for (InputInfo inputInfo : mInputInfoList) {
            mWebInfo.updateValue(inputInfo.displayName, inputInfo.getValue());
        }
    }

    private JsonObject combineJson(JsonObject srcObj, JsonObject addObj) {
        Set<String> addKeySet = addObj.keySet();
        for (String key : addKeySet) {
            JsonElement jsonElement = addObj.get(key);
            srcObj.add(key, jsonElement);
        }
        return srcObj;
    }

    private class WebInfo {
        Integer timezone;
        Integer flag;
        Integer thirdType;
        Integer authType;
        Integer type;
        Integer accept;
        String mobileArea;
        String countryCode;
        String mobile;
        String email;
        String account;
        String pwd;
        String oldPwd;
        String vcode;
        String uniqueId;
        String accessToken;
        String feedbackId;
        String noticeId;
        String did;
        String ownerId;
        String deviceName;
        String orderId;
        String payType;
        String serviceType;
        String xingeToken;
        String voucherCode;
        String qrcodeToken;
        String sharedId;
        String positionId;
        String targetId;
        String promotionId;
        String infoMap;

        void initDefaultValue() {
            timezone = 28800;
            flag = 1;
            thirdType = 1;
            authType = 1;
            type = 1;
            accept = 1;
            mobileArea = "86";
            countryCode = "CN";
            mobile = "10086";
            email = "test@dophigo.com";
            account = TextUtils.isEmpty(mobile) ? email : mobile;
            pwd = "qwe123";
            oldPwd = "qwe123";
            vcode = "123456";
            uniqueId = Utils.getPhoneUuid(TestWebApiActivity.this);
            accessToken = AccountSPUtils.getInstance().getString(TestWebApiActivity.this, AccountSPUtils.TOKEN, "");
            feedbackId = "feedbackId";
            noticeId = "noticeId";
            did = "did";
            ownerId = "ownerId";
            deviceName = "deviceName";
            orderId = "orderId";
            payType = "payType";
            serviceType = "serviceType";
            xingeToken = "xingeToken";
            voucherCode = "voucherCode";
            qrcodeToken = "qrcodeToken";
            sharedId = "sharedId";
            positionId = "positionId";
            targetId = "targetId";
            promotionId = "promotionId";
            infoMap = "nick";
        }

        void copyFrom(WebInfo webInfo) {
            Field[] fields = getClass().getDeclaredFields();
            Object newValue;
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    newValue = field.get(webInfo);
                    if (newValue != null) {
                        LogUtils.i(TAG, "update " + field.getName() + " to " + newValue);
                        field.set(this, newValue);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
//            LogUtils.i(TAG, "copyFrom " + toString());
        }

        void updateValue(String fieldName, Object value) {
            try {
                Field field = getClass().getDeclaredField(fieldName);
                if (field.getType() == value.getClass()) {
                    field.set(this, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
//            LogUtils.i(TAG, "updateValue " + toString());
        }

        String getParamDepict(String fieldName) {
            switch (fieldName) {
                case "timezone":
                    return "时区，单位秒，如东八区28800";
                case "flag":
                    return "1：表示用户注册，2：表示找回密码，3：表示只是发送一条短信";
                case "thirdType":
                    return "第三方登录类型，1：微信，2：Facebook，3：QQ，4：微博等";
                case "authType":
                    return "账号认证类型，1：注册账号；2：微信；3：QQ等";
                case "type":
                    return "问题分类，：1：连接问题 ；2：录像问题 ；3：离线问题 ；4：配网问题 ；5：云服务问题 ；9：其它问题";
                case "accept":
                    return "是否接受，1：接受；2：拒绝";
                case "mobileArea":
                    return "国家码，例如中国是86";
                case "countryCode":
                    return "国家二字码,如中国：CN";
                case "mobile":
                    return "电话号码";
                case "account":
                    return "帐号名称";
                case "email":
                    return "邮箱地址";
                case "pwd":
                    return "密码";
                case "oldPwd":
                    return "原密码";
                case "vcode":
                    return "验证码";
                case "uniqueId":
                    return "终端设备的唯一标识(自动填写)";
                case "accessToken":
                    return "终端用户token(自动填写)";
                case "feedbackId":
                    return "反馈id(自动填写)";
                case "noticeId":
                    return "公告id(自动填写)";
                case "did":
                    return "设备id(自动填写)";
                case "ownerId":
                    return "主人的ivUid(自动填写)";
                case "deviceName":
                    return "设备名称(自动填写)";
                case "qrcodeToken":
                    return "生成的二维码token(自动填写)";
                case "orderId":
                    return "订单id(自动填写)";
                case "payType":
                    return "支付类型，微信或支付宝";
                case "serviceType":
                    return "套餐服务类型，vss：全时套餐，evs：事件套餐";
                case "xingeToken":
                    return "获取的注册的token(自动填写)";
                case "voucherCode":
                    return "兑换码(自动填写)";
                case "sharedId":
                    return "被分享者的ivUid(自动填写)";
                case "positionId":
                    return "预置位id";
                case "targetId":
                    return "当分享者取消分享时，为被分享者的ivUid；当被分享者取消分享时，为分享者的为被分享者的ivUid(自动填写)";
                case "promotionId":
                    return "促销id(自动填写)";
                case "infoMap":
                    return "用户昵称";
            }
            return null;
        }

        @Override
        public String toString() {
            return "WebInfo{" +
                    "mobileArea='" + mobileArea +
                    ", countryCode='" + countryCode + '\'' +
                    ", mobile='" + mobile + '\'' +
                    ", account='" + account + '\'' +
                    ", email='" + email + '\'' +
                    ", pwd='" + pwd + '\'' +
                    ", oldPwd='" + oldPwd + '\'' +
                    ", vcode='" + vcode + '\'' +
                    ", uniqueId='" + uniqueId + '\'' +
                    ", accessToken='" + accessToken + '\'' +
                    ", feedbackId='" + feedbackId + '\'' +
                    ", noticeId='" + noticeId + '\'' +
                    ", did='" + did + '\'' +
                    ", ownerId='" + ownerId + '\'' +
                    ", deviceName='" + deviceName + '\'' +
                    ", orderId='" + orderId + '\'' +
                    ", payType='" + payType + '\'' +
                    ", serviceType='" + serviceType + '\'' +
                    ", xingeToken='" + xingeToken + '\'' +
                    ", voucherCode='" + voucherCode + '\'' +
                    ", qrcodeToken='" + qrcodeToken + '\'' +
                    ", sharedId='" + sharedId + '\'' +
                    ", positionId='" + positionId + '\'' +
                    ", targetId='" + targetId + '\'' +
                    ", promotionId='" + promotionId + '\'' +
                    '}';
        }
    }

    class InputInfo {
        String displayName;
        Class infoType;
        String value;

        InputInfo(String displayName, Class infoType, String value) {
            this.displayName = displayName;
            this.infoType = infoType;
            this.value = value;
            if (this.value == null) {
                try {
                    Field field = mWebInfo.getClass().getDeclaredField(this.displayName);
                    field.setAccessible(true);
                    Object fieldValue = field.get(mWebInfo);
                    this.value = String.valueOf(fieldValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (this.value == null) {
                if (this.infoType == Integer.class || this.infoType == long.class) {
                    this.value = "1";
                }
            }
        }

        InputInfo(String displayName, Class infoType) {
            this(displayName, infoType, null);
        }

        Object getValue() {
            if (infoType == Integer.class) {
                return Integer.valueOf(value);
            } else if (infoType == long.class) {
                return Long.valueOf(value);
            } else if (infoType == Map.class) {
                Map<String, String> map = new HashMap<>();
                map.put("nick", value);
                map.put("headUrl", "headUrl");
                return map;
            } else if (infoType == List.class) {
                List<String> list = new ArrayList<>();
                list.add("1");
                list.add("2");
                return list;
            } else if (infoType == File.class) {
                return null;
            }
            return value;
        }

        @Override
        public String toString() {
            return displayName + "=" + value;
        }
    }

    class InputItemHolder extends RecyclerView.ViewHolder {
        TextView tvDisplayName;
        TextView tvInputType;
        EditText etValue;

        InputItemHolder(View view) {
            super(view);
            tvDisplayName = view.findViewById(R.id.display_name);
            tvInputType = view.findViewById(R.id.input_type);
            etValue = view.findViewById(R.id.input_edit);
        }
    }
}
