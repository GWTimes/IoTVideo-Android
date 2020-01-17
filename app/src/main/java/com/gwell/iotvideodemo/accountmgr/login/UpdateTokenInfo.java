package com.gwell.iotvideodemo.accountmgr.login;

public class UpdateTokenInfo {

    /**
     * code : 0
     * data : {"accessId":"-9223371598768111611","expireTime":1579778159,"ivToken":"010155E2C1BB49578E55121E66000000862F05A15AD90D389D33D8775EC2D8319C391F07A2B32DA070A6133696977AD5978D8DE2C3DCCAC2C8667D1A804B6601"}
     * msg : Success
     * requestId : f6cd062d-64ab-4f0a-afe3-ddaf923343fe
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
         * accessId : -9223371598768111611
         * expireTime : 1579778159
         * ivToken : 010155E2C1BB49578E55121E66000000862F05A15AD90D389D33D8775EC2D8319C391F07A2B32DA070A6133696977AD5978D8DE2C3DCCAC2C8667D1A804B6601
         */

        private String accessId;
        private int expireTime;
        private String ivToken;

        public String getAccessId() {
            return accessId;
        }

        public void setAccessId(String accessId) {
            this.accessId = accessId;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(int expireTime) {
            this.expireTime = expireTime;
        }

        public String getIvToken() {
            return ivToken;
        }

        public void setIvToken(String ivToken) {
            this.ivToken = ivToken;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "accessId='" + accessId + '\'' +
                    ", expireTime=" + expireTime +
                    ", ivToken='" + ivToken + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UpdateTokenInfo{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
