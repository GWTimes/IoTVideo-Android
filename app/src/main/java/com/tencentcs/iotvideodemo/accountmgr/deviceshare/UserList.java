package com.tencentcs.iotvideodemo.accountmgr.deviceshare;

import android.text.TextUtils;

import androidx.annotation.NonNull;

public class UserList {

    /**
     * code : 0
     * msg : Success
     * data : {"accessId":"-9223371598768111601","name":null,"nick":"","mobileArea":null,"mobile":null,"email":null,"headUrl":""}
     */

    private int code;
    private String msg;
    private User data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public static class User {
        /**
         * accessId : -9223371598768111601
         * name : null
         * nick :
         * mobileArea : null
         * mobile : null
         * email : null
         * headUrl :
         */

        private String accessId;
        private String name;
        private String nick;
        private String mobileArea;
        private String mobile;
        private String email;
        private String headUrl;

        public String getAccessId() {
            return accessId;
        }

        public void setAccessId(String accessId) {
            this.accessId = accessId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getMobileArea() {
            return mobileArea;
        }

        public void setMobileArea(String mobileArea) {
            this.mobileArea = mobileArea;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public String getDisplayName() {
            if (!TextUtils.isEmpty(nick)) {
                return nick;
            } else if (!TextUtils.isEmpty(name)) {
                return name;
            } else if (!TextUtils.isEmpty(accessId)) {
                return accessId;
            }

            return "unknown";
        }

        @NonNull
        @Override
        public String toString() {
            return getDisplayName();
        }
    }
}
