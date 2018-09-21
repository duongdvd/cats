package jp.co.willwave.aca.contraints;

import com.google.common.base.Joiner;
import jp.co.willwave.aca.utilities.PasswordUtils;
import org.passay.*;
import org.passay.spring.SpringMessageResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 200;
    public static final int NUM = 1;
    @Autowired
    private MessageSource messageSource;

    @Override
    public void initialize(ValidPassword arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(new SpringMessageResolver(messageSource),
                Arrays.asList(new LengthRule(MIN_LENGTH, MAX_LENGTH),
                new CharacterRule(EnglishCharacterData.LowerCase, NUM),
                new CharacterRule(EnglishCharacterData.UpperCase, NUM),
                new CharacterRule(EnglishCharacterData.Digit, NUM),
                new CharacterRule(PasswordUtils.CustomCharacterData.Special, NUM),
                new WhitespaceRule()));

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                Joiner.on(",").join(validator.getMessages(result)))
                .addConstraintViolation();
        return false;
    }
}
