package com.renren.mobile.x2.core.xmpp.node.childs;


import com.renren.mobile.x2.core.xmpp.XMLMapping;
import com.renren.mobile.x2.core.xmpp.XMLMapping.XMLType;
import com.renren.mobile.x2.core.xmpp.node.XMPPNode;

public class Feed extends XMPPNode {

    private static final long serialVersionUID = -4364175423323947323L;

    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "id")
    public String mId = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "type")
    public String mType = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "source_id")
    public String mSouceId = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "description")
    public String mDescription = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "placename")
    public String mPlaceName = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "photo1_url_s")
    public String mPhoto1_1 = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "photo1_url_m")
    public String mPhoto1_2 = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "photo1_url_l")
    public String mPhoto1_3 = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "photo2_url_s")
    public String mPhoto2_1 = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "photo2_url_m")
    public String mPhoto2_2 = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "photo2_url_l")
    public String mPhoto2_3 = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "forward_line")
    public String mForward_Line = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "video")
    public String mVideo = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "original_name")
    public String mOriginal_Name = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "original_descrip")
    public String mOriginal_Descrip = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "summary")
    public String mSummary = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "reply_id")
    public String mReplyId = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "reply_content")
    public String mReplyContent = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "user_name")
    public String mUserName = null;
    @XMLMapping(Type = XMLType.ATTRIBUTE, Name = "isfocus")
    public String mIsFoces = null;


    public long getFeedId() {
        try {
            return Long.parseLong(mId);
        } catch (Exception e) {
            return -1;
        }
    }

    public int getFeedType() {
        try {
            return Integer.parseInt(mType);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isFocus() {
        try {
            return Boolean.parseBoolean(mIsFoces);
        } catch (Exception e) {
        }
        return false;

    }

    @Override
    public String getNodeName() {
        return "feed";
    }

}
