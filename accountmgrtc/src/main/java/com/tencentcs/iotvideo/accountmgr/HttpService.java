package com.tencentcs.iotvideo.accountmgr;

import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;

import java.io.File;
import java.util.Map;

public interface HttpService {

    /**
     * 1.1.1手机获取验证码
     *
     * @param mobileArea         国家码，例如中国是86
     * @param mobile             手机号码
     * @param flag               0：注册和绑定验证码，1：修改密码验证码，2：其它
     * @param ticket             app进行图形验证结果的票据
     * @param randstr            app进行图形验证结果的随机字符串
     * @param subscriberListener Http回调结果的观察者
     */
    void mobileCheckCode(String mobileArea,
                         String mobile,
                         Integer flag,
                         String ticket,
                         String randstr,
                         SubscriberListener subscriberListener);

    /**
     * 1.1.2邮箱获取验证码
     *
     * @param email              邮箱
     * @param pwd                密码
     * @param flag               0：注册和绑定，1：找回密码，2：只发送一条短信
     * @param ticket             app进行图形验证结果的票据
     * @param randstr            app进行图形验证结果的随机字符串
     * @param subscriberListener Http回调结果的观察者
     */
    void emailCheckCode(String email,
                        String pwd,
                        Integer flag,
                        String ticket,
                        String randstr,
                        SubscriberListener subscriberListener);

    /**
     * 1.2.1手机用户注册
     *
     * @param mobileArea         国家码
     * @param mobile             手机号码
     * @param pwd                密码
     * @param vcode              验证码
     * @param subscriberListener Http回调结果的观察者
     */
    void mobileRegister(String mobileArea,
                        String mobile,
                        String pwd,
                        String vcode,
                        SubscriberListener subscriberListener);

    /**
     * 1.2.2邮箱用户注册
     *
     * @param email              邮箱
     * @param pwd                密码
     * @param vcode              验证码
     * @param subscriberListener Http回调结果的观察者
     */
    void emailRegister(String email,
                       String pwd,
                       String vcode,
                       SubscriberListener subscriberListener);

    /**
     * 1.3.1手机登录
     *
     * @param mobile             终端用户账号
     * @param mobileArea         地区
     * @param pwd                账号密码
     * @param uniqueId           设备唯一码
     * @param subscriberListener Http回调结果的观察者
     */
    void mobileLogin(String mobile,
                     String mobileArea,
                     String pwd,
                     String uniqueId,
                     SubscriberListener subscriberListener);

    /**
     * 1.3.2邮箱登录
     *
     * @param email              终端用户账号
     * @param pwd                账号密码
     * @param uniqueId           设备唯一码
     * @param subscriberListener Http回调结果的观察者
     */
    void emailLogin(String email,
                    String pwd,
                    String uniqueId,
                    SubscriberListener subscriberListener);

    /**
     * 1.4第三方登录
     *
     * @param thirdType          第三方登录类型，1：微信，2：QQ，3：微博，4：facebook，5：line等
     * @param uniqueId           终端唯一id,用于区分同一个用户的多个终端
     * @param code               第三方code
     * @param timeZone           当前时区，单位为秒。例如东八区28800
     * @param subscriberListener Http回调结果的观察者
     */
    void thirdLogin(Integer thirdType,
                    String uniqueId,
                    String code,
                    Integer timeZone,
                    SubscriberListener subscriberListener);

    /**
     * 1.4第三方注册
     *
     * @param thirdType          第三方登录类型，1：微信，2：QQ，3：微博，4：facebook，5：line等
     * @param uniqueId           终端唯一id,用于区分同一个用户的多个终端
     * @param unionIdToken       标识unionId的token
     * @param subscriberListener Http回调结果的观察者
     */
    void thirdRegister(Integer thirdType,
                       String uniqueId,
                       String unionIdToken,
                       SubscriberListener subscriberListener);

    /**
     * 1.5第三方绑定
     *
     * @param thirdType          第三方登录类型，1：微信，2：Facebook，3：QQ，4：微博等
     * @param uniqueId           终端唯一id,用于区分同一个用户的多个终端
     * @param bindMode           绑定方式，mobile：绑定手机，email：绑定邮箱
     * @param mobileArea         国家区号，当是绑定手机时，必填
     * @param mobile             手机号码，当是绑定手机时，必填
     * @param email              邮箱，当是绑定邮箱时，必填
     * @param pwd                用户密码
     * @param unionIdToken       标识unionId的token
     * @param subscriberListener Http回调结果的观察者
     */
    void thirdBindAccount(Integer thirdType,
                          String uniqueId,
                          String bindMode,
                          String mobileArea,
                          String mobile,
                          String email,
                          String pwd,
                          String unionIdToken,
                          SubscriberListener subscriberListener);

