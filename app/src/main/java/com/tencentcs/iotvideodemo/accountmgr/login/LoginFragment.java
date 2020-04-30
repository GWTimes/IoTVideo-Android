package com.tencentcs.iotvideodemo.accountmgr.login;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.UrlHelper;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.utils.AppSPUtils;
import com.tencentcs.iotvideodemo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.lifecycle.ViewModelProviders;

public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "LoginFragment";

    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private LoginViewModel mLoginViewModel;
    private OEMDialog mOEMDialog;
    private Button mLoginBtn;

    private boolean mIsNeedRestart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mUserNameView = view.findViewById(R.id.tv_user_name);
        mPasswordView = view.findViewById(R.id.tv_password);
        mLoginBtn = view.findViewById(R.id.btn_login);
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
    public void onDestroy() {
        super.onDestroy();
        if (mOEMDialog != null) {
            if (mOEMDialog.isShowing()) {
                mOEMDialog.dismiss();
            }
            mOEMDialog = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (getActivity() instanceof LoginActivity) {
                    ((LoginActivity) getActivity()).hideSoftKeyboard();
                }
                if (mIsNeedRestart) {
                    Toast.makeText(getActivity(), R.string.effective_after_restarting, Toast.LENGTH_LONG).show();
                } else {
                    loginClicked();
                }
                break;

            case R.id.btn_forgot_password:
                retrievePasswordClicked();
                break;

            case R.id.btn_forgot_register:
                registerClicked();
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_menu_setting) {
            showOEMDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showOEMDialog() {
        if (mOEMDialog == null) {
            mOEMDialog = new OEMDialog(getActivity());
        }
        mOEMDialog.show(new OnConfirmClickedListener() {
            @Override
            public void onConfirmClicked(String productId, int serviceType) {
                LogUtils.i(TAG, "showOEMDialog productId = " + productId + " serviceType = " + serviceType);
                boolean hasChanged = false;
                if (!AccountMgr.getProductId().equals(productId)) {
                    AppSPUtils.getInstance().putString(getActivity(), AppSPUtils.PRODUCT_ID, productId);
                    hasChanged = true;
                }

                if (serviceType != UrlHelper.getInstance().getServerType()) {
                    AppSPUtils.getInstance().putBoolean(getActivity(), AppSPUtils.NEED_SWITCH_SERVER_TYPE, true);
                    AppSPUtils.getInstance().putInteger(getActivity(), AppSPUtils.SERVER_TYPE, serviceType);
                    hasChanged = true;
                }

                if (hasChanged) {
                    AccountSPUtils.getInstance().putInteger(getActivity(), AccountSPUtils.VALIDITY_TIMESTAMP, 0);
                    mLoginBtn.setText(R.string.effective_after_restarting);
                    mIsNeedRestart = true;
                } else {
                    mLoginBtn.setText(R.string.action_sign_in);
                    mIsNeedRestart = false;
                }
            }
        });
    }

    private class OEMDialog extends AppCompatDialog {
        private Button confirmBtn;
        private Button cancelBtn;
        private EditText etProductId;
        private Spinner spinner;

        private OEMDialog(@NonNull Context context) {
            super(context);
            setContentView(R.layout.dialog_oem_service);
            setTitle("自定义参数");
            confirmBtn = findViewById(R.id.btn_confirm);
            cancelBtn = findViewById(R.id.btn_cancel);
            etProductId = findViewById(R.id.et_product_id);
            spinner = findViewById(R.id.spinner_service_list);
            List<String> serviceList = new ArrayList<>();
            serviceList.add(getString(R.string.official_server));
            serviceList.add(getString(R.string.test_server));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, serviceList);
            spinner.setAdapter(arrayAdapter);
            spinner.setSelection(UrlHelper.getInstance().isRelease() ? 0 : 1);

            String productId = AppSPUtils.getInstance().getString(getActivity(), AppSPUtils.PRODUCT_ID, AccountMgr.getProductId());
            etProductId.setText(productId);
        }

        private void show(OnConfirmClickedListener listener) {
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productId = etProductId.getText().toString();
                    int serviceType = spinner.getSelectedItemPosition() == 0 ? UrlHelper.SERVER_RELEASE : UrlHelper.SERVER_DEV;
                    if (listener != null) {
                        listener.onConfirmClicked(productId, serviceType);
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
        void onConfirmClicked(String productId, int serviceType);
    }
}
