package jp.co.willwave.aca.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CatStringUtil extends StringUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CatStringUtil.class);

    public static final String EMPTY = "";
    public static final int NUMBER_255 = 255;

    public static boolean checkAlphabet(String string) {
        for (char ch : string.toCharArray()) {
            if (!(Character.isLetter(ch)) && !(Character.isDigit(ch))) {
                return false;
            }
        }
        return true;
    }

    public static Optional<String> string(Object value) {
        if (value == null) return Optional.empty();
        return Optional.of(value.toString());
    }

    public static String stringValue(Object value) {
        return CatStringUtil.string(value).orElse(null);
    }

    public static boolean isNumeric(String number) {
        if(StringUtils.isEmpty(number)) {
            return false;
        }
        for (char ch : number.toCharArray()) {
            if (!(Character.isDigit(ch))) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkJapanese(String string) {
        for (char ch : string.toCharArray()) {
            if (!((int) ch <= NUMBER_255)) {
                return false;
            }
        }
        return true;
    }

    public static boolean contains(String str1, String str2) {
        if (isEmpty(str1) || isEmpty(str2)) {
            return false;
        }

        if (str1.contains(str2)) {
            return true;
        }
        return false;
    }

    public static boolean equals(String str1, String str2) {
        if (isEmpty(str1) || isEmpty(str2)) {
            return false;
        }

        if (str1.equals(str2)) {
            return true;
        }
        return false;
    }

    public static List<String> getListFromString(String str, String stringIndex) {
        List<String> strings = new ArrayList<String>();
        try {
            strings = Arrays.asList(str.split(stringIndex));
            return strings;

        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error(" Convert string to list string : " + ex.getMessage());
            return strings;
        }
    }

    public static String getStringFromList(String stringIndex, List<String> strings) {
        String string = "";
        try {
            string = String.join(stringIndex, strings);
            return string;
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error(" Convert list to string : " + ex.getMessage());
            return string;
        }
    }

    public static int parseInt(String number) {
        int result = 0;
        if(number != null) {
            result = Integer.parseInt(number);
        }
        return result;
    }

    public static boolean isNotEmpty(Object str) {
        return (!isEmpty(str));
    }
}