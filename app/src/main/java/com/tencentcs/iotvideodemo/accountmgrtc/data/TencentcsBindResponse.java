package com.tencentcs.iotvideodemo.accountmgrtc.data;

public class TencentcsBindResponse {

    /**
     * Response : {"AccessToken":"01D7474F65000000986C914B6ABBC16D89F564FFB5A1C9963852C2CB17E2A6CF1DDF4997B4603261F79FAC63E9D852BBD34EAA012A7475122A80CEF74D2825E2","RequestId":"81b676c6-0753-42e7-b26d-9a0e2db25ff7"}
     */

    private ResponseBean Response;

    public ResponseBean getResponse() {
        return Response;
    }

    public void setResponse(ResponseBean Response) {
        this.Response = Response;
    }

    public static class ResponseBean {
        /**
         * AccessToken : 01D7474F65000000986C914B6ABBC16D89F564FFB5A1C9963852C2CB17E2A6CF1DDF4997B4603261F79FAC63E9D852BBD34EAA012A7475122A80CEF74D2825E2
         * RequestId : 81b676c6-0753-42e7-b26d-9a0e2db25ff7
         */

        private String AccessToken;
        private String RequestId;

        public String getAccessToken() {
            return AccessToken;
        }

        public void setAccessToken(String AccessToken) {
            this.AccessToken = AccessToken;
        }

        public String getRequestId() {
            return RequestId;
        }

        public void setRequestId(String RequestId) {
            this.RequestId = RequestId;
        }
    }
}