    /**
     * 1.6第三方解绑
     *
     * @param authType           账号认证类型，1：注册账号；2：微信；3：QQ等
     * @param subscriberListener Http回调结果的观察者
     */
    void thirdUnbind(Integer authType,
                     SubscriberListener subscriberListener);

    /**
     * 1.7获取用户accessToken
     *
     * @param uniqueId           设备唯一码
     * @param subscriberListener Http回调结果的观察者
     */
    void getAccessToken(String uniqueId,
                        SubscriberListener subscriberListener);

    /**
     * 1.8终端用户登出
     *
     * @param subscriberListener Http回调结果的观察者
     */
    void logout(SubscriberListener subscriberListener);

    /**
     * 1.9.1手机重置密码
     *
     * @param mobileArea         国家码
     * @param mobile             手机号码
     * @param pwd                用户重新设置的密码
     * @param vcode              验证码
     * @param subscriberListener Http回调结果的观察者
     */
    void mobileResetPwd(String mobileArea,
                        String mobile,
                        String pwd,
                        String vcode,
                        SubscriberListener subscriberListener);

    /**
     * 1.9.2邮箱重叠密码
     *
     * @param email              邮箱
     * @param pwd                用户重新设置的密码
     * @param vcode              验证码
     * @param subscriberListener Http回调结果的观察者
     */
    void emailResetPwd(String email,
                       String pwd,
                       String vcode,
                       SubscriberListener subscriberListener);

    /**
     * 1.10修改密码
     *
     * @param oldPwd             老密码
     * @param pwd                新密码
     * @param subscriberListener Http回调结果的观察者
     */
    void modifyPwd(String oldPwd,
                   String pwd,
                   SubscriberListener subscriberListener);

    /**
     * 1.11修改终端用户信息
     *
     * @param infoMap            参数键对，当前键名包括nick(昵称)，headUrl(头像url)
     * @param subscriberListener Http回调结果的观察者
     */
    void modifyInfo(Map<String, String> infoMap,
                    SubscriberListener subscriberListener);

    /**
     * 1.12查询终端用户信息
     *
     * @param subscriberListener Http回调结果的观察者
     */
    void queryInfo(SubscriberListener subscriberListener);

    /**
     * 1.13更新用户ivToken
     *
     * @param uniqueId           设备唯一码
     * @param subscriberListener Http回调结果的观察者
     */
    void replaceToken(String uniqueId,
                      SubscriberListener subscriberListener);

    /**
     * 1.14根据手机或邮箱或用户id查找用户
     *
     * @param mobileArea         国家电话区号，例如中国 86，当为电话号码时必填
     * @param condition          要匹配的手机号或邮箱或用户id
     * @param subscriberListener Http回调结果的观察者
     */
    void findUser(String mobileArea,
                  String condition,
                  SubscriberListener subscriberListener);

    /**
     * 1.15app注册推送token的绑定
     *
     * @param xingeToken         获取的注册的token
     * @param subscriberListener Http回调结果的观察者
     */
    void pushTokenBind(String xingeToken,
                       SubscriberListener subscriberListener);

    /**
     * 1.16app获取升级地址
     *
     * @param subscriberListener Http回调结果的观察者
     */
    void queryUrl(SubscriberListener subscriberListener);

    /**
     * 2.1上传app日志文件
     *
     * @param logfile            压缩的日志文件字节流
     * @param subscriberListener Http回调结果的观察者
     */
    void uploadLog(File logfile,
                   SubscriberListener subscriberListener);

    /**
     * 2.2App获取上传到腾讯云存的cos的授权信息
     *
     * @param subscriberListener Http回调结果的观察者
     */
    void getAuthInfo(SubscriberListener subscriberListener);

    /**
     * 2.3提交反馈信息
     *
     * @param type               问题分类，：1：连接问题 ；2：录像问题 ；3：离线问题 ；4：配网问题 ；5：云服务问题 ；9：其它问题
     * @param content            反馈的文本内容
     * @param url                反馈的图片url
     * @param logUrl             上传用户日志的url
     * @param subscriberListener Http回调结果的观察者
     */
    void feedbackSubmit(Integer type,
                        String content,
                        String url,
                        String logUrl,
                        SubscriberListener subscriberListener);

