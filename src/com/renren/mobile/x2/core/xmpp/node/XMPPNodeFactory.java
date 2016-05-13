package com.renren.mobile.x2.core.xmpp.node;

import android.text.TextUtils;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.core.xmpp.node.base.*;
import com.renren.mobile.x2.core.xmpp.node.childs.*;

import java.util.HashMap;

import com.renren.mobile.x2.core.xmpp.node.Error;

/**
 * @author dingwei.chen1988@gmail.com
 * @说明 XMPP节点工厂
 */
public final class XMPPNodeFactory {
    private static final HashMap<String, Class<? extends XMPPNode>> XMPPNodeMap = new HashMap<String, Class<? extends XMPPNode>>();

    private static void putNodeInMap(XMPPNode node) {
        /**  @说明 ：不要添加新的XML节点类，一定要用以前就有的类，要扩展就扩展以前的类就可以了。
         * 假如写下面这行代码抛了除0异常，说明新类添加得不对。。。*/
        int hasNode = 1 / (XMPPNodeMap.containsKey(node.mTag) ? 0 : 1);
        XMPPNodeMap.put(node.mTag, node.getClass());
    }

    static {
        putNodeInMap(new Text());
        putNodeInMap(new X());
        putNodeInMap(new Item());
        putNodeInMap(new Subject());
        putNodeInMap(new Status());
        putNodeInMap(new Audio());
        putNodeInMap(new Image());
        putNodeInMap(new Invite());
        putNodeInMap(new Query());
        putNodeInMap(new Actor());
        putNodeInMap(new Destroy());
        putNodeInMap(new Check());
        putNodeInMap(new Contact());
        putNodeInMap(new Z());
        putNodeInMap(new Person());
        putNodeInMap(new Gid());
        putNodeInMap(new Profile());
        putNodeInMap(new Domain());
        putNodeInMap(new FromType());
        putNodeInMap(new FromText());
        putNodeInMap(new Name());
        putNodeInMap(new Time());
        putNodeInMap(new PictureProfileUrl());
        putNodeInMap(new Type());
        putNodeInMap(new Body());
        putNodeInMap(new Auth());
        putNodeInMap(new Error());
        putNodeInMap(new Failure());
        putNodeInMap(new Stream());
        putNodeInMap(new Success());
        putNodeInMap(new XmlNotWellFormed());
        putNodeInMap(new Message());
        putNodeInMap(new Id());
        putNodeInMap(new Feed());
        //fake data
        putNodeInMap(new FakeData());
        putNodeInMap(new FakeTalk());

        //消息相关
        putNodeInMap(new Comment());
        putNodeInMap(new Like());
        putNodeInMap(new User());
        putNodeInMap(new Voice());
        putNodeInMap(new DeletePost());
        putNodeInMap(new SystemMessage());
    }


    /*子节点生成*/
    public static XMPPNode createXMPPNode(String nodeName) {
        if (!XMPPNodeMap.containsKey(nodeName)) {
            return null;
        }
        try {
            return XMPPNodeMap.get(nodeName).newInstance();
        } catch (Exception ignored) {
        }
        return null;
    }

    public static XMPPNode obtainRootNode(String rootText, String from, String to, String id) {
        XMPPNode node = createXMPPNode(rootText);
        node.addAttribute("from", from);
        node.addAttribute("to", to);
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginInfo().mUserName)) {
            node.addAttribute("fname", LoginManager.getInstance().getLoginInfo().mUserName);
        }
        if (id != null) {
            node.addAttribute("id", id);
        }
        return node;
    }
}
