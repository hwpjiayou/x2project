package com.renren.mobile.x2.network.talk;

import java.io.InputStream;

import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.actions.ActionDispatcher;
import com.renren.mobile.x2.core.xmpp.node.base.FakeData;
import com.renren.mobile.x2.core.xmpp.node.base.FakeTalk;
import com.renren.mobile.x2.network.talk.messagecenter.base.Utils;
import com.renren.mobile.x2.utils.log.Logger;

/**
 * @author xiaobo.yuan
 */
public class TalkServerEmulator {
    private final static String DATACONTAINER = "talk_fake_data.xml";

    private static FakeTalk data = null;

    private static Logger logger = new Logger("yxb");

    static {
        try {
            InputStream in = RenrenChatApplication.getApplication().getAssets().open(DATACONTAINER);

            data = Utils.parseStream(in, FakeTalk.class);
            logger.v("data in static = "+data.toXMLString());
        }  catch (Exception ignored) {
        }
    }

    public static void simulateServerPush(String id){
        if(data == null)
            return;
        for(FakeData fakeData : data.mFakeData){
            if(fakeData.mId.equals(id)){
                logger.v("fake data = "+fakeData.toXMLString());
                ActionDispatcher.batchDispatchAction(fakeData.messages);
            }
        }

    }
}
