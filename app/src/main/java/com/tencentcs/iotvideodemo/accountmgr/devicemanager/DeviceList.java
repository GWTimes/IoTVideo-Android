package com.tencentcs.iotvideodemo.accountmgr.devicemanager;

import java.io.Serializable;
import java.util.List;

public class DeviceList {
    /**
     * code : 0
     * msg : Success
     * requestId : 896e098f-2383-454e-a95f-852ccdd498f8
     * data : {"count":1,"pattern":0,"deviceList":[{"devId":"7226904","remarkName":"卧室","modifyTime":1548679047,"relation":0,"version":0,"groupId":0,"permission":271,"secretKey":"cIaHvtVEkvmZNFW5ZwasQscCe4Mo6o001/KsaYK8cbzFzSpmibUGOAhxAEQG+lOP","noDisturb":null,"deviceCate":1}]}
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
        /**
         * count : 1
         * pattern : 0
         * deviceList : [{"devId":"7226904","remarkName":"卧室","modifyTime":1548679047,"relation":0,"version":0,"groupId":0,"permission":271,"secretKey":"cIaHvtVEkvmZNFW5ZwasQscCe4Mo6o001/KsaYK8cbzFzSpmibUGOAhxAEQG+lOP","noDisturb":null,"deviceCate":1}]
         */

        private int count;
        private int pattern;
        private List<Device> deviceList;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getPattern() {
            return pattern;
        }

        public void setPattern(int pattern) {
            this.pattern = pattern;
        }

        public List<Device> getDeviceList() {
            return deviceList;
        }

        public void setDeviceList(List<Device> deviceList) {
            this.deviceList = deviceList;
        }
    }

    public static class Device implements Serializable {
        /**
         * devId : 7226904
         * remarkName : 卧室
         * modifyTime : 1548679047
         * relation : 0
         * version : 0
         * groupId : 0
         * permission : 271
         * secretKey : cIaHvtVEkvmZNFW5ZwasQscCe4Mo6o001/KsaYK8cbzFzSpmibUGOAhxAEQG+lOP
         * noDisturb : null
         * deviceCate : 1
         */

        private String devId;
        private String remarkName;
        private int modifyTime;
        private int relation;
        private int version;
        private int groupId;
        private int permission;
        private String secretKey;
        private Object noDisturb;
        private int deviceCate;

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

        public int getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(int modifyTime) {
            this.modifyTime = modifyTime;
        }

        public int getRelation() {
            return relation;
        }

        public void setRelation(int relation) {
            this.relation = relation;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public int getPermission() {
            return permission;
        }

        public void setPermission(int permission) {
            this.permission = permission;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public Object getNoDisturb() {
            return noDisturb;
        }

        public void setNoDisturb(Object noDisturb) {
            this.noDisturb = noDisturb;
        }

        public int getDeviceCate() {
            return deviceCate;
        }

        public void setDeviceCate(int deviceCate) {
            this.deviceCate = deviceCate;
        }
    }
}
