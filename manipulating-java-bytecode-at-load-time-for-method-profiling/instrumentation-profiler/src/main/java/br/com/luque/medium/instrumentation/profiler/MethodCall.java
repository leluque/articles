package br.com.luque.medium.instrumentation.profiler;

import java.util.Objects;

public class MethodCall {

    private long threadId;
    private String objectId;
    private long startTimeNano;
    private long endTimeNano;

    public MethodCall(long threadId) {
        setThreadId(threadId);
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public long getStartTimeNano() {
        return startTimeNano;
    }

    public void markStartTimeNano() {
        this.startTimeNano = System.nanoTime();
    }

    public long getEndTimeNano() {
        return endTimeNano;
    }

    public void markEndTimeNano() {
        this.endTimeNano = System.nanoTime();
    }

    public long getDurationNano() {
        return endTimeNano - startTimeNano;
    }
}
