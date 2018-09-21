package jp.co.willwave.aca.exception;

import jp.co.willwave.aca.model.Message;

public class LogicWebException extends CommonException {
    private Message errorMessage;
    private static final long serialVersionUID = 2L;

    public LogicWebException() {
        super();
    }

    public LogicWebException(String message) {
        super(message);
    }

    public LogicWebException(Message errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public LogicWebException(Exception e) {
        super(e);
    }

    public Message getErrorMessage() {
        return errorMessage;
    }
}
