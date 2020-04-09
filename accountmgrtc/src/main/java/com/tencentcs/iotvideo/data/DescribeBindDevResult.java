package com.tencentcs.iotvideo.data;

import java.util.List;

public class DescribeBindDevResult extends BaseResult {
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String devId;
        private String deviceName;
        private String deviceMode;
        private String shareType;

        public String getDevId() {
            return devId;
        }

        public void setDevId(String devId) {
            this.devId = devId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceMode() {
            return deviceMode;
        }

        public void setDeviceMode(String deviceMode) {
            this.deviceMode = deviceMode;
        }

        public String getShareType() {
            return shareType;
        }

        public void setShareType(String shareType) {
            this.shareType = shareType;
        }
    }
}
