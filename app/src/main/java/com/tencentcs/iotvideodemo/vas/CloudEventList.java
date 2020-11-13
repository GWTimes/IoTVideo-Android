package com.tencentcs.iotvideodemo.vas;

import java.util.List;

public class CloudEventList {

    /**
     * msg : Success
     * code : 0
     * data : {"thumbUrlSuffix":"","pageEnd":true,"imgUrlPrefix":"","list":[{"imgUrlSuffix":"","alarmType":9728,"alarmId":"4295267943713901603743955","startTime":1603743955,"endTime":1603743965,"firstAlarmType":1024},{"imgUrlSuffix":"","alarmType":2202231976,"alarmId":"4295267943713901603743646","startTime":1603743646,"endTime":1603743763,"firstAlarmType":16777216},{"imgUrlSuffix":"","alarmType":16384,"alarmId":"4295267943713901603743445","startTime":1603743445,"endTime":1603743453,"firstAlarmType":16384},{"imgUrlSuffix":"","alarmType":1547968515,"alarmId":"4295267943713901603743262","startTime":1603743262,"endTime":1603743428,"firstAlarmType":4194304},{"imgUrlSuffix":"","alarmType":2097152,"alarmId":"4295267943713901603743061","startTime":1603743061,"endTime":1603743069,"firstAlarmType":2097152},{"imgUrlSuffix":"","alarmType":1024,"alarmId":"4295267943713901603742860","startTime":1603742860,"endTime":1603742868,"firstAlarmType":1024},{"imgUrlSuffix":"","alarmType":301993988,"alarmId":"4295267943713901603742518","startTime":1603742518,"endTime":1603742667,"firstAlarmType":33554432}]}
     */
    private String msg;
    private int code;
    private DataEntity data;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(DataEntity data) {
        this.data = data;
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

    public class DataEntity {
        /**
         * thumbUrlSuffix :
         * pageEnd : true
         * imgUrlPrefix :
         * list : [{"imgUrlSuffix":"","alarmType":9728,"alarmId":"4295267943713901603743955","startTime":1603743955,"endTime":1603743965,"firstAlarmType":1024},{"imgUrlSuffix":"","alarmType":2202231976,"alarmId":"4295267943713901603743646","startTime":1603743646,"endTime":1603743763,"firstAlarmType":16777216},{"imgUrlSuffix":"","alarmType":16384,"alarmId":"4295267943713901603743445","startTime":1603743445,"endTime":1603743453,"firstAlarmType":16384},{"imgUrlSuffix":"","alarmType":1547968515,"alarmId":"4295267943713901603743262","startTime":1603743262,"endTime":1603743428,"firstAlarmType":4194304},{"imgUrlSuffix":"","alarmType":2097152,"alarmId":"4295267943713901603743061","startTime":1603743061,"endTime":1603743069,"firstAlarmType":2097152},{"imgUrlSuffix":"","alarmType":1024,"alarmId":"4295267943713901603742860","startTime":1603742860,"endTime":1603742868,"firstAlarmType":1024},{"imgUrlSuffix":"","alarmType":301993988,"alarmId":"4295267943713901603742518","startTime":1603742518,"endTime":1603742667,"firstAlarmType":33554432}]
         */
        private String thumbUrlSuffix;
        private boolean pageEnd;
        private String imgUrlPrefix;
        private List<ListEntity> list;

        public void setThumbUrlSuffix(String thumbUrlSuffix) {
            this.thumbUrlSuffix = thumbUrlSuffix;
        }

        public void setPageEnd(boolean pageEnd) {
            this.pageEnd = pageEnd;
        }

        public void setImgUrlPrefix(String imgUrlPrefix) {
            this.imgUrlPrefix = imgUrlPrefix;
        }

        public void setList(List<ListEntity> list) {
            this.list = list;
        }

        public String getThumbUrlSuffix() {
            return thumbUrlSuffix;
        }

        public boolean isPageEnd() {
            return pageEnd;
        }

        public String getImgUrlPrefix() {
            return imgUrlPrefix;
        }

        public List<ListEntity> getList() {
            return list;
        }

        public class ListEntity {
            /**
             * imgUrlSuffix :
             * alarmType : 9728
             * alarmId : 4295267943713901603743955
             * startTime : 1603743955
             * endTime : 1603743965
             * firstAlarmType : 1024
             */
            private String imgUrlSuffix;
            private long alarmType;
            private String alarmId;
            private long startTime;
            private long endTime;
            private long firstAlarmType;

            public void setImgUrlSuffix(String imgUrlSuffix) {
                this.imgUrlSuffix = imgUrlSuffix;
            }

            public void setAlarmType(long alarmType) {
                this.alarmType = alarmType;
            }

            public void setAlarmId(String alarmId) {
                this.alarmId = alarmId;
            }

            public void setStartTime(long startTime) {
                this.startTime = startTime;
            }

            public void setEndTime(long endTime) {
                this.endTime = endTime;
            }

            public void setFirstAlarmType(int firstAlarmType) {
                this.firstAlarmType = firstAlarmType;
            }

            public String getImgUrlSuffix() {
                return imgUrlSuffix;
            }

            public long getAlarmType() {
                return alarmType;
            }

            public String getAlarmId() {
                return alarmId;
            }

            public long getStartTime() {
                return startTime;
            }

            public long getEndTime() {
                return endTime;
            }

            public long getFirstAlarmType() {
                return firstAlarmType;
            }

            @Override
            public String toString() {
                return "{" +
                        "alarmType=" + alarmType +
                        ", alarmId='" + alarmId + '\'' +
                        ", startTime=" + startTime +
                        ", endTime=" + endTime +
                        '}';
            }
        }
    }
}
