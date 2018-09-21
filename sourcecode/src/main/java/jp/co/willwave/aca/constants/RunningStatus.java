package jp.co.willwave.aca.constants;

public enum RunningStatus {
    NOT_YET_STARTED, RUNNING, FINISHED, CHANGED;

    public boolean isRunning() {
        return RunningStatus.RUNNING.equals(this);
    }
    public boolean isFinished() {
        return RunningStatus.FINISHED.equals(this);
    }
    public boolean isChanged() {
        return RunningStatus.CHANGED.equals(this);
    }
}
