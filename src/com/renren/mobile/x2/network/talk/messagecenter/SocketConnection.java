package com.renren.mobile.x2.network.talk.messagecenter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.text.TextUtils;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.core.xmpp.node.Message;
import com.renren.mobile.x2.core.xmpp.node.base.Stream;
import com.renren.mobile.x2.network.talk.messagecenter.base.Connection;
import com.renren.mobile.x2.network.talk.messagecenter.base.ConnectionArgs;
import com.renren.mobile.x2.network.talk.messagecenter.base.IMessage;
import com.renren.mobile.x2.network.talk.messagecenter.base.XMLParser;
import com.renren.mobile.x2.utils.Config;
import com.renren.mobile.x2.utils.Md5;
import com.renren.mobile.x2.utils.SystemService;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import static com.renren.mobile.x2.network.talk.messagecenter.base.ConnectionArgs.SocketArgs;
import static com.renren.mobile.x2.network.talk.messagecenter.base.Utils.addItemWithNotify;
import static com.renren.mobile.x2.utils.log.Logger.l;
import static com.renren.mobile.x2.utils.log.Logger.net;

/**
 * Socket连接过程
 *
 * @author yang-chen
 */
public class SocketConnection extends Connection {
    private static final int BUILD_STATUS = 0; // 发起Socket连接
    private static final int AUTH_STATUS = 1; // 发起服务器认证
    private static final int RESP_STATUS = 2; // 回应认证
    private static final int COMM_STATUS = 3; // 通信状态
    private static final int CLOSE_STATUS = 4; // 关闭连接
    private static final int[] mNextState = new int[]{
            AUTH_STATUS, // ->BUILD_STATUS
            RESP_STATUS, // ->AUTH_STATUS
            COMM_STATUS, // ->RESP_STATUS
            COMM_STATUS, // still in Comm State
            BUILD_STATUS, // ->Close
            CLOSE_STATUS};
    private static final String kActionName = sContext.getPackageName() + ".keepconnection";
    private static final PendingIntent kPendingIntent = PendingIntent.getBroadcast(sContext, 0, new Intent(kActionName), 0);
    private static final IntentFilter filter = new IntentFilter(kActionName);
    private Socket mSocket = null;
    private Writer mOutput = null;
    private BufferedInputStream mBufferedInputStream = null;
    private SocketParser mParser;
    private int mCurrentState;
    private KeepConnectionReciver mKeepConnectionReciver = null;
    private SocketArgs socketArgs;

    public SocketConnection(ConnectionManager cm, ConnectionArgs args) {
        super(cm);
        mType = SOCKET;
        mKeepConnectionReciver = new KeepConnectionReciver();
        mParser = new SocketParser();
        socketArgs = args.mSocketArgs;
    }

    @Override
    protected void onBeginConnection() {
        mCurrentState = BUILD_STATUS;
        try {
            l("=======================new Socket(" + Config.SOCKET_URL
                    + ") begin=====================");
            mSocket = new Socket(Config.SOCKET_URL, Config.SOCKET_DEFAULT_PORT);
            mSocket.setSoTimeout(socketArgs.getValue(SocketArgs.TIMEOUT_TIME));
            mSocket.setKeepAlive(true);
            l("=======================new Socket finish=====================");
            mOutput = new OutputStreamWriter(mSocket.getOutputStream());
            mBufferedInputStream = new BufferedInputStream(
                    mSocket.getInputStream()) {

                @Override
                public synchronized int read(byte[] buffer, int offset,
                                             int byteCount) throws IOException {
                    int len = super.read(buffer, offset, byteCount);
                    if (len < 0) {
                        net("socket recv:" + len);
                    } else {
                        String readStr = new String(buffer, offset, len);
                        net("recv:|" + readStr + "|");
                        if (readStr.trim().length() == 0) {
                            return 0;
                        }
                    }
                    return len;
                }
            };
            safeWriteString(getNodeStr(BUILD_NODE,
                    RenrenChatApplication.getAppID(),
                    RenrenChatApplication.getFrom(),
                    RenrenChatApplication.getVersionName()));// 建立连接
            mCurrentState = AUTH_STATUS;
            mParser.parse(mBufferedInputStream);
        } catch (Exception e) {
            onConnectionLost(e);
        } finally {
            try {
                mOutput.close();
                mBufferedInputStream.close();
            } catch (Exception ignored) {
            }
        }
        l("stop thread!!!!!!!!!!");
    }

    @Override
    protected synchronized void onSendMessageToNet() {
        for (IMessage msg : sMessageTasks) {
            if (msg.isSending()) {
                continue;
            }
            try {
                safeWriteString(msg.getContent());
                msg.setSendingStatus(true);
                addItemWithNotify(sMessageTimeoutCheckQueue, msg);
            } catch (IOException e) {
                msg.addFailureTimes();
                onConnectionLost(e);
                break;
            }
        }
    }

    private final class SocketParser extends XMLParser<Stream> {

        public SocketParser() {
            super(Stream.class);
        }

