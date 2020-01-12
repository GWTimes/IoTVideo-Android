package com.gwell.iotvideodemo.vas;

import java.util.List;

public class PlaybackList {

    /**
     * code : 0
     * msg : Success
     * requestId : bdcb3c8f-3aac-469b-a7ed-09998cf85114
     * data : {"palyList":[{"starttime":1578827659000,"endtime":1578828447000,"m3u8Url":"http://lcb.iotvideo.tencentcs.com/timeshift/live/031400005df99fe03b8d15050eb45385/timeshift.m3u8?starttime=20200112191419&endtime=20200112192727"},{"starttime":1578828453000,"endtime":1578829554000,"m3u8Url":"http://lcb.iotvideo.tencentcs.com/timeshift/live/031400005df99fe03b8d15050eb45385/timeshift.m3u8?starttime=20200112192733&endtime=20200112194554"}]}
     */

    private int code;
    private String msg;
    private String requestId;
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<PalyListBean> palyList;

        public List<PalyListBean> getPalyList() {
            return palyList;
        }

        public void setPalyList(List<PalyListBean> palyList) {
            this.palyList = palyList;
        }

        public static class PalyListBean {
            /**
             * starttime : 1578827659000
             * endtime : 1578828447000
             * m3u8Url : http://lcb.iotvideo.tencentcs.com/timeshift/live/031400005df99fe03b8d15050eb45385/timeshift.m3u8?starttime=20200112191419&endtime=20200112192727
             */

            private long starttime;
            private long endtime;
            private String m3u8Url;

            public long getStarttime() {
                return starttime;
            }

            public void setStarttime(long starttime) {
                this.starttime = starttime;
            }

            public long getEndtime() {
                return endtime;
            }

            public void setEndtime(long endtime) {
                this.endtime = endtime;
            }

            public String getM3u8Url() {
                return m3u8Url;
            }

            public void setM3u8Url(String m3u8Url) {
                this.m3u8Url = m3u8Url;
            }
        }
    }
}