    /**
     * 2.4查询反馈信息列表
     *
     * @param subscriberListener Http回调结果的观察者
     */
    void feedbackList(SubscriberListener subscriberListener);

    /**
     * 2.5查询反馈详情
     *
     * @param feedbackId         反馈id
     * @param subscriberListener Http回调结果的观察者
     */
    void feedbackDetail(String feedbackId,
                        SubscriberListener subscriberListener);

    /**
     * 2.6查询系统消息列表
     *
     * @param currentPage        当前页
     * @param pageSize           页总数
     * @param subscriberListener Http回调结果的观察者
     */
    void noticeList(Integer currentPage,
                    Integer pageSize,
                    SubscriberListener subscriberListener);

    /**
     * 2.7查询系统消息详情
     *
     * @param noticeId           公告id
     * @param subscriberListener Http回调结果的观察者
     */
    void noticeDetail(String noticeId,
                      SubscriberListener subscriberListener);

    /**
     * 3.1查询设备被分享给的用户列表
     *
     * @param devId              设备id
     * @param subscriberListener Http回调结果的观察者
     */
    void listSharedUsers(String devId,
                         SubscriberListener subscriberListener);

    /**
     * 3.2分享邀请-账号方式
     *
     * @param shareId            被分享者的ivUid
     * @param devId              设备id
     * @param subscriberListener Http回调结果的观察者
     */
    void accountShare(String shareId,
                      String devId,
                      SubscriberListener subscriberListener);

    /**
     * 3.3分享邀请-接受分享
     *
     * @param ownerId            主人的ivUid
     * @param devId              设备id
     * @param accept             是否接受，1：接受；2：拒绝
     * @param subscriberListener Http回调结果的观察者
     */
    @Deprecated
    void acceptShare(String ownerId,
                     String devId,
                     Integer accept,
                     SubscriberListener subscriberListener);

    /**
     * 3.4生成分享二维码
     *
     * @param devId              设备id
     * @param deviceName         设备名称
     * @param userName           用户名称
     * @param subscriberListener Http回调结果的观察者
     */
    void genShareQrcode(String devId,
                        String deviceName,
                        String userName,
                        SubscriberListener subscriberListener);

    /**
     * 3.5分享邀请-扫描二维码方式
     *
     * @param qrcodeToken        生成的二维码token
     * @param subscriberListener Http回调结果的观察者
     */
    void scanShareQrcode(String qrcodeToken,
                         SubscriberListener subscriberListener);

    /**
     * 3.6取消邀请
     *
     * @param devId              devId 设备id
     * @param targetId           当分享者取消分享时，为被分享者的ivUid；当被分享者取消分享时，为分享者的为被分享者的ivUid
     * @param subscriberListener Http回调结果的观察者
     */
    void cancelShare(String devId,
                     String targetId,
                     SubscriberListener subscriberListener);

    /**
     * 4.1热度值套餐列表查询
     *
     * @param countryCode        国家二字码,如中国：CN
     * @param subscriberListener Http回调结果的观察者
     */
    void listHotValue(String countryCode,
                      SubscriberListener subscriberListener);

    /**
     * 4.2套餐列表查询
     *
     * @param countryCode        国家二字码,如中国：CN
     * @param serviceType        套餐服务类型，vss：全时套餐，evs：事件套餐
     * @param subscriberListener Http回调结果的观察者
     */
    void vasList(String countryCode,
                 String serviceType,
                 SubscriberListener subscriberListener);

    /**
     * 4.3查询设备已购买服务的概要
     *
     * @param devId              设备id
     * @param subscriberListener Http回调结果的观察者
     */
    void vasServiceOutline(String devId,
                           SubscriberListener subscriberListener);

    /**
     * 4.4查询设备所有支持的服务详情列表
     *
     * @param devId              设备id
     * @param subscriberListener Http回调结果的观察者
     */
    void vasServiceList(String devId,
                        SubscriberListener subscriberListener);

    /**
     * 4.5生成订单
     *
     * @param packageNo          套餐编号
     * @param devId              要绑定的设备id
     * @param timezone           时区，单位秒，如东八区28800
     * @param couponCode         优惠券编号
     * @param subscriberListener Http回调结果的观察者
     */
    void vasOrderGenerate(String packageNo,
                          String devId,
                          Integer timezone,
                          String couponCode,
                          SubscriberListener subscriberListener);

