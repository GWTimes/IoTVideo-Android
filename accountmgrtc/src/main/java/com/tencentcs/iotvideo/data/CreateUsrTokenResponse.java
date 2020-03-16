package com.tencentcs.iotvideo.data;

public class CreateUsrTokenResponse extends BaseResponse {
    /**
     * TerminalId : -9151314009025149975
     * AccessToken : 01030F46F67770125EBBA13E65000000724C1D35865C7E367F6CAB7ECEF9F37FC5250E7812C50D3A91B36F2C7EE5A3FACD637CFC35EF5686E8B874553546679B
     * AccessId : -9223371603063077911
     * ExpireTime : 1583938882
     * RequestId : 12881c68-d42f-4349-855d-137786e03acc
     */

    private String TerminalId;
    private String AccessToken;
    private String AccessId;
    private int ExpireTime;
    private String RequestId;

    public String getTerminalId() {
        return TerminalId;
    }

    public void setTerminalId(String TerminalId) {
        this.TerminalId = TerminalId;
    }

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String AccessToken) {
        this.AccessToken = AccessToken;
    }

    public String getAccessId() {
        return AccessId;
    }

    public void setAccessId(String AccessId) {
        this.AccessId = AccessId;
    }

    public int getExpireTime() {
        return ExpireTime;
    }

    public void setExpireTime(int ExpireTime) {
        this.ExpireTime = ExpireTime;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String RequestId) {
        this.RequestId = RequestId;
    }
}
