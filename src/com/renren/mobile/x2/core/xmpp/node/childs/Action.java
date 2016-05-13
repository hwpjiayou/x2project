package com.renren.mobile.x2.core.xmpp.node.childs;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;

/**
 * Message_Action
 * @author : xiaoguang.zhang
 * Date: 12-8-13
 * Time: 下午7:44
 * @说明 message下action子节点
 */
public class Action extends XMPPNode {

	private static final long serialVersionUID = 361949225743047483L;
	
	@XMLMapping(Type= XMLType.ATTRIBUTE,Name="type")
    public String mType;

    @Override
    public String getNodeName() {
        return "action";
    }

    public String getType(){
        if(mType!=null){
            return mType;
        }
        return "unknow";
    }

    public Action(){}

    public Action(String type){
        this.addAttribute("type", type);
    }
}
