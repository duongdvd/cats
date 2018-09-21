package jp.co.willwave.aca.model.constant;

/**
 * @author p-khanhnv
 */
public class Constant {
    public static final long SEARCH_LIMIT_DEFAULT = 100L;
    public static final long SEARCH_SKIP_DEFAULT = 0L;

    public static final String UTF_8 = "UTF-8";
    public static final String INVALID_TOKEN = "token.invalid";

    public static int MAX_SEARCH_RESULT = 10;

    public static class Session {
        public static final String USER_LOGIN_INFO = "userLoginInfo";
        public static final String USER_LOGIN_ROLE = "userLoginRole";
        public static final String MESSAGES = "Messages";
        public static final String ADMIN_LOGIN_INFO = "adminLoginInfo";
        public static final String CUSTOMER_TYPE = "customerType";
        public static final String ROUTE_HAS_CHANGE = "routeHasChange";
        public static final String ACCESS_PATHS = "accessPaths";
    }

    // Salt for encryptOneWay using SHA-512.
    public static final String SHA_512_PASSWORD_KEY = "RelipaSoft!Password@#$20180423150155";
    public static final String SHA_512_TOKEN_KEY = "RelipaSoft!Token@#$20180423150256";
    public static final String VALID_PASSWORD = "RelipaSoft@201804";

    public static class ErrorCode {
        public static final String NOT_EMPTY = "E0001";
        public static final String USER_OR_PASS_INCORRECT = "E0002";
        public static final String ALREADY_EXISTS = "E0003";
        public static final String FORMAT_INVALID = "E0004";
        public static final String HAVE_NO_PERMISSION = "E0005";
        public static final String NOT_FOUND_DATA = "E0006";
        public static final String PASS_CONFIRM_NOT_MATCH = "E0007";
        public static final String NOT_EXISTS = "E0008";
        public static final String NOT_UPDATE_DATA = "E0009";
        public static final String NOT_INSERT_DATA = "E0010";
        public static final String OLD_NEW_PASSWORD_SAME = "E0011";
        public static final String ALPHA_CHARACTER = "E0012";
        public static final String LOGIN_REQUIRE = "E0013";
        public static final String SEND_MAIL_FAILED = "E0014";
        public static final String EDIT_ERROR = "E0016";
        public static final String USER_MANAGED_DIVISION = "E0017";
        public static final String DIVISION_NAME = "E0018";
        public static final String DIVISION_PARENT = "E0019";
        public static final String NOT_PERMISSION_MANAGED_DIVISION = "E0031";
        public static final String EXISTS_CONSTRAIN_DIVISION = "E0032";
        public static final String DELETE_CONSTRAINT = "E0030";
        public static final String DEVICE_ALREADY_USED = "E0020";
        public static final String ROUTE_IS_RUNNING = "E0021";
        public static final String NOT_FORMAT = "E0015";
        public static final String COMPANY_EXIST_DIVISION = "E0040";
        public static final String QUICKBLOX_ERROR = "E0060";
        public static final String USER_NOT_MANAGED_DEVICE = "E0099";
        public static final String NOT_FOUND_USER = "E0041";
        public static final String NOT_FOUND_SYSADMIN = "E0042";
        public static final String NOT_FOUND_USERID = "E0043";
        public static final String NOT_FOUND_COMPANY = "E0044";
        public static final String LOGIN_FAIL = "E0045";
        public static final String NOT_FOUND_LOGINID = "E0046";
        public static final String UUID_INVALID = "E0047";
        public static final String INPUT_NOT_CORRECT = "E0048";
        public static final String DEVICES_NOT_FOUND = "E0049";
        public static final String DIVISION_PERMISSION = "E0050";
        public static final String NOT_OPERATOR = "E0051";
        public static final String DIVISON_PERMISION = "E0052";
        public static final String NOT_DEVICE = "E0053";
        public static final String NOT_CAR_OF_DEVICE = "E0054";
        public static final String USER_ID = "E0055";
        public static final String ERROR_DATABASE = "E0056";
        public static final String ROUTE_NOT_FOUND = "E0057";
        public static final String MESSAGE_NOT_FOUND = "E0058";
        public static final String ROUTE_FINISH = "E0059";
        public static final String MAKER_HAS_NOT = "E0061";
        public static final String ROUTE_PLAN = "E0062";
        public static final String ROUTE_ACTUAL_CREATE = "E0063";
        public static final String NOT_FOUND_ROUTE = "E0064";
        public static final String TAKE_OUT = "E0065";
        public static final String USER_NOT_EXIST = "E0066";
        public static final String CUR_PASSWORD_INCORRECT = "E0068";
        public static final String CUS_INACTIVE = "E0069";
        public static final String DEVICE_LOGIN_ID_EXIST = "E0070";
        public static final String EXPORT_NO_DATA = "E0071";
        public static final String ROUTE_HAS_BEEN_USED = "E0072";
        public static final String ROUTE_HAS_BEEN_CHANGED = "E0073";
        public static final String CHECK_MAX_LENGTH = "E0074";
        public static final String DEVICE_HAS_BEEN_UNASSIGNED = "E0075";
        public static final String CSV_ERROR = "E0080";
        public static final String DUPLICATE_LOGIN_ID_CSV = "E0081";
        public static final String MUST_GREATER_THAN = "E0076";
        public static final String FAIL_CHECK_CUSTOMER = "E0077";
        public static final String HAS_BEEN_DELETED = "E0078";
        public static final String DEVICE_NUMBER = "E0079";
        public static final String USER_INACTIVE = "E0082";
        public static final String PASS_LENGTH = "E0090";
        public static final String UPPER_CASE = "E0091";
        public static final String PASS_NUMBER = "E0092";
        public static final String PASS_SPECIAl = "E0093";
        public static final String PASS_LOWERCASE = "E0094";
        public static final String PASS_NUMERICAL = "E0095";
        public static final String PASS_WHITESPACE = "E0096";
        public static final String PASS_MAXLENGTH = "E0097";

    }

