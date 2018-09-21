package jp.co.willwave.aca.utilities;

import jp.co.willwave.aca.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);
    private static final String YYYY_MM_DD_HH_MM_SSS_SS = "YYYY/MM/DD HH:MM:SS SSS";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_SLASH = "yyyy/MM/dd";
    public static final String YYYY_MM_DD_HH_MM_SS_SLASH = "yyyy/MM/dd HH:mm:ss";
    public static final String DD = "dd";
    public static final String YYYY_MM_DASH = "yyyy-MM";
    public static final String COLON = ":";

    public static final int COUNTITEM = 7;
    public static final int FRIST = 0;
    private static final SimpleDateFormat acaDF = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SSS_SS);

    private static final SimpleDateFormat acaShortDF = new SimpleDateFormat(YYYY_MM_DD);
    public static final int TWO = 1;
    public static final int THREE = 2;
    public static final int FOUR = 3;
    public static final int FIVE = 4;
    public static final int SIX = 5;
    public static final int END_INDEX_MONTH = 6;
    public static final int SEVEN = 6;
    public static final int END_INDEX_YEAR = 4;
    public static final int END_INDEX_DAY = 8;
    public static final int END_INDEX_HOUR = 10;
    public static final int END_INDEX_MINUTE = 12;
    public static final int END_INDEX_SECOND = 14;
    public static final int END_INDEX = 17;
    public static final int DATE_LENGTH = 14;

    /**
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String convertSimpleDateFormat(Date date, String pattern) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String getCurrentDate(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }

    /**
     * Get System Date.
     *
     * @return
     */
    // Get current date as string
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * Get current date as string.
     *
     * @return
     */
    public static String getCurrentDateString() {

        return acaDF.format(getCurrentDate());
    }

    public static String getCurrentShortDateString() {

        return acaShortDF.format(getCurrentDate());
    }

    public static String convertSimpleDateFormat(String date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }


    /**
     * @param stringDate
     * @param pattern
     * @return
     * @throws Exception
     */
    public static Date convertStringSimpleDateFormat(String stringDate, String pattern) throws CommonException {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(stringDate);
        } catch (ParseException e) {
            LOG.error(e.getMessage(), e);
            throw new CommonException(e);
        }
    }

    public static String convertStringToDateFormat(String stringDate) throws Exception {
        String dateString = "";
        if (stringDate != null&&stringDate.length()>=DATE_LENGTH) {
            String[] inputObj = new String[COUNTITEM];
            inputObj[FRIST] = stringDate.substring(0, END_INDEX_YEAR);
            inputObj[TWO] = stringDate.substring(END_INDEX_YEAR, END_INDEX_MONTH);
            inputObj[THREE] = stringDate.substring(END_INDEX_MONTH, END_INDEX_DAY);
            inputObj[FOUR] = stringDate.substring(END_INDEX_DAY, END_INDEX_HOUR);
            inputObj[FIVE] = stringDate.substring(END_INDEX_HOUR, END_INDEX_MINUTE);
            inputObj[SIX] = stringDate.substring(END_INDEX_MINUTE, END_INDEX_SECOND);

            dateString = String.format("%-4s/%-2s/%-2s %-2s:%-2s:%-2s", inputObj);
        }
        return dateString;
    }

    public static String convertDateToStringFormat(String stringDate) {
        String dateString = stringDate.replace("/", "").replace(":", "").replace(" ", "");
        return dateString;
    }

    /**
     * Get period time between two date.
     *
     * @param s start date.
     * @param e end date.
     * @return (d1-d2) millisecond.
     */
    public static Long subtract(Date s, Date e) {
        return e.getTime() - s.getTime();
    }

    /**
     * Convert date(millisecond) to format minute:second.
     *
     * @param millis millisecond.
     *
     * @return String minute:second.
     */
    public static String ToMinuteSecond(Long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return String.format("%02d", minutes) + COLON + String.format("%02d", seconds);
    }

    public static Date castDate(Object object) {
        if (object == null) return null;
        return (Date) object;
    }
}
