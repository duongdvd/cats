package jp.co.willwave.aca.dto.api;

import jp.co.willwave.aca.common.ErrorCodeConfig;
import lombok.Data;

@Data
public class ResponseDTO<T> {
    private boolean result;
    private int errorCode = ErrorCodeConfig.ERROR_SYSTEM;
    private String message;
    private T body;

    public ResponseDTO() {
    }

    private ResponseDTO(boolean result) {
        this.result = result;
    }

    private ResponseDTO(boolean result, T body) {
        this.result = result;
        this.body = body;
    }

    public ResponseDTO success() {
        return new ResponseDTO(true);
    }

    public ResponseDTO success(T body) {
        return new ResponseDTO(true, body);
    }

    public ResponseDTO error() {
        return new ResponseDTO(false);
    }

    public ResponseDTO errorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public ResponseDTO message(String message) {
        this.message = message;
        return this;
    }

    public ResponseDTO body(T body) {
        this.body = body;
        return this;
    }

}
