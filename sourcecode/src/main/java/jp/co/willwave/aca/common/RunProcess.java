package jp.co.willwave.aca.common;

import java.io.IOException;

public class RunProcess {
    /**
     * 改行コード
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * コマンド
     */
    private final String cmd;

    /**
     * コンストラクタ
     */
    public RunProcess(String cmd) {
        this.cmd = cmd;
    }

    /**
     * プロセスの実行を行う
     */
    public ProcessResultBean execute() throws IOException, InterruptedException {
        // プロセスを開始する
        ProcessBuilder pb = new ProcessBuilder();
        String[] cmd = {"bash", "-c", this.cmd};
        pb.command(cmd);
        Process p = pb.start();

        // 標準出力、エラー出力を取得するスレッドを動作させる
        ProcessStreamReadThread stdoutThread = new ProcessStreamReadThread(p.getInputStream());
        stdoutThread.start();
        ProcessStreamReadThread stderrThread = new ProcessStreamReadThread(p.getErrorStream());
        stderrThread.start();

        // プロセスの終了を待つ
        int ret = p.waitFor();

        // スレッドの終了を待つ
        stdoutThread.join();
        stderrThread.join();

        // 終了状態を生成し、戻す
        ProcessResultBean retBean = new ProcessResultBean(ret, stdoutThread.getStreamMessage(),
                stderrThread.getStreamMessage());
        return retBean;
    }
}
