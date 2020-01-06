package com.gwell.iotvideodemo.accountmgr.deviceshare;

import android.text.TextUtils;

public class UserList {

    /**
     * code : 0
     * msg : Success
     * data : {"ivUid":"-9223371598768111611","userName":"","nick":"nick","headUrl":"headUrl"}
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
         * ivUid : -9223371598768111611
         * userName :
         * nick : nick
         * headUrl : headUrl
         */

        private String ivUid;
        private String userName;
        private String nick;
        private String headUrl;

        public String getIvUid() {
            return ivUid;
        }

        public void setIvUid(String ivUid) {
            this.ivUid = ivUid;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
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
            } else if (!TextUtils.isEmpty(userName)) {
                return userName;
            } else if (!TextUtils.isEmpty(ivUid)) {
                return ivUid;
            }

            return "unknown";
        }
    }
}
