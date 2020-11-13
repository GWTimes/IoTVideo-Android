package com.tencentcs.iotvideo.data;

import java.util.List;

public class DescribeBindDevResult extends BaseResult {
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int count;
        private List<Device> deviceList;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<Device> getDeviceList() {
            return deviceList;
        }

        public void setDeviceList(List<Device> deviceList) {
            this.deviceList = deviceList;
        }
    }

    public static class Device {
        private String devId;
        private String remarkName;
        private int relation;

        public String getDevId() {
            return devId;
        }

        public void setDevId(String devId) {
            this.devId = devId;
        }

        public String getRemarkName() {
            return remarkName;
        }

        public void setRemarkName(String remarkName) {
            this.remarkName = remarkName;
        }

        public int getRelation() {
            return relation;
        }

        public void setRelation(int relation) {
            this.relation = relation;
        }
    }
}
