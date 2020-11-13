package com.tencentcs.iotvideodemo.vas;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CloudStorageInfo {

    private int serviceType;//0:无服务; 1:全时; 2:事件

    private long utcExpire;//服务过去时间

    private int pause;//0:服务正常，打开推流; 1:服务暂停，暂停推流; 2:服务停止

    private String serviceParm;//其他参数

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public long getUtcExpire() {
        return utcExpire;
    }

    public void setUtcExpire(long utcExpire) {
        this.utcExpire = utcExpire;
    }

    public int getPause() {
        return pause;
    }

    public void setPause(int pause) {
        this.pause = pause;
    }

    public String getServiceParm() {
        return serviceParm;
    }

    public void setServiceParm(String serviceParm) {
        this.serviceParm = serviceParm;
    }

    @Override
    public String toString() {
        return "CloudStorageInfo{" +
                "serviceType='" + serviceType + '\'' +
                ", utcExpire=" + utcExpire +
                ", pause=" + pause +
                ", serviceParm='" + serviceParm + '\'' +
                '}';
    }

    public String getServiceTypeInfo() {
        switch (serviceType) {
            case 1:
                return "全时";
            case 2:
                return "事件";
            default:
                return "无服务";
        }
    }

    public String getServiceStateInfo() {
        switch (pause) {
            case 0:
                return "服务正常，打开推流";
            case 1:
                return "服务暂停，暂停推流";
            default:
                return "服务停止";
        }
    }

    public String getUtcExpireInfo() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date(utcExpire * 1000L));
    }
}
