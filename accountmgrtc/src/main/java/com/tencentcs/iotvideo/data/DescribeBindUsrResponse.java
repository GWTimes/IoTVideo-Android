package com.tencentcs.iotvideo.data;

import java.util.List;

public class DescribeBindUsrResponse extends BaseResponse {

    /**
     * Data : [{"AccessId":"-9223371603063077911","Role":"owner"}]
     * RequestId : 23180bf7-2165-4d81-adda-bbeb6eb4414c
     */

    private List<DataBean> Data;

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean {
        /**
         * AccessId : -9223371603063077911
         * Role : owner
         */

        private String AccessId;
        private String Role;

        public String getAccessId() {
            return AccessId;
        }

        public void setAccessId(String AccessId) {
            this.AccessId = AccessId;
        }

        public String getRole() {
            return Role;
        }

        public void setRole(String Role) {
            this.Role = Role;
        }
    }
}
