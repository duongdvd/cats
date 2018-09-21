package jp.co.willwave.aca.common;

public class ProcessResultBean {
    /**
     * プロセスの終了コード
     */
    private final int returnCode;

    /**
     * プロセス実行時の標準出力
     */
    private final String stdoutMessage;

    /**
     * プロセス実行時のエラー出力
     */
    private final String stderrMessage;

    /**
     * コンストラクタ
     */
    public ProcessResultBean(int returnCode, String stdoutMessage, String stderrMessage) {
        super();
        this.returnCode = returnCode;
        this.stdoutMessage = stdoutMessage;
        this.stderrMessage = stderrMessage;
    }

    /**
     * beanの情報を文字列で返却する
     */
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("return code: [");
        sb.append(this.returnCode);
        sb.append("]");
        sb.append(RunProcess.LINE_SEPARATOR);

        sb.append("stdout message:");
        sb.append(RunProcess.LINE_SEPARATOR);
        sb.append(this.stdoutMessage);
        sb.append(RunProcess.LINE_SEPARATOR);

        sb.append("stderr message:");
        sb.append(RunProcess.LINE_SEPARATOR);
        sb.append(this.stderrMessage);
        sb.append(RunProcess.LINE_SEPARATOR);

        return sb.toString();
    }

    /**
     * returnCodeの取得
     */
    public int getReturnCode() {
        return this.returnCode;
    }

    /**
     * stdoutMessageの取得
     */
    public String getStdoutMessage() {
        return this.stdoutMessage;
    }

    /**
     * stderrMessageの取得
     */
    public String getStderrMessage() {
        return this.stderrMessage;
    }
}
