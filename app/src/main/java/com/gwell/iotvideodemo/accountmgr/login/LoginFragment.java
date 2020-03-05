package com.gwell.iotvideodemo.accountmgr.login;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideo.utils.UrlHelper;
import com.gwell.iotvideodemo.MyApp;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.accountmgr.AccountSPUtils;
import com.gwell.iotvideodemo.base.BaseFragment;
import com.gwell.iotvideodemo.utils.AppSPUtils;
import com.gwell.iotvideodemo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "LoginFragment";

    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private TextView mTvSwitchServiceTip;
    private LoginViewModel mLoginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserNameView = view.findViewById(R.id.tv_user_name);
        mPasswordView = view.findViewById(R.id.tv_password);
        mTvSwitchServiceTip = view.findViewById(R.id.tv_switch_service_tip);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.btn_login || id == EditorInfo.IME_NULL) {
                    loginClicked();
                    return true;
                }
                return false;
            }
        });

        view.findViewById(R.id.btn_login).setOnClickListener(this);
        view.findViewById(R.id.btn_forgot_password).setOnClickListener(this);
        view.findViewById(R.id.btn_forgot_register).setOnClickListener(this);
        view.findViewById(R.id.tv_more).setOnClickListener(this);
        Spinner spinner = view.findViewById(R.id.spinner_service_list);
        List<String> serviceList = new ArrayList<>();
        serviceList.add(getString(R.string.official_server));
        serviceList.add(getString(R.string.test_server));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, serviceList);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(UrlHelper.getInstance().isRelease() ? 0 : 1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedService = position == 0 ? UrlHelper.SERVER_RELEASE : UrlHelper.SERVER_DEV;
                if (selectedService != UrlHelper.getInstance().getServerType()) {
                    AccountSPUtils.getInstance().putInteger(getActivity(), AccountSPUtils.VALIDITY_TIMESTAMP, 0);
                    AppSPUtils.getInstance().putBoolean(getActivity(), AppSPUtils.NEED_SWITCH_SERVER_TYPE, true);
                    AppSPUtils.getInstance().putInteger(getActivity(), AppSPUtils.SERVER_TYPE, selectedService);
                    Toast.makeText(getActivity(), R.string.effective_after_restarting, Toast.LENGTH_LONG).show();
                    mTvSwitchServiceTip.setVisibility(View.VISIBLE);
                } else {
                    mTvSwitchServiceTip.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginViewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoginViewModel.getOperateData().setValue(LoginViewModel.OperateType.Login);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (getActivity() instanceof LoginActivity) {
                    ((LoginActivity) getActivity()).hideSoftKeyboard();
                }
                loginClicked();
                break;

            case R.id.btn_forgot_password:
                retrievePasswordClicked();
                break;

            case R.id.btn_forgot_register:
                registerClicked();
                break;

            case R.id.tv_more:
                showOEMDialog();
                break;
        }
    }

    private void loginClicked() {
        mLoginViewModel.getOperateData().setValue(LoginViewModel.OperateType.Login);
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        mLoginViewModel.login(userName, password, Utils.getPhoneUuid(getContext()));
    }

    private void registerClicked() {
        mLoginViewModel.getFragmentData().setValue(LoginViewModel.Fragment.InputAccount);
        mLoginViewModel.getOperateData().setValue(LoginViewModel.OperateType.Register);
    }

    private void retrievePasswordClicked() {
        mLoginViewModel.getFragmentData().setValue(LoginViewModel.Fragment.InputAccount);
        mLoginViewModel.getOperateData().setValue(LoginViewModel.OperateType.ResetPwd);
    }

    private void showOEMDialog() {
        new OEMDialog(getActivity()).show(new OnConfirmClickedListener() {
            @Override
            public void onConfirmClicked(String productId) {
                LogUtils.i(TAG, "showOEMDialog productId = " + productId);
                String currentProductId = AppSPUtils.getInstance().getString(getActivity(), AppSPUtils.PRODUCT_ID, MyApp.PRODUCT_ID);
                if (!currentProductId.equals(productId)) {
                    AppSPUtils.getInstance().putString(getActivity(), AppSPUtils.PRODUCT_ID, productId);
                    AppSPUtils.getInstance().putBoolean(getActivity(), AppSPUtils.NEED_SWITCH_SERVER_TYPE, true);
                    Toast.makeText(getActivity(), R.string.effective_after_restarting, Toast.LENGTH_LONG).show();
                    mTvSwitchServiceTip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private class OEMDialog extends Dialog {
        private Button confirmBtn;
        private Button cancelBtn;
        private EditText etProductId;

        private OEMDialog(@NonNull Context context) {
            super(context);
            setContentView(R.layout.dialog_oem_service);
            confirmBtn = findViewById(R.id.btn_confirm);
            cancelBtn = findViewById(R.id.btn_cancel);
            etProductId = findViewById(R.id.et_product_id);

            String productId = AppSPUtils.getInstance().getString(getActivity(), AppSPUtils.PRODUCT_ID, AccountMgr.getProductId());
            etProductId.setText(productId);
        }

        private void show(OnConfirmClickedListener listener) {
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productId = etProductId.getText().toString();
                    if (listener != null) {
                        listener.onConfirmClicked(productId);
                    }
                    dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            show();
        }
    }

    private interface OnConfirmClickedListener {
        void onConfirmClicked(String productId);
    }
}
