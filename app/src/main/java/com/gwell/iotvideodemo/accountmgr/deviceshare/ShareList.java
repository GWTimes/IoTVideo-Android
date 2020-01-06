package com.gwell.iotvideodemo.accountmgr.deviceshare;

import java.util.List;

public class ShareList {

    /**
     * code : 0
     * msg : Success
     * data : {"users":[{"ivUid":"-9223371598768111613","userName":"","nick":"IotVideo","headUrl":""}]}
     */

    private int code;
    private String msg;
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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<User> users;

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        public static class User {
            /**
             * ivUid : -9223371598768111613
             * userName :
             * nick : IotVideo
             * headUrl :
             */

            private String ivUid;
            private String userName;
            private String nick;
            private String headUrl;

            public String getIvUid() {
                return ivUid;
            }

            public void setIvUid(String ivUid) {
                this.ivUid = ivUid;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getNick() {
                return nick;
            }

            public void setNick(String nick) {
                this.nick = nick;
            }

            public String getHeadUrl() {
                return headUrl;
            }

            public void setHeadUrl(String headUrl) {
                this.headUrl = headUrl;
            }
        }
    }
}
