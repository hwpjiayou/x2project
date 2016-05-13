package com.renren.mobile.x2.actions;

import com.renren.mobile.x2.actions.base.Action;
import com.renren.mobile.x2.actions.base.ActionError;
import com.renren.mobile.x2.actions.base.ActionSendSuccess;
import com.renren.mobile.x2.components.chat.net.PushSingleChat;
import com.renren.mobile.x2.components.message.action.PushMessage;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;
import com.renren.mobile.x2.utils.log.Logger;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class ActionDispatcher {

    protected static final ActionError mActionError = new ActionError();
    protected static final ActionSendSuccess mActionSuccess = new ActionSendSuccess();

    protected static final LinkedList<Action<? extends XMPPNode>> mActions = new LinkedList<Action<? extends XMPPNode>>();

    static {
        mActions.add(new PushSingleChat());
        mActions.add(new PushMessage());
    }

    protected LinkedList<Action<? extends XMPPNode>> getActionsPool(String nodeName) {
        return mActions;
    }

    private static Logger logger = new Logger("yxb");

    /*批处理业务*/
    public static <T extends XMPPNode> void batchDispatchAction(List<T> roots) {
        logger.v("come into batch dispatch actions");
        if (roots.size() == 0) {
            return;
        }
        Set<Action<? extends XMPPNode>> set = new LinkedHashSet<Action<? extends XMPPNode>>();
        List<Action<? extends XMPPNode>> list = new LinkedList<Action<? extends XMPPNode>>();
        for (XMPPNode node : roots) {
            if (checkAction(mActionError, node)) {
                mActionError.processAction((Message) node);
                break;
            }
            for (Action<? extends XMPPNode> action : mActions) {
                if ((action.getNodeClass().isInstance(node)) && checkAction(action, node)) {
                    action.addToList(node);
                    if (!set.contains(action)) {
                        list.add(action);
                    }
                    set.add(action);
                    break;
                }
            }
            if (checkAction(mActionSuccess, node)) {
                mActionSuccess.processAction(node);
            }
        }
        for (Action<? extends XMPPNode> action : list) {

            action.batchRun();
        }
    }

    @SuppressWarnings("unchecked") //已做判断
    private static <U extends XMPPNode> boolean checkAction(Action<U> action, XMPPNode node) {
        try {
            return action.checkActionType((U) node);
        } catch (Exception e) {
            return false;
        }
    }

}
