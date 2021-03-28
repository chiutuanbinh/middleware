package me.binhct.middleware.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Response {
    public static class Error {
        private String code;
        private String message;

        public Error() {
            super();
        }

        public Error(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private Error error;
    private Object value;

    public Response(){
        this(null, null);
    }

    public Response(Error error, Object value) {
        this.error = error;
        this.value = value;
    }

    public Error getError() {
        return error;
    }

    public Object getValue() {
        return value;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