    public static class EnCryption {
        private static final int _0X45 = 0x45;
        private static final int _0X4F = 0x4f;
        private static final int _0X43 = 0x43;
        private static final int _0X41 = 0x41;
        private static final int _0X44 = 0x44;
        private static final int _0X4C = 0x4c;
        private static final int _0X49 = 0x49;
        private static final int _0X55 = 0x55;
        private static final int _0X42 = 0x42;
        private static final int _0X2A = 0x2a;
        private static final int _0X2D = 0x2d;
        public static final int RADIUS = 16;
        public static final int _0X100 = 0x100;
        public static final int _0XFF = 0xff;
        public static final String SHA_512 = "SHA-512";
        public static final String PKCS5PADDING = "AES/ECB/PKCS5Padding";
        public static final String AES = "AES";
        public static final byte[] KEY = {_0X2D, _0X2A, _0X2D, _0X42, _0X55, _0X49, _0X4C, _0X44, _0X41, _0X43, _0X4F
                , _0X44, _0X45, _0X2D, _0X2A, _0X2D};
    }

    public static class Condition {
        public static final String AND = "AND";
        public static final String OR = "OR";
    }

    public static class ColumnName {
        public static final String EMAIL = "divisionMail";
        public static final String ID = "id";
        public static final String COMPANIES_NAME = "divisionName";
        public static final String DESCRIPTION = "description";
        public static final String UPDATE_DATE = "updateDate";
        public static final String CREATE_DATE = "createDate";
        public static final String UPDATE_BY = "updateBy";
        public static final String CREATE_BY = "createBy";
        public static final String DELETE_FLG = "deleteFlg";
        public static final String USERS_ID = "usersId";

    }

    public static class UserProfile {
        public static final String USER_PROFILE_FORM = "userProfilesInfo";
        public static final String USER_PROFILE_FORM_UPDATE = "UsersController.updateUserProfilesInfo";
        public static final String USER_FORM = "users";
        public static final String USER_SYSAMIN_FORM = "systemAdminProfileForm";

    }

    public static class ConfigKey {
        public static final String TIME_CHECK_SAFETY_CONFIRM = "TIME_CHECK_SAFETY_CONFIRM";
    }
}