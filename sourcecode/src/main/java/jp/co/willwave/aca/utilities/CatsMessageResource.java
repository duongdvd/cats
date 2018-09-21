package jp.co.willwave.aca.utilities;

import jp.co.willwave.aca.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CatsMessageResource {
    @Autowired
    private MessageSource messageSource;

    public String get(String errorCode, Object[] args) {
        return messageSource.getMessage(errorCode, args, Locale.JAPAN);
    }

    public Message get(String errorCode) {
        return new Message(errorCode, messageSource.getMessage(errorCode, null, Locale.JAPAN));
    }

    public Message getMessage(String errorCode, Object[] args) {
        return new Message(errorCode, messageSource.getMessage(errorCode, args, Locale.JAPAN));
    }

    public Message getMessage(String errorCode, Object[] args, Message.MessageTye type) {
        return new Message(errorCode, messageSource.getMessage(errorCode, args, Locale.JAPAN), type);
    }

    public Message getWithParamKeys(String errorCode, Object[] args) {
        String[] paramKeys = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            paramKeys[i] = get(String.valueOf(args[i]), null);
        }
        return new Message(errorCode, messageSource.getMessage(errorCode, paramKeys, Locale.JAPAN));
    }

    public Message info(String code, Object... args) {
        return Message.info(code, messageSource.getMessage(code, args, Locale.JAPAN));
    }
}
