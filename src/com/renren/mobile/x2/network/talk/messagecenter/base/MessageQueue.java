package com.renren.mobile.x2.network.talk.messagecenter.base;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author xiaobo.yuan
 */
public class MessageQueue extends LinkedList<IMessage> {

    private static final long serialVersionUID = 4553275022788791708L;

    public synchronized boolean remove(Long key) {
        Iterator<IMessage> iter = this.iterator();
        while (iter.hasNext()) {
            IMessage msg = iter.next();
            if (msg.getKey() == key) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    public IMessage getMessageWithKey(long key) {
        for (IMessage msg : this) {
            if (msg.getKey() == key)
                return msg;
        }
        return null;
    }

    public void setSendingStatus(long key, boolean status) {
        IMessage msg = getMessageWithKey(key);
        if (msg != null)
            msg.setSendingStatus(status);
    }

    public boolean hasMessageToSend() {
        for (IMessage msg : this) {
            if (!msg.isSending())
                return true;
        }
        return false;
    }

    @Override
    public boolean add(IMessage msg) {
        msg.setAddTime();
        return super.add(msg);
    }
}
