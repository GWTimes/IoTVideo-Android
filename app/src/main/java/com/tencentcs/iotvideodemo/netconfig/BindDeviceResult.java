package com.tencentcs.iotvideodemo.netconfig;

public class BindDeviceResult {

    /**
     * code : 0
     * data : {"devToken":"018BD4040D0100000643D3708A43765429B1D7D51C229E3D49A6C0ECF8CE9D69A1F0C80B2A1B9D677C187343714EEC0DD47868145F69F44C67753D6C33C3E0DE"}
     * msg : Success
     */

    private int code;
    private DataBean data;
    private String msg;

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

    public static class DataBean {
        /**
         * devToken : 018BD4040D0100000643D3708A43765429B1D7D51C229E3D49A6C0ECF8CE9D69A1F0C80B2A1B9D677C187343714EEC0DD47868145F69F44C67753D6C33C3E0DE
         */

        private String devToken;

        public String getDevToken() {
            return devToken;
        }

        public void setDevToken(String devToken) {
            this.devToken = devToken;
        }
    }
}
