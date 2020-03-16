package com.tencentcs.iotvideo.http;

/**
 * Created by QSH on 2019/12/11.
 * 服务器error_code返回大全
 */

public class HttpCode {
    /**
     * ok
     */
    public static final int ERROR_0 = 0;

    /**
     * 未登录或者超时
     */
    public static final int ERROR_801001001 = 801001001;

    /**
     * 缺少SESSIONID输入参数
     */
    public static final int ERROR_801001002 = 801001002;

    /**
     * 缺少输入参数
     */
    public static final int ERROR_801001003 = 801001003;

    /**
     * 系统正在维护，请稍后再试
     */
    public static final int ERROR_801001004 = 801001004;

    /**
     * KEY不正确
     */
    public static final int ERROR_801001005 = 801001005;

    /**
     * 该账号已被使用
     */
    public static final int ERROR_801002001 = 801002001;

    /**
     * 注册失败
     */
    public static final int ERROR_801002002 = 801002002;

    /**
     * 验证码不正确
     */
    public static final int ERROR_801002003 = 801002003;

    /**
     * 验证码不存在
     */
    public static final int ERROR_801002004 = 801002004;

    /**
     * 验证码超时
     */
    public static final int ERROR_801002005 = 801002005;

    /**
     * 发送验证码失败
     */
    public static final int ERROR_801002006 = 801002006;

    /**
     * 账号不存在
     */
    public static final int ERROR_801002007 = 801002007;

    /**
     * 密码错误
     */
    public static final int ERROR_801002008 = 801002008;

    /**
     * 账号未注册
     */
    public static final int ERROR_801002009 = 801002009;

    /**
     * 密码重置失败
     */
    public static final int ERROR_801002010 = 801002010;

    /**
     * 更新密码失败
     */
    public static final int ERROR_801002011 = 801002011;

    /**
     * 注销失败
     */
    public static final int ERROR_801002012 = 801002012;

    /**
     * 更新用户信息失败
     */
    public static final int ERROR_801002013 = 801002013;

    /**
     * 用户不存在
     */
    public static final int ERROR_801002014 = 801002014;

    /**
     * 不支持表情输入
     */
    public static final int ERROR_801002015 = 801002015;

    /**
     * 用户CODE资源不足
     */
    public static final int ERROR_801002016 = 801002016;

    /**
     * 密码错误登录超过5次，请1小时后重试
     */
    public static final int ERROR_801002017 = 801002017;

    /**
     * 国家码不支持发送手机短信
     */
    public static final int ERROR_801002018 = 801002018;

    /**
     * 初次绑定账号，未设置密码
     */
    public static final int ERROR_801002019 = 801002019;

    /**
     * 设备不合法
     */
    public static final int ERROR_801003001 = 801003001;

    /**
     * 您已经绑定该设备了
     */
    public static final int ERROR_801003002 = 801003002;

    /**
     * 设备被其他人绑定了
     */
    public static final int ERROR_801003003 = 801003003;

    /**
     * 设备不存在
     */
    public static final int ERROR_801003004 = 801003004;

    /**
     * 设备已经创建群了
     */
    public static final int ERROR_801003005 = 801003005;

    /**
     * 设备主人才可以把设备加入到一个群中
     */
    public static final int ERROR_801003006 = 801003006;

    /**
     * 设备未绑定
     */
    public static final int ERROR_801003007 = 801003007;

    /**
     * 设备未被自己绑定
     */
    public static final int ERROR_801003008 = 801003008;

    /**
     * 设备解绑失败
     */
    public static final int ERROR_801003009 = 801003009;

    /**
     * 门铃不存在
     */
    public static final int ERROR_801003010 = 801003010;

    /**
     * 设备更新
     */
    public static final int ERROR_801003011 = 801003011;

    /**
     * 设备还在群中
     */
    public static final int ERROR_801003012 = 801003012;

    /**
     * 设备已经加入了其他群
     */
    public static final int ERROR_801003013 = 801003013;

    /**
     * 设备初始化参数不存在
     */
    public static final int ERROR_801003014 = 801003014;

    /**
     * 绑定设备的用户ID不合法
     */
    public static final int ERROR_801003015 = 801003015;

    /**
     * TOKEN不合法
     */
    public static final int ERROR_801003016 = 801003016;

    /**
     * 锁的管理ID获取失败
     */
    public static final int ERROR_801003017 = 801003017;

    /**
     * 锁绑定确认信息错误
     */
    public static final int ERROR_801003018 = 801003018;

    /**
     * 设备不能分享给自己
     */
    public static final int ERROR_801003019 = 801003019;

