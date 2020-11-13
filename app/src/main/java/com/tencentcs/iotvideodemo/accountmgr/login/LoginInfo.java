package com.tencentcs.iotvideodemo.accountmgr.login;

import android.text.TextUtils;

public class LoginInfo {

    /**
     * code : 0
     * data : {"accessId":"-9223371598768111606","headUrl":"","accessToken":"01019EA75798737E07BE991E660000004B6409F68B76C13255ED879387FA2561958530D7FAB8652642D087D2AE87E3F2FB2E8216E9561E91CC1262437F87BEBF","nick":""}
     * msg : Success
     * requestId : eeca8585-1d4d-4607-a901-609c662b9c73
     */

    private int code;
    private DataBean data;
    private String msg;
    private String requestId;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public static class DataBean {
        /**
         * accessId : -9223371598768111606
         * headUrl :
         * accessToken : 01019EA75798737E07BE991E660000004B6409F68B76C13255ED879387FA2561958530D7FAB8652642D087D2AE87E3F2FB2E8216E9561E91CC1262437F87BEBF
         * nick :
         */

        private String accessId;
        private String headUrl;
        private String accessToken;
        private String nick;
        private int expireTime;

        public String getAccessId() {
            return accessId;
        }

        public void setAccessId(String accessId) {
            this.accessId = accessId;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(int expireTime) {
            this.expireTime = expireTime;
        }

        public boolean isLoginDataValid() {
            if (TextUtils.isEmpty(accessToken)) {
                return false;
            } else if (System.currentTimeMillis() >= expireTime * 1000L) {
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            return "LoginData{" +
                    "accessId='" + accessId + '\'' +
                    ", accessToken='" + accessToken + '\'' +
                    ", expireTime=" + expireTime +
                    ", headUrl='" + headUrl + '\'' +
                    ", nick='" + nick + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
