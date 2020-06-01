package com.tencentcs.iotvideodemo.accountmgr.devicemanager;

import java.io.Serializable;
import java.util.List;

public class DeviceList {

    /**
     * code : 0
     * msg : Success
     * data : [{"devId":"55484851000000000000000000000000","did":null,"deviceName":"42949672973","deviceType":null,"deviceMode":"app.pack.test","url":null,"shareType":"owner"}]
     */

    private int code;
    private String msg;
    private List<Device> data;

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

    public List<Device> getData() {
        return data;
    }

    public void setData(List<Device> data) {
        this.data = data;
    }

    public static class Device implements Serializable {
        /**
         * devId : 55484851000000000000000000000000
         * did : null
         * deviceName : 42949672973
         * deviceType : null
         * deviceMode : app.pack.test
         * url : null
         * shareType : owner
         */

        private String devId;
        private String did;
        private String deviceName;
        private String deviceType;
        private String deviceMode;
        private String url;
        private String shareType;
        private String sysCate;
        private boolean isSelected;

        public Device() {}

        public Device(String devId, String deviceName, String shareType, String deviceMode) {
            this.devId = devId;
            this.deviceName = deviceName;
            this.shareType = shareType;
            this.deviceMode = deviceMode;
        }

        public String getDevId() {
            return devId;
        }

        public void setDevId(String devId) {
            this.devId = devId;
        }

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceMode() {
            return deviceMode;
        }

        public void setDeviceMode(String deviceMode) {
            this.deviceMode = deviceMode;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getShareType() {
            return shareType;
        }

        public void setShareType(String shareType) {
            this.shareType = shareType;
        }

        public String getSysCate() {
            return sysCate;
        }

        public void setSysCate(String sysCate) {
            this.sysCate = sysCate;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "devId='" + devId + '\'' +
                    ", did='" + did + '\'' +
                    ", deviceName='" + deviceName + '\'' +
                    ", deviceType='" + deviceType + '\'' +
                    ", deviceMode='" + deviceMode + '\'' +
                    ", url='" + url + '\'' +
                    ", shareType='" + shareType + '\'' +
                    '}';
        }
    }
}