    /**
     * 4.6查询订单详情
     *
     * @param orderId            订单id
     * @param subscriberListener Http回调结果的观察者
     */
    void vasOrderQuery(String orderId,
                       SubscriberListener subscriberListener);

    /**
     * 4.7查询订单列表
     *
     * @param devId              订单id
     * @param orderStatus        订单状态
     * @param subscriberListener Http回调结果的观察者
     */
    void vasOrderList(String devId,
                      Integer orderStatus,
                      SubscriberListener subscriberListener);

    /**
     * 4.8订单信息总览
     *
     * @param devId              设备id
     * @param subscriberListener Http回调结果的观察者
     */
    void vasOrderOverview(String devId,
                          SubscriberListener subscriberListener);

    /**
     * 4.9生成支付签名信息
     *
     * @param orderId            订单id
     * @param payType            支付类型，微信或支付宝
     * @param subscriberListener Http回调结果的观察者
     */
    void vasPaymentGenerate(String orderId,
                            String payType,
                            SubscriberListener subscriberListener);

    /**
     * 4.10获取支付结果
     *
     * @param orderId            订单id
     * @param subscriberListener Http回调结果的观察者
     */
    void vasOrderResult(String orderId,
                        SubscriberListener subscriberListener);

    /**
     * 4.11获取云回放可回放列表
     *
     * @param orderId            订单id
     * @param timeZone           相对于0时区的秒数，例如东八区28800
     * @param subscriberListener Http回调结果的观察者
     */
    void cloudStorageList(String orderId,
                          Integer timeZone,
                          SubscriberListener subscriberListener);

    /**
     * 4.12云存回放列表
     *
     * @param devId              设备id
     * @param timeZone           相对于0时区的秒数，例如东八区28800
     * @param startTime          时间戳，单位毫秒，开始时间
     * @param endTime            时间戳，单位毫秒，结束时间
     * @param subscriberListener Http回调结果的观察者
     */
    void cloudStoragePlayback(String devId,
                              Integer timeZone,
                              long startTime,
                              long endTime,
                              SubscriberListener subscriberListener);

    /**
     * 4.13倍速回放
     *
     * @param devId              设备id
     * @param startTime          倍速回放的开始时间
     * @param speed              倍数
     * @param subscriberListener Http回调结果的观察者
     */
    void cloudStorageSpeedPlay(String devId,
                               long startTime,
                               Integer speed,
                               SubscriberListener subscriberListener);

    /**
     * 4.14下载视频m3u8列表
     *
     * @param devId              设备id
     * @param timeZone           相对于0时区的秒数，例如东八区28800
     * @param dateTime           时间戳，单位毫秒，为当天的零点零分零秒
     * @param subscriberListener Http回调结果的观察者
     */
    void cloudStorageDownload(String devId,
                              Integer timeZone,
                              long dateTime,
                              SubscriberListener subscriberListener);

    /**
     * 4.15购买云存套餐
     *
     * @param tid                腾讯设备ID
     * @param pkgId              套餐ID
     * @param orderCount         需要购买的套餐数量
     * @param storageRegion      云存服务所在的区域
     * @param subscriberListener 回调
     */
    void cloudStorageCreate(String tid,
                            String pkgId,
                            Integer orderCount,
                            String storageRegion,
                            SubscriberListener subscriberListener);

    /**
     * 5.1查看用户已经领取的优惠券列表
     *
     * @param subscriberListener Http回调结果的观察者
     */
    void couponOwnerList(SubscriberListener subscriberListener);

    /**
     * 5.2推送促销活动的信息列表
     *
     * @param promotionId        促销id
     * @param subscriberListener Http回调结果的观察者
     */
    void couponPopPromotion(String promotionId,
                            SubscriberListener subscriberListener);

    /**
     * 5.3获取指定推送优惠券信息列表
     *
     * @param couponIds          优惠券id列表，以,分割
     * @param subscriberListener Http回调结果的观察者
     */
    void couponPopAssignedCouponList(String couponIds,
                                     SubscriberListener subscriberListener);

    /**
     * 5.4领取优惠券，支持一键领取多张
     *
     * @param couponIds          优惠券id列表，以,分割
     * @param subscriberListener Http回调结果的观察者
     */
    void couponPopReceive(String couponIds,
                          SubscriberListener subscriberListener);

    /**
     * 5.5获取可用的优惠券列表
     *
     * @param packageNo          套餐编号
     * @param subscriberListener Http回调结果的观察者
     */
    void couponAvailableList(String packageNo,
                             SubscriberListener subscriberListener);

