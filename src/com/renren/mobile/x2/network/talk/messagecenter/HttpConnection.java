package com.renren.mobile.x2.network.talk.messagecenter;

import android.text.TextUtils;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.core.xmpp.node.base.Body;
import com.renren.mobile.x2.network.talk.binder.RemoteServiceBinder;
import com.renren.mobile.x2.network.talk.messagecenter.base.Connection;
import com.renren.mobile.x2.network.talk.messagecenter.base.ConnectionArgs;
import com.renren.mobile.x2.network.talk.messagecenter.base.IMessage;
import com.renren.mobile.x2.network.talk.messagecenter.base.Utils;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Config;
import com.renren.mobile.x2.utils.Md5;
import com.renren.mobile.x2.utils.log.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.renren.mobile.x2.network.talk.messagecenter.base.Utils.addItemWithNotify;

/**
 * Http连接过程
 *
 * @author yang-chen
 */
public class HttpConnection extends Connection {
    public static final int POLL = 1;
    public static final int CHAT = 2;
    private static final int AUTH_NODE = 1;
    private static final int BUILD_NODE = 2;
    private static final int POLL_NODE = 3;
    private static final int CLOSE_NODE = 4;

    private final AtomicBoolean mIsQuit = new AtomicBoolean(false);
    private int mRid;
    protected String mSid = "";
    protected HttpClient mHttpClient;
    private Timer mTimer = new Timer();
    private int mPollWaitTime = -1;
    private ConnectionArgs.HttpArgs httpArgs;

    public HttpConnection(ConnectionManager cm, ConnectionArgs args) {
        super(cm);
        mType = HTTP;
        httpArgs = args.mHttpArgs;
        mHttpClient = Utils.createHttpClient(httpArgs.getValue(ConnectionArgs.HttpArgs.CLIENT_SO_TIME));
        System.setProperty("http.keepAlive", "true");
    }

    private HttpPost getPost(String str, int type)
            throws UnsupportedEncodingException {
        final HttpPost post;
        switch (type) {
            case CHAT:
                post = new HttpPost(Config.HTTP_SEND_URL);
                break;
            case POLL:
                ++mRid;
                post = new HttpPost(Config.HTTP_TALK_URL);
                break;
            default:
                post = new HttpPost(Config.HTTP_TALK_URL);
        }
        post.addHeader("Connection", "keep-alive");
        post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Host", Config.HOST_NAME);
        post.addHeader("User-Agent", "Mozilla/5.0");
        post.addHeader("Accept-Language", "zh_CN,zh");
        post.addHeader("Accept-Charset", "utf-8");
        post.addHeader("Accept-Encoding", "utf-8");
        post.removeHeaders(org.apache.http.protocol.HTTP.EXPECT_DIRECTIVE);
        if (Config.IS_ADD_XONLINEHOST) {
            post.addHeader("X-Online-Host", Config.HOST_NAME);
        }
        post.setEntity(new StringEntity(str, "utf-8"));
        return post;
    }

    protected final Body sendText(final String str, final int type)
            throws Exception {
        Logger.net("send:|" + str + "|");
        int tryCount = 0;
        HttpClient client = mHttpClient;
        if (type == CHAT) {
            client = Utils.createHttpClient(httpArgs.getValue(ConnectionArgs.HttpArgs.CLIENT_SO_TIME));
        }
        final HttpPost post = getPost(str, type);
        while (tryCount++ < 2) { // 最多尝试2次，否则认为网络失败，重新连接
            final long time1 = System.currentTimeMillis();
            final HttpResponse httpResponse = client.execute(post);
//			Header[] headers = httpResponse.getAllHeaders();
//			StringBuffer sb = new StringBuffer();
//			for(Header header : headers){
//				sb.append("[").append(header.getName()).append(":").append(header.getValue()).append("]\n");
//			}
//			Logger.net("resp header:\n" + sb.toString());
            final String response = EntityUtils.toString(httpResponse.getEntity());

            Logger.net(String.format("recv:(%6d)|%s|", System.currentTimeMillis() - time1, response));
            // Wap网关返回不正常字符串的时候
            if (!response.contains("<") || response.trim().startsWith("<html>")) {
                CommonUtil.waitTimeWithoutInterrupt(RemoteServiceBinder.sIsForeGround ? 5000 : 10000);
                Logger.l("recv some other msg");
                continue;
            }
//			if ("wml".equals(element.tag) || "html".equals(element.tag)) { // 网关返回wml或者html网页
//			}	sb.append("[").append(header.getName()).a
            final Body body = Utils.parseString(response, Body.class);
            if (body == null) { // 收不到body节点
                CommonUtil.waitTimeWithoutInterrupt(RemoteServiceBinder.sIsForeGround ? 5000 : 10000);
                Logger.l("recv msg form wap gate");
                continue;
            }
            return body;
        }
        return null;
    }

