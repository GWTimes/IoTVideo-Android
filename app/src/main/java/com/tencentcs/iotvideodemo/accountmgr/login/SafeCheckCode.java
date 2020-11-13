package com.tencentcs.iotvideodemo.accountmgr.login;

import com.tencentcs.iotvideodemo.BuildConfig;
import com.tencentcs.iotvideodemo.utils.Utils;

class SafeCheckCode {
    String ticket;
    String randstr;
    private boolean isExpired;

    SafeCheckCode(String ticket, String randstr) {
        this.ticket = ticket;
        this.randstr = randstr;
        isExpired = false;
    }

    boolean isExpired() {
        if (!Utils.isOemVersion()) {
            return false;
        }
        return isExpired;
    }

    void setExpired(boolean expired) {
        isExpired = expired;
    }

    @Override
    public String toString() {
        return "SafeCheckCode{" +
                "ticket='" + ticket + '\'' +
                ", randstr='" + randstr + '\'' +
                ", isExpired=" + isExpired +
                '}';
    }
}
