package com.tencentcs.iotvideodemo.accountmgr.deviceshare;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.Utils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.widget.RecycleViewDivider;
import com.tencentcs.iotvideodemo.widget.SimpleRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AccountShareFragment extends BaseFragment implements View.OnClickListener, SimpleRecyclerViewAdapter.OnItemClickListener {
    private static final String TAG = "AccountShareFragment";

    private EditText mInputAccount;
    private RecyclerView mRVUserList;
    private List<UserList.User> mUserList;
    private SimpleRecyclerViewAdapter<UserList.User> mAdapter;
    private DeviceShareViewModel mDeviceShareViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInputAccount = view.findViewById(R.id.account_to_share);
        mRVUserList = view.findViewById(R.id.user_list);
        mUserList = new ArrayList<>();
        mAdapter = new SimpleRecyclerViewAdapter<>(getContext(), mUserList);
        mAdapter.setOnItemClickListener(this);
        mRVUserList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRVUserList.addItemDecoration(new RecycleViewDivider(getContext(), RecycleViewDivider.VERTICAL));
        mRVUserList.setAdapter(mAdapter);
        view.findViewById(R.id.confirm_to_share).setOnClickListener(this);
        initViewModel();
    }

    private void initViewModel() {
        mDeviceShareViewModel = ViewModelProviders.of(getActivity()).get(DeviceShareViewModel.class);
        mDeviceShareViewModel.getFindUserData().observe(getActivity(), new Observer<HttpRequestState>() {
            @Override
            public void onChanged(HttpRequestState httpRequestState) {
                switch (httpRequestState.getStatus()) {
                    case START:
                        mUserList.clear();
                        break;
                    case SUCCESS:
                        UserList userList = JSONUtils.JsonToEntity(httpRequestState.getJsonObject().toString(), UserList.class);
                        if (userList.getData() != null) {
                            mUserList.add(userList.getData());
                            mAdapter.notifyDataSetChanged();
                            mDeviceShareViewModel.shareDevice(userList.getData().getAccessId());
                        } else {
                            Snackbar.make(mRVUserList, "no such user", Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case ERROR:
                        Snackbar.make(mRVUserList, httpRequestState.getE().getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });

        mDeviceShareViewModel.getDeviceShareData().observe(getActivity(), new Observer<HttpRequestState>() {
            @Override
            public void onChanged(HttpRequestState httpRequestState) {
                Snackbar.make(mInputAccount, httpRequestState.getStatusTip(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.confirm_to_share) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            String userName = mInputAccount.getText().toString();
            if (isAccessId(userName)) {
                mDeviceShareViewModel.shareDevice(userName);
            } else {
                mDeviceShareViewModel.findUser(userName);
            }
        }
    }

    @Override
    public void onRecyclerViewItemClick(int position) {
        mDeviceShareViewModel.shareDevice(mUserList.get(position).getAccessId());
    }

    private boolean isAccessId(String input) {
        return input != null && input.length() == 20 && input.startsWith("-");
    }
}