    @Override
    protected void onBeginConnection() {
        mRid = 1000000 + (int) (Math.random() * 1000000);
        try {
            Body body = sendText(getNodeStr(
                    BUILD_NODE, LoginManager.getInstance().getLoginInfo().mUserId,
                    mRid,
                    httpArgs.getValue(ConnectionArgs.HttpArgs.WAITTIME)), POLL);
            if (body == null || body.auth == null) {
                throw new LoginErrorException(BUILD_NODE, ConnectionException.GET_EMPTY_DATA);
            }

            if (!TextUtils.isEmpty(body.inactivity) && TextUtils.isDigitsOnly(body.inactivity)) {
                try {
                    Logger.l("inactivity=%s", body.inactivity);
                    mPollWaitTime = Integer.parseInt(body.inactivity);
                } catch (Exception ignored) {
                }
            }

            String authStr = body.auth.mValue;
            Logger.l("auth str:%s", authStr);
            mSid = body.sid;
            body = sendText(getNodeStr(AUTH_NODE,
                    mSid,
                    mRid,
                    Md5.toMD5(authStr + LoginManager.getInstance().getSecretKey())),
                    POLL);
            if (body.success == null) {
                throw new LoginErrorException(AUTH_NODE, ConnectionException.GET_ERROR_DATA);
            }
            onConnectReady();
            mIsQuit.set(false);
            startPoll();
        } catch (LoginErrorException e) {
            loginErrorNotification(e);
        } catch (Exception e) {
            onConnectionLost(e);
        }
    }

    @Override
    protected synchronized void onSendMessageToNet() {
        final StringBuilder buffer = new StringBuilder();

        for (IMessage msg : sMessageTasks) {
            if (!msg.isSending())
                buffer.append(msg.getContent());
        }

        try {
            final Body body = sendText(
                    "<body sid=\"" + mSid + "\">" + buffer.toString()
                            + "</body>", CHAT);
            for (IMessage msg : sMessageTasks) {
                msg.setSendingStatus(true);
                addItemWithNotify(sMessageTimeoutCheckQueue, msg);
            }
            onRecvMessage(body.mChilds);
        } catch (Exception e) {
            for (IMessage msg : sMessageTasks) {
                if (!msg.isSending())
                    msg.addFailureTimes();
            }
            onConnectionLost(e);
        }
    }

    private void startPoll() {
        while (!mIsQuit.get()) {
            waitForReady();
            int waitTime = httpArgs.getValue(ConnectionArgs.HttpArgs.POLL_SPACE_TIME);
            Logger.l("poll wait time:%d", waitTime);
            if (waitTime > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ignored) {
                    Logger.l("on interrput");
                }
            }
            try {
                Body body = sendText(getNodeStr(POLL_NODE, mSid, mRid), POLL);
                if (body == null) {
                    throw new ConnectionException(ConnectionException.GET_ERROR_DATA);
                } else if ("terminate".equals(body.type)) { // 获得Terminate节点
                    throw new ConnectionException(ConnectionException.GET_TERMINATE_NODE);
                } else {
                    onRecvMessage(body.mChilds);
                }
            } catch (Exception e) {
                onConnectionLost(e);
            }
        }
    }

    @Override
    public void onChangeAppGround(final boolean isAppForegound) {
        Logger.l("in connection : " + isAppForegound);
        if (isAppForegound) {
            httpArgs.setValue(ConnectionArgs.HttpArgs.POLL_SPACE_TIME, -1);
            interrupt();
        } else {
            mTimer.purge();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    httpArgs.setValue(ConnectionArgs.HttpArgs.POLL_SPACE_TIME, mPollWaitTime * 1000);
                }
            }, httpArgs.getValue(ConnectionArgs.HttpArgs.CHANGE_APP_WAIT_TIME));
        }
    }

    @Override
    public void onDisconnect(boolean isLogout) {
        Logger.l("===============http onDisconnect");
        mIsQuit.set(true);
        sIsReady.set(false);
        mStatus = DISCONNECTED;
        if (isLogout) {
            new Thread() {
                @Override
                public void run() {
                    String str = getNodeStr(CLOSE_NODE, mSid, mRid);
                    Logger.net("send:|" + str + "|");
                    HttpClient client = Utils.createHttpClient();
                    try {
                        if (!sIsReady.get())
                            client.execute(getPost(str, POLL));
                    } catch (Exception ignored) {
                    }
                }
            }.start();
        }
    }

    @Override
    protected String getNodeStr(int nodeType, Object... args) {
        switch (nodeType) {
            case AUTH_NODE:
                return String
                        .format("<body sid='%s' rid='%s' to='talk.sixin.com' from='talk.sixin.com' xmlns='http://jabber.org/protocol/httpbind'>"
                                + "<response mechanism='MAS_SECRET_KEY'>%s</response>"
                                + "</body>", args);
            case BUILD_NODE:
                return String.format(
                        "<body from='%s@talk.sixin.com' hold='1' rid='%s'"
                                + sCommonBuildString + " wait='%d' />", args);
            case POLL_NODE:
                return String.format(
                        "<body sid='%s' rid='%s'  to='talk.sixin.com'/>", args);
            case CLOSE_NODE:
                return String
                        .format("<body sid='%s' rid='%s'><presence type='unavailable'></presence></body>",
                                args);
            default:
                return null;
        }
    }
}
