package jp.co.willwave.aca.utilities;

import jp.co.willwave.aca.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidatorUtil {
    @Autowired
    private CatsMessageResource catsMessageResource;

    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

    public List<Message> validate(BindingResult result) {
        List<Message> messages = new ArrayList<Message>();

        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                String messageInput = fieldError.getDefaultMessage();
                String messageCode = CatStringUtil.EMPTY;
                String[] params = null;
                if (!CatStringUtil.isEmpty(messageInput)) {
                    String[] arrMessages = messageInput.split(",");
                    messageCode = arrMessages[0].trim();
                    if (arrMessages.length > 1) {
                        params = new String[arrMessages.length - 1];
                        for (int i = 1; i < arrMessages.length; i++) {
                            params[i - 1] = arrMessages[i].trim();
                        }
                    }
                }
                if (params == null || params.length == 0) {
                    messages.add(catsMessageResource.getMessage(messageCode, params));
                } else {
                    messages.add(catsMessageResource.getWithParamKeys(messageCode, params));
                }
            }
        }

        return messages;
    }

    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
