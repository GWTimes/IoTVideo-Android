package com.gwell.iotvideodemo.accountmgr.deviceshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.gwell.http.utils.HttpUtils;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.base.BaseFragment;
import com.gwell.iotvideodemo.base.HttpRequestState;
import com.gwell.iotvideodemo.widget.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AccountShareFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "AccountShareFragment";

    private EditText mInputAccount;
    private RecyclerView mRVUserList;
    private List<UserList.User> mUserList;
    private RecyclerView.Adapter<ItemHolder> mAdapter;
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
        mAdapter = new RecyclerView.Adapter<ItemHolder>() {

            @NonNull
            @Override
            public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_user, parent, false);
                ItemHolder holder = new ItemHolder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull ItemHolder holder, final int position) {
                holder.userName.setText(mUserList.get(position).getNick());
                holder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDeviceShareViewModel.shareDevice(mUserList.get(position).getIvUid());
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mUserList.size();
            }
        };
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
                        UserList userList = HttpUtils.JsonToEntity(httpRequestState.getJsonObject().toString(), UserList.class);
                        if (userList.getData() != null) {
                            mUserList.add(userList.getData());
                            mAdapter.notifyDataSetChanged();
                            mDeviceShareViewModel.shareDevice(userList.getData().getIvUid());
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
            mDeviceShareViewModel.findUser(mInputAccount.getText().toString());
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        LinearLayout rootView;
        TextView userName;

        ItemHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.root_view);
            userName = itemView.findViewById(R.id.user_item_name);
        }
    }
}
