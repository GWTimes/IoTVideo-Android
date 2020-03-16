package com.tencentcs.iotvideo.data;

public class CreateAppUsrResponse extends BaseResponse {
    /**
     * CunionId : test001
     * AccessId : -9223371603063077911
     * NewRegist : false
     * RequestId : 18a68ec0-5550-4301-ba6b-e02d0c12ee29
     */

    private String CunionId;
    private String AccessId;
    private boolean NewRegist;
    private String RequestId;

    public String getCunionId() {
        return CunionId;
    }

    public void setCunionId(String CunionId) {
        this.CunionId = CunionId;
    }

    public String getAccessId() {
        return AccessId;
    }

    public void setAccessId(String AccessId) {
        this.AccessId = AccessId;
    }

    public boolean isNewRegist() {
        return NewRegist;
    }

    public void setNewRegist(boolean NewRegist) {
        this.NewRegist = NewRegist;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String RequestId) {
        this.RequestId = RequestId;
    }
}
