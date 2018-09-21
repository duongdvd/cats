package jp.co.willwave.aca.common;

import jp.co.willwave.aca.model.Message;
import lombok.Data;

@Data
public class LogicException extends Exception {
    private static final long serialVersionUID = 1L;
    private Message errorMessage;
    private int errorCode;

    public LogicException() {
        super();
    }

    public LogicException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public LogicException(Message errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public LogicException(Exception e) {
        super(e);
    }

    public Message getErrorMessage() {
        return errorMessage;
    }

}
