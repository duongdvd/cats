package jp.co.willwave.aca.exception;

import jp.co.willwave.aca.model.Message;

public class CommonException extends Exception {
    private Message errorMessage;
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CommonException() {
        super();
    }

    public CommonException(String message) {
        super(message);
    }

    public CommonException(Message errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public CommonException(Exception e) {
        super(e);
    }

    public Message getErrorMessage() {
        return errorMessage;
    }
}