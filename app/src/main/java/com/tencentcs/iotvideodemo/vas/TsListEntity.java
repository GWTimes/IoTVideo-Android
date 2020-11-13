package com.tencentcs.iotvideodemo.vas;

import java.util.List;

public class TsListEntity {

    /**
     * msg : Success
     * code : 0
     * data : [{"start":1603742420,"end":1603742428},{"start":1603742528,"end":1603742536},{"start":1603742608,"end":1603742616},{"start":1603742668,"end":1603742676},{"start":1603742868,"end":1603742876},{"start":1603743068,"end":1603743076},{"start":1603743384,"end":1603743388},{"start":1603743404,"end":1603743412},{"start":1603743424,"end":1603743432},{"start":1603743700,"end":1603743708},{"start":1603743740,"end":1603743748},{"start":1603743964,"end":1603743972}]
     * requestId : null
     */
    private String msg;
    private int code;
    private List<DataEntity> data;
    private String requestId;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(List<DataEntity> data) {
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

    public List<DataEntity> getData() {
        return data;
    }

    public String getRequestId() {
        return requestId;
    }

    public class DataEntity {
        /**
         * start : 1603742420
         * end : 1603742428
         */
        private int start;
        private int end;

        public void setStart(int start) {
            this.start = start;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
}
