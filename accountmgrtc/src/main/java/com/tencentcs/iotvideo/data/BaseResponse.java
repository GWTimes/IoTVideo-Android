package com.tencentcs.iotvideo.data;

public class BaseResponse {
    private ErrorBean Error;

    public ErrorBean getError() {
        return Error;
    }

    public void setError(ErrorBean Error) {
        this.Error = Error;
    }

    public static class ErrorBean {
        /**
         * Code : InvalidParameter
         * Message : AccessId格式或类型错误
         */

        private String Code;
        private String Message;

        public String getCode() {
            return Code;
        }

        public void setCode(String Code) {
            this.Code = Code;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String Message) {
            this.Message = Message;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "Code='" + Code + '\'' +
                    ", Message='" + Message + '\'' +
                    '}';
        }
    }
}
