package com.renren.mobile.x2.network.talk.messagecenter.base;

/**
 * @author yang-chen
 */
public abstract class IMessage {
    private int failureTimes = 0;
    private boolean isSending = false;
    private long timeWhenAdd = -1L;

    public abstract String getContent();

    public abstract long getKey();

    public int getFailureTimes() {
        return failureTimes;
    }

    public void addFailureTimes() {
        failureTimes++;
    }

    public boolean isSending() {
        return isSending;
    }

    public void setSendingStatus(boolean isSending) {
        this.isSending = isSending;
    }

    public void setAddTime() {
        timeWhenAdd = System.currentTimeMillis();
    }

    public long getAddTime() {
        return timeWhenAdd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IMessage)) return false;
        IMessage iMessage = (IMessage) o;
        if (getKey() != iMessage.getKey()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) getKey();
        int countHashCode = getContent().hashCode();
        result = 31 * result + (int) (countHashCode ^ (countHashCode >>> 32));
        return result;
    }
}