    /**
     * 设备分享不存在或非法
     */
    public static final int ERROR_801003020 = 801003020;

    /**
     * 数字密码标记删除失败
     */
    public static final int ERROR_801003021 = 801003021;

    /**
     * 密码已用完，请尝试重置密码
     */
    public static final int ERROR_801003022 = 801003022;

    /**
     * 数字密码生成失败
     */
    public static final int ERROR_801003023 = 801003023;

    /**
     * 管理ID获取失败
     */
    public static final int ERROR_801003024 = 801003024;

    /**
     * 设备未申请重置动态密码
     */
    public static final int ERROR_801003025 = 801003025;

    /**
     * 设备设置失败
     */
    public static final int ERROR_801003026 = 801003026;

    /**
     * 报警记录上传失败
     */
    public static final int ERROR_801003027 = 801003027;

    /**
     * 锁的ID获取失败
     */
    public static final int ERROR_801003028 = 801003028;

    /**
     * 设备主人才可以创建群
     */
    public static final int ERROR_801004001 = 801004001;

    /**
     * 创建群失败
     */
    public static final int ERROR_801004002 = 801004002;

    /**
     * 群主才可以修改群
     */
    public static final int ERROR_801004003 = 801004003;

    /**
     * 更新群信息失败
     */
    public static final int ERROR_801004004 = 801004004;

    /**
     * 群不存在
     */
    public static final int ERROR_801004005 = 801004005;

    /**
     * 群成员才可以查看群信息
     */
    public static final int ERROR_801004006 = 801004006;

    /**
     * 超过群成员上限
     */
    public static final int ERROR_801004007 = 801004007;

    /**
     * 超过群上限
     */
    public static final int ERROR_801004008 = 801004008;

    /**
     * 转移的群必须是群主
     */
    public static final int ERROR_801004009 = 801004009;

    /**
     * 添加群成员失败
     */
    public static final int ERROR_801005001 = 801005001;

    /**
     * 已经是群成员了
     */
    public static final int ERROR_801005002 = 801005002;

    /**
     * 群主才可以删除成员
     */
    public static final int ERROR_801005003 = 801005003;

    /**
     * 群成员不存在
     */
    public static final int ERROR_801005004 = 801005004;

    /**
     * 群主不可以删除自己
     */
    public static final int ERROR_801005005 = 801005005;

    /**
     * 删除群成员失败
     */
    public static final int ERROR_801005006 = 801005006;

    /**
     * 群成员退群失败
     */
    public static final int ERROR_801005007 = 801005007;

    /**
     * 群成员转移失败
     */
    public static final int ERROR_801005008 = 801005008;

    /**
     * 群主才可以解散群
     */
    public static final int ERROR_801005009 = 801005009;

    /**
     * 群解散失败
     */
    public static final int ERROR_801005010 = 801005010;

    /**
     * 群里有设备不能解散
     */
    public static final int ERROR_801005011 = 801005011;

    /**
     * 不能转移到同一个群
     */
    public static final int ERROR_801005012 = 801005012;

    /**
     * 保存用户反馈失败
     */
    public static final int ERROR_801006001 = 801006001;

    /**
     * 升级信息不存在
     */
    public static final int ERROR_801007001 = 801007001;

    /**
     * 系统消息已失效
     */
    public static final int ERROR_801008001 = 801008001;

    /**
     * 无权限访问
     */
    public static final int ERROR_801008002 = 801008002;

    /**
     * 定时参数不存在
     */
    public static final int ERROR_802001001 = 802001001;

    /**
     * 类型不存在
     */
    public static final int ERROR_802002001 = 802002001;

    /**
     * 二维码已失效
     */
    public static final int ERROR_803001001 = 803001001;

    /**
     * 发送一次性订阅消息失败
     */
    public static final int ERROR_803002001 = 803002001;

    /**
     * 请求微信用户access_token失败
     */
    public static final int ERROR_803002002 = 803002002;

    /**
     * 请求微信用户个人信息失败
     */
    public static final int ERROR_803002003 = 803002003;

    /**
     * 该第三方登录方式暂未开放
     */
    public static final int ERROR_803002004 = 803002004;

    /**
     * 第一次使用该第三方账号登录
     */
    public static final int ERROR_803002005 = 803002005;

    /**
     * 该第三方账号已绑定用户
     */
    public static final int ERROR_803002006 = 803002006;

    /**
     * 该授权token错误
     */
    public static final int ERROR_803002007 = 803002007;

    /**
     * 该账号已绑定了第三方账号
     */
    public static final int ERROR_803002008 = 803002008;
}