        @Override
        protected void onStackSizeChanged(int size) {
            if (size == 0) {
                mCurrentState = CLOSE_STATUS;
                onConnectionLost(new Exception("server closed parser"));
                return;
            }
            if (size != 1) {
                return;
            }
            final Stream stream = getRoot();
            if (stream == null) {
                onConnectionLost(new LoginErrorException(BUILD_NODE));
                return;
            }
            if (stream.success != null && !sIsReady.get()) {
                onConnectReady();
                beginKeepConnection();
            } else if (sIsReady.get()) {
                StringBuilder sb = new StringBuilder();
                for (Message e : stream.messages) {
                    if (e.mMsgKey != null) {
                        sb.append("<ack msgkey=\"")
                                .append(e.mMsgKey)
                                .append("\"/>");
                    }
                }
                try {
                    safeWriteString(sb.toString());
                } catch (IOException e) {
                    onConnectionLost(e);
                }
                onRecvMessage(stream.messages);
            } else {
                try {
                    if (!stream.errors.isEmpty()) {
                        l("error in login Talk server..........");
                        throw new LoginErrorException(mCurrentState, ConnectionException.GET_ERROR_DATA);
                    }
                    String msg = null;
                    switch (mCurrentState) {
                        case AUTH_STATUS:
                            msg = getNodeStr(AUTH_NODE, LoginManager.getInstance().getLoginInfo().mUserId);
                            break;
                        case RESP_STATUS:
                            if (stream.auth == null) {
                                throw new LoginErrorException(RESP_STATUS);
                            }
                            String authPrefix = stream.auth.mValue;
                            String secretkey = LoginManager.getInstance().getSecretKey();
                            l("auth:%s, secretkey:%s", authPrefix, secretkey);
                            msg = getNodeStr(RESPONSE_NODE, Md5.toMD5(authPrefix + secretkey));
                            break;
                    }
                    safeWriteString(msg);
                    mCurrentState = mNextState[mCurrentState];
                } catch (LoginErrorException e) {
                    loginErrorNotification(e);
                } catch (Exception e) {
                    onConnectionLost(e);
                }
            }
            stream.clear();
        }
    }

    private synchronized void safeWriteString(String str) throws IOException {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        net("send:|" + str + "|");
        mOutput.write(str);
        mOutput.flush();
    }

    public synchronized void beginKeepConnection() {
        SystemService.sAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), socketArgs.getValue(SocketArgs.POLL_SPACE_TIME), kPendingIntent);
        try {
            sContext.registerReceiver(mKeepConnectionReciver, filter);
        } catch (Exception ignored) {
        }
        // Timer + WakeLock的心跳包实现
//		wl = SystemService.sPowerManager.newWakeLock(
//				PowerManager.PARTIAL_WAKE_LOCK, "keep connection");
//		timer = new Timer();
//		timer.schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//				Utils.log("timer is schedule sIsReady.get():" + sIsReady.get());
//				if (sIsReady.get()) {
//					try {
//						wl.acquire();
//						safeWriteString(" ");
//					} catch (IOException e) {
//						e.printStackTrace();
//						onConnectionLost("error in KeepConnectionReciver");
//					} finally {
//						wl.release();
//					}
//				}
//			}
//		}, 5000, 15000);
    }

//	private PowerManager.WakeLock wl;
//	private Timer timer;
//
//	private Handler mHandler;
//	private class KeepConnectionHandler implements Runnable {
//		public voi	d run() {
//			if (sIsReady.get()) {
//				try {
//					safeWriteString(" ");
//				} catch (IOException e) {
//					e.printStackTrace();
//					onConnectionLost("error in KeepConnectionReciver");
//				}
//				mHandler.postDelayed(this, 15 * 1000);
//			}
//		}
//	}

    public synchronized void endKeepConnection() {
        try {
            SystemService.sAlarmManager.cancel(kPendingIntent);
            if (mKeepConnectionReciver != null) {
                sContext.unregisterReceiver(mKeepConnectionReciver);
            }
        } catch (Exception ignored) {
        }
//		if (timer != null) {
//			timer.purge();
//			timer.cancel();
//		}
    }

    public class KeepConnectionReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (sIsReady.get()) {
                try {
                    safeWriteString(" ");
                } catch (IOException e) {
                    onConnectionLost(e);
                }
            } else {
                endKeepConnection();
            }
        }
    }

    @Override
    public synchronized void onDisconnect(boolean isLogout) {
        l("===============socket onDisconnect");
        mStatus = DISCONNECTED;
        sIsReady.set(false);
        endKeepConnection();
        if (mOutput == null) {
            return;
        }
//		if (isLogout) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					synchronized (mOutput) {
//						try {
//							Utils.l("close socket start & send CLOSE_MSG");
//							mOutput.write(CLOSE_MSG);
//							mOutput.flush();
//							mSocket.shutdownInput();
//							mOutput.close();
//							mSocket.close();
//							Utils.l("close socket end");
//						} catch (Exception ignored) {
//						}
//					}
//				}
//			}).start();
//		}
        try {
            l("mOutput.close();");
            mOutput.close();
            mSocket.shutdownInput();
            mSocket.close();
        } catch (IOException ignored) {
        }
    }

    private static final int AUTH_NODE = 0;
    private static final int BUILD_NODE = 1;
    private static final int CLOSE_NODE = 2;
    private static final int RESPONSE_NODE = 3;

    @Override
    protected String getNodeStr(int nodeType, Object... args) {
        switch (nodeType) {
            case AUTH_NODE:
                return String
                        .format("<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='MAS_SECRET_KEY'>%s</auth>",
                                args);
            case BUILD_NODE:
                return String.format("<stream:stream online_deploy='false' "
                        + sCommonBuildString + ">", args);
            case CLOSE_NODE:
                return "</stream:stream>";
            case RESPONSE_NODE:
                return String.format("<response>%s</response>", args);
            default:
                return null;
        }
    }
}
