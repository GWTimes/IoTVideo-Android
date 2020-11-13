package com.tencentcs.iotvideodemo.vas;

public class EventInfo {


    /**
     * msg : Success
     * code : 0
     * data : {"imgUrl":"xxxxx","thumbUrlSuffix":"xxxxxx","alarmType":3,"alarmId":"xxx","startTime":123455,"endTime":123566,"firstAlarmType":1,"deviceId":123}
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
         * imgUrl : xxxxx
         * thumbUrlSuffix : xxxxxx
         * alarmType : 3
         * alarmId : xxx
         * startTime : 123455
         * endTime : 123566
         * firstAlarmType : 1
         * deviceId : 123
         */
        private String imgUrl;
        private String thumbUrlSuffix;
        private long alarmType;
        private String alarmId;
        private int startTime;
        private int endTime;
        private int firstAlarmType;
        private String deviceId;

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public void setThumbUrlSuffix(String thumbUrlSuffix) {
            this.thumbUrlSuffix = thumbUrlSuffix;
        }

        public void setAlarmType(long alarmType) {
            this.alarmType = alarmType;
        }

        public void setAlarmId(String alarmId) {
            this.alarmId = alarmId;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public void setFirstAlarmType(int firstAlarmType) {
            this.firstAlarmType = firstAlarmType;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public String getThumbUrlSuffix() {
            return thumbUrlSuffix;
        }

        public long getAlarmType() {
            return alarmType;
        }

        public String getAlarmId() {
            return alarmId;
        }

        public int getStartTime() {
            return startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public int getFirstAlarmType() {
            return firstAlarmType;
        }

        public String getDeviceId() {
            return deviceId;
        }

        @Override
        public String toString() {
            return "DataEntity{" +
                    "imgUrl='" + imgUrl + '\'' +
                    ", thumbUrlSuffix='" + thumbUrlSuffix + '\'' +
                    ", alarmType=" + alarmType +
                    ", alarmId='" + alarmId + '\'' +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", firstAlarmType=" + firstAlarmType +
                    ", deviceId=" + deviceId +
                    '}';
        }
    }
}