    /**
     * 5.6查询兑换码对应的商品（优惠活动的套餐信息）信息
     *
     * @param voucherCode        兑换码
     * @param subscriberListener Http回调结果的观察者
     */
    void couponVoucherPackInfo(String voucherCode,
                               SubscriberListener subscriberListener);

    /**
     * 5.7兑换码兑换对应的商品（优惠活动的套餐信息）
     *
     * @param voucherCode        兑换码
     * @param devId              设备id
     * @param timezone           时区描述
     * @param subscriberListener Http回调结果的观察者
     */
    void couponVoucherExchange(String voucherCode,
                               String devId,
                               Integer timezone,
                               SubscriberListener subscriberListener);

    /**
     * 6.1事件列表查询
     *
     * @param devId              设备id
     * @param startTime          单个事件的开始时间
     * @param endTime            单个事件的结束时间
     * @param lastId             倒序分页查看的最后一条记录ID
     * @param pageSize           每页总数
     * @param subscriberListener Http回调结果的观察者
     */
    void eventAlarmList(String devId,
                        long startTime,
                        long endTime,
                        Integer lastId,
                        Integer pageSize,
                        SubscriberListener subscriberListener);

    /**
     * 6.2事件删除
     *
     * @param eventIds
     * @param subscriberListener Http回调结果的观察者
     */
    void eventAlarmDelete(String eventIds,
                          SubscriberListener subscriberListener);

    /**
     * 6.3查询预置位列表
     *
     * @param devId              设备Id
     * @param subscriberListener Http回调结果的观察者
     */
    void prePositionList(String devId,
                         SubscriberListener subscriberListener);

    /**
     * 6.4添加预置位
     *
     * @param devId              设备Id
     * @param serialNumber       预置位序号，最大值为6，最多6个预置位
     * @param positionName       预置位名称
     * @param positionUrl        预置位url
     * @param x                  x坐标
     * @param y                  y坐标
     * @param subscriberListener Http回调结果的观察者
     */
    void prePositionAdd(String devId,
                        Integer serialNumber,
                        String positionName,
                        String positionUrl,
                        String x,
                        String y,
                        SubscriberListener subscriberListener);

    /**
     * 6.5修改预置位名称
     *
     * @param positionId         预置位id
     * @param positionName       预置位名称
     * @param subscriberListener Http回调结果的观察者
     */
    void prePositionModify(String positionId,
                           String positionName,
                           SubscriberListener subscriberListener);

    /**
     * 6.6删除预置位
     *
     * @param positionId         预置位id
     * @param subscriberListener Http回调结果的观察者
     */
    void prePositionDelete(String positionId,
                           SubscriberListener subscriberListener);

    /**
     * 7.1用户设备绑定
     *
     * @param devId              设备Id
     * @param tid                设备tid
     * @param remarkName         设备名称，如果为空，默认为在控制台创建设备时的名字（一期设备时，最多24个字符，二期最多64个字符）
     * @param permission         权限（初始为0，表示不支持权限配置）
     * @param forceBind          true:强制绑定（踢掉原用户），false:不能踢掉原用户
     * @param subscriberListener Http回调结果的观察者
     */
    void deviceBind(String devId,
                    String tid,
                    String remarkName,
                    long permission,
                    boolean forceBind,
                    SubscriberListener subscriberListener);

    void deviceBind(String devId,
                    boolean forceBind,
                    SubscriberListener subscriberListener);

    /**
     * 7.2用户设备解绑定
     *
     * @param devId              设备Id
     * @param subscriberListener Http回调结果的观察者
     */
    void deviceUnbind(String devId,
                      SubscriberListener subscriberListener);

    /**
     * 7.3查看设备列表
     *
     * @param subscriberListener Http回调结果的观察者
     */
    void deviceList(SubscriberListener subscriberListener);

    /**
     * 创建匿名访问Token
     *
     * @param ttlMinutes         Token的TTL(time to alive)分钟数,最大值1440(即24小时)
     * @param tid                设备ID。创建Token时, 此参数为必须项
     * @param oldAccessToken     旧的AccessToken。续期Token时，此参数为必须
     * @param subscriberListener Http回调结果的观察者
     */
    void createAnonymousAccessToken(int ttlMinutes,
                                    String tid,
                                    String oldAccessToken,
                                    SubscriberListener subscriberListener);
}