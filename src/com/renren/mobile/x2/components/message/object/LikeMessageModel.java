package com.renren.mobile.x2.components.message.object;

import com.renren.mobile.x2.core.orm.ORM;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.db.table.MessageColumn;

public class LikeMessageModel extends BaseMessageModel {

    /**
     * 新鲜事ID
     */
    @ORM(mappingColumn=MessageColumn.FEED_ID)
    public long feedId;
    
    /**
     * @return the feedId
     */
    public long getFeedId() {
        return feedId;
    }

    /**
     * @param feedId the feedId to set
     */
    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    @Override
    public void swapDataFromXML(Message message) {
        this.content = message.mLike.mText.mValue;
        this.time = getTime(message.mLike.mTime);
    }


}
