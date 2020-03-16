package com.tencentcs.iotvideo.http;

import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;

public interface TencentcsHttpService {

    /**
     * 终端用户注册
     *
     * @param CunionId           标识用户的唯一id，防止同一个用户多次注册
     * @param subscriberListener Http回调结果的观察者
     */
    void CreateAppUsr(String CunionId,
                      SubscriberListener subscriberListener);

    /**
     * 终端用户接入授权
     *
     * @param AccessId           终端用户在IotVideo上的唯一标识id
     * @param UniqueId           终端唯一id,用于区分同一个用户的多个终端
     * @param TtlMinutes         Token的TTL(time to alive)分钟数
     * @param subscriberListener Http回调结果的观察者
     */
    void CreateUsrToken(String AccessId,
                        String UniqueId,
                        Integer TtlMinutes,
                        SubscriberListener subscriberListener);

    /**
     * 终端用户绑定设备
     *
     * @param AccessId           终端用户在IotVideo上的唯一标识id
     * @param Tid                设备TID
     * @param Role               用户角色，owner：主人，guest：访客
     * @param ForceBind          是否踢掉之前的主人，true：踢掉；false：不踢掉。当role为owner时，可以不填
     * @param subscriberListener Http回调结果的观察者
     */
    void CreateBinding(String AccessId,
                       String Tid,
                       String Role,
                       Boolean ForceBind,
                       SubscriberListener subscriberListener);

    /**
     * 终端用户解绑设备
     *
     * @param AccessId           终端用户在IotVideo上的唯一标识id
     * @param Tid                设备TID
     * @param Role               用户角色，owner：主人，guest：访客
     * @param subscriberListener Http回调结果的观察者
     */
    void DeleteBinding(String AccessId,
                       String Tid,
                       String Role,
                       SubscriberListener subscriberListener);

    /**
     * 查询终端用户绑定的设备列表
     *
     * @param AccessId           终端用户在IotVideo上的唯一标识id
     * @param subscriberListener Http回调结果的观察者
     */
    void DescribeBindDev(String AccessId,
                         SubscriberListener subscriberListener);

    /**
     * 查询设备绑定的终端用户列表
     *
     * @param AccessId           终端用户在IotVideo上的唯一标识id
     * @param Tid                设备TID
     * @param subscriberListener Http回调结果的观察者
     */
    void DescribeBindUsr(String AccessId,
                         String Tid,
                         SubscriberListener subscriberListener);

    /**
     * 终端用户临时访问设备授权
     *
     * @param AccessId           终端用户在IotVideo上的唯一标识id
     * @param Tids               设备TID列表,0<元素数量<=100
     * @param TtlMinutes         Token的TTL(time to alive)分钟数
     * @param subscriberListener Http回调结果的观察者
     */
    void CreateDevToken(String AccessId,
                        String[] Tids,
                        Integer TtlMinutes,
                        SubscriberListener subscriberListener);
}