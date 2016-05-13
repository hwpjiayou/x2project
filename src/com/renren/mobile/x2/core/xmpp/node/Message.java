package com.renren.mobile.x2.core.xmpp.node;

import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.base.Body;
import com.renren.mobile.x2.core.xmpp.node.childs.Action;
import com.renren.mobile.x2.core.xmpp.node.childs.Comment;
import com.renren.mobile.x2.core.xmpp.node.childs.DeletePost;
import com.renren.mobile.x2.core.xmpp.node.childs.Feed;
import com.renren.mobile.x2.core.xmpp.node.childs.Like;
import com.renren.mobile.x2.core.xmpp.node.childs.SystemMessage;

public class Message extends XMPPNode {

    private static final long serialVersionUID = 7987150540852484680L;

    /* 站在角色的角度 */
    public static enum TYPE {
        CHAT, // 普通聊天消息和状态消息
        PRIVATE_CHAT, //

        NORMAL, // 新鲜事推送
        X, // x节点消息:邀请消息
        NULL, // 非正常消息,容错处理

        WANT_BE_INVITED, // 被邀请者
        HAD_BE_INVITED, // 原群成员
        INVITE_SUCCESS, // 邀请人
        INVITE_ERROR, // 邀请失败

        GROUPCHAT, // 群聊消息
        GROUPCHAT_SEND_SUCCESS, // 群聊消息发送者
        GROUPCHAT_SEND_ERROR, // 群聊消息发送者

        UPDATE_SUBJECT, // 其他成员受到修改群名的消息
        UPDATE_SUBJECT_ERROR, UPDATE_SUBJECT_SUCCESS,

    }

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "from")
    public String mFrom = null;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "to")
    public String mTo = null;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "id")
    public String mId = null;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "type")
    public String mType = null;

    @XMLMapping(Type = XMLType.NODE, Name = "error")
    public Error mErrorNode;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "offline")
    public String mOffline = null;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "feed")
    public String mFeed = null;

    @XMLMapping(Type = XMLType.NODE, Name = "body")
    public Body mBody = null;

    @XMLMapping(Type = XMLType.NODE, Name = "action")
    public Action mAction = null;

    @XMLMapping(Type = XMLType.NODE, Name = "feed")
    public Feed mFeedNode = null;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "time")
    public String mTime = null;

    @XMLMapping(Type = XMLType.NODE, Name = "x")
    public X mXNode;

    @XMLMapping(Type = XMLType.NODE, Name = "z")
    public Z mZNode;

    @XMLMapping(Type = XMLType.NODE, Name = "subject")
    public Subject mSubjectNode;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "msgkey")
    public String mMsgKey;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "fname")
    public String mFromName;

    @XMLMapping(Type = XMLType.NODE, Name = "comment")
    public Comment mComment;
    
    @XMLMapping(Type = XMLType.NODE, Name = "like")
    public Like mLike;
    
    @XMLMapping(Type = XMLType.NODE, Name = "delete_post")
    public DeletePost mDeletePost;
    
    @XMLMapping(Type = XMLType.NODE, Name = "system")
    public SystemMessage mSystemMessaeg;

//	public TYPE getMessageType() {
//		if (mXNode != null) {
//			return TYPE.X;// 群聊协议中X节点
//		}
//		if (mSubjectNode != null) {
//			return TYPE.UPDATE_SUBJECT;// 修改群主题消息
//		}
//		if (this.mType == null) {
//			return TYPE.NULL;
//		} else if (this.mType.equals("chat")) {
//			return TYPE.CHAT;
//		} else if (this.mType.equals("personal")) {
//			return TYPE.PRIVATE_CHAT;
//		} else if (this.mType.equals("normal")) {// 新鲜事ID
//			return TYPE.NORMAL;
//		}
//		return TYPE.NULL;
//	}

    public long getFromId() {
        try {
            return Long.parseLong(this.mFrom.split("[@]")[0]);
        } catch (Exception e) {
            return -1L;
        }
    }

    public String getFromDomain() {
        if (mFrom.contains("@")) {
            return mFrom.split("[@]")[1];
        } else {
            return mFrom;
        }
    }

    public String getToDomain() {
        String result = mTo.split("[@]")[1];
        return result;
    }

    public long getToId() {
        try {
            return Long.parseLong(this.mTo.split("[@]")[0]);
        } catch (Exception e) {
            return -1L;
        }
    }

    public String getFromName() {
        return this.mFromName;
    }

    public String getMsgKey() {
        return this.mMsgKey != null ? this.mMsgKey : "";
    }

    public boolean isContainFeed() {
        boolean flag = (mFeed != null && mFeed.equals("true") && mFeedNode != null);
        return flag;
    }

    // from='roomId@muc.m.renren.com/senderId'
    public long getSendId() {
        String[] str = this.mFrom.split("[/]");
        try {
            return Long.parseLong(str[str.length - 1]);
        } catch (Exception e) {
            return -1L;
        }
    }

    public boolean isOffline() {
        try {
            return this.mOffline.toUpperCase().equals("TRUE");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSyn() {
        try {
            return this.mOffline.toUpperCase().equals("SYN");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getNodeName() {
        return "message";
    }

    @Override
    public String getId() {
        return mId;
    }

}
