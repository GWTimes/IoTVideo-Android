package com.tencentcs.iotvideo.data;

public class CreateShareTokenResult extends BaseResult {
    private int code;
    private DataBean data;

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

    public static class DataBean {
        private long qrcodeToken;

        public long getQrcodeToken() {
            return qrcodeToken;
        }

        public void setQrcodeToken(long qrcodeToken) {
            this.qrcodeToken = qrcodeToken;
        }
    }
}
