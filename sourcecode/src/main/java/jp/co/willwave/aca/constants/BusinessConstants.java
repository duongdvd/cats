package jp.co.willwave.aca.constants;

/**
 * 業務定数
 */
public class BusinessConstants {
    /**
     * 自社番号
     */
    public static final int CURRENT_HOUSE_NO = 0;

    /**
     * 送信フラグ：未配信
     */
    public static final Short SEND_FLG_UNSENT = 0;
    /**
     * 送信フラグ：モバイル返信済み
     */
    public static final Short SEND_FLG_REPLIED = 1;
    /**
     * 送信フラグ：既読
     */
    public static final Short SEND_FLG_READ = 2;

    /**
     * 応答フラグ：未応答
     */
    public static final Short RESPONSE_FLG_NONE = 0;
    /**
     * 応答フラグ：OK
     */
    public static final Short RESPONSE_FLG_OK = 1;
    /**
     * 応答フラグ：NG
     */
    public static final Short RESPONSE_FLG_NG = 2;

    /**
     * 変更フラグ：新規
     */
    public static final String STATUS_NEW_KEY = "1";
    /**
     * 変更フラグ：更新なし
     */
    public static final String STATUS_NO_CHANGE_KEY = "2";
    /**
     * 変更フラグ：更新あり
     */
    public static final String STATUS_YES_CHANGE_KEY = "3";
    /**
     * 変更フラグ：削除
     */
    public static final String STATUS_DELETE_KEY = "4";
    /**
     * 変更フラグ：取消
     */
    public static final String STATUS_CANCEL_KEY = "5";

    /**
     * ルート番号：新規[新規登録]
     */
    public static final Long ORDER_NO_NEW = 0L;

    /**
     * 日時フォーマット
     */
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";

    public static final int MAX_LENGTH_SALT = 5;

    public static final String ARRIVAL_NOTE_START = "START ROUTE";

    public static final String PASS_DEFAULT = "1A2b3c4D";

    public static final String RESET_PASSWORD_EMAIL_TITLE = "Reset password CATS";

    public static final String RESET_PASSWORD_EMAIL_BODY = "New password : ";
}
