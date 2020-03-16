package com.tencentcs.iotvideo.data;

import java.util.List;

public class DescribeBindDevResponse extends BaseResponse {
    /**
     * Data : [{"Tid":"37303039000000000000000000000000","DeviceName":"37303039000000000000000000000000","DeviceModel":"","Role":"owner"}]
     * RequestId : 76681c34-7fa7-4c5c-af5c-479202ba0202
     */

    private String RequestId;
    private List<Device> Data;

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String RequestId) {
        this.RequestId = RequestId;
    }

    public List<Device> getData() {
        return Data;
    }

    public void setData(List<Device> Data) {
        this.Data = Data;
    }

    public static class Device {
        /**
         * Tid : 37303039000000000000000000000000
         * DeviceName : 37303039000000000000000000000000
         * DeviceModel :
         * Role : owner
         */

        private String Tid;
        private String DeviceName;
        private String DeviceModel;
        private String Role;

        public String getTid() {
            return Tid;
        }

        public void setTid(String Tid) {
            this.Tid = Tid;
        }

        public String getDeviceName() {
            return DeviceName;
        }

        public void setDeviceName(String DeviceName) {
            this.DeviceName = DeviceName;
        }

        public String getDeviceModel() {
            return DeviceModel;
        }

        public void setDeviceModel(String DeviceModel) {
            this.DeviceModel = DeviceModel;
        }

        public String getRole() {
            return Role;
        }

        public void setRole(String Role) {
            this.Role = Role;
        }
    }
}
