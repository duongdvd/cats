package jp.co.willwave.aca.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessStreamReadThread extends Thread {
    private final BufferedReader br;

    private final List<String> streamMessageList = new ArrayList<String>();

    public ProcessStreamReadThread(InputStream is) {
        br = new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public void run() {
        try {
            this.readStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readStream() throws IOException {
        try {
            while (true) {
                String line = br.readLine();
                if (null == line) {
                    break;
                }
                this.streamMessageList.add(line);
            }
        } finally {
            br.close();
        }
    }

    public List<String> getStreamMessageList() {
        return this.streamMessageList;
    }

    public String getStreamMessage() {
        int cnt = 0;
        StringBuilder sb = new StringBuilder();
        for (String streamMessage : streamMessageList) {
            sb.append(streamMessage);
            if (this.streamMessageList.size() != cnt) {
                sb.append(RunProcess.LINE_SEPARATOR);
            }
            cnt++;
        }
        return sb.toString();
    }
}
