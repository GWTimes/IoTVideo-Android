package com.tencentcs.iotvideodemo.vas;

public class PlaybackList {

    /**
     * msg : Success
     * code : 0
     * data : {"startTime":1600653494,"endTime":1600653494,"endflag":"true，","url":"xxxx"}
     * requestId : xxxxxx
     */
    private String msg;
    private int code;
    private DataEntity data;
    private String requestId;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public DataEntity getData() {
        return data;
    }

    public String getRequestId() {
        return requestId;
    }

    public class DataEntity {
        /**
         * startTime : 1600653494
         * endTime : 1600653494
         * endflag : true，
         * url : xxxx
         */
        private int startTime;
        private int endTime;
        private boolean endflag;
        private String url;

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public void setEndflag(boolean endflag) {
            this.endflag = endflag;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getStartTime() {
            return startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public boolean getEndflag() {
            return endflag;
        }

        public String getUrl() {
            return url;
        }
    }
}
