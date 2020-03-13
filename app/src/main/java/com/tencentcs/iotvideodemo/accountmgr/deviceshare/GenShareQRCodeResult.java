package com.tencentcs.iotvideodemo.accountmgr.deviceshare;

public class GenShareQRCodeResult {

    /**
     * code : 0
     * msg : Success
     * data : {"qrcodeToken":185418383573929984,"expireTime":1577261787}
     */

    private int code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * qrcodeToken : 185418383573929984
         * expireTime : 1577261787
         */

        private long qrcodeToken;
        private int expireTime;

        public long getQrcodeToken() {
            return qrcodeToken;
        }

        public void setQrcodeToken(long qrcodeToken) {
            this.qrcodeToken = qrcodeToken;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(int expireTime) {
            this.expireTime = expireTime;
        }
    }
}
