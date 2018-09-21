package jp.co.willwave.aca.utilities;

import jp.co.willwave.aca.constants.BusinessConstants;
import jp.co.willwave.aca.contraints.PasswordConstraintValidator;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class PasswordUtils {
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final int MAX_BYTE_TOKEN = 128;

    public static String getSalt() {
        int length = BusinessConstants.MAX_LENGTH_SALT;
        StringBuilder returnValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }

    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public static String generateSecurePassword(String password, String salt) {
        String returnValue;
        byte[] securePassword = hash(password.toCharArray(), salt.getBytes());
        returnValue = Base64.getEncoder().encodeToString(securePassword);
        return returnValue;
    }

    public static boolean verifyUserPassword(String providedPassword,
                                             String securedPassword, String salt) {
        boolean returnValue;
        String newSecurePassword = generateSecurePassword(providedPassword, salt);
        returnValue = newSecurePassword.equalsIgnoreCase(securedPassword);
        return returnValue;
    }

    public static String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[MAX_BYTE_TOKEN];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

    public static String generateValidPassword() {
        PasswordGenerator generator = new PasswordGenerator();
        return generator.generatePassword(PasswordConstraintValidator.MIN_LENGTH,
                new CharacterRule(EnglishCharacterData.LowerCase, PasswordConstraintValidator.NUM),
                new CharacterRule(EnglishCharacterData.UpperCase, PasswordConstraintValidator.NUM),
                new CharacterRule(EnglishCharacterData.Digit, PasswordConstraintValidator.NUM),
                new CharacterRule(CustomCharacterData.Special, PasswordConstraintValidator.NUM));
    }

    /**
     * Custom character data for Passay
     */
    public enum CustomCharacterData implements CharacterData {
        /** Special characters. */
        Special(
        "INSUFFICIENT_SPECIAL",
        // ASCII symbols
        "!#$%&'()*+-./:;<=>?@[]^_`{|}~");


        /** Error code. */
        private final String errorCode;

        /** Characters. */
        private final String characters;


        /**
         * @param  code  Error code.
         * @param  charString  Characters as string.
         */
        CustomCharacterData(final String code, final String charString)
        {
            errorCode = code;
            characters = charString;
        }

        @Override
        public String getErrorCode()
        {
            return errorCode;
        }

        @Override
        public String getCharacters()
        {
            return characters;
        }
    }

}
