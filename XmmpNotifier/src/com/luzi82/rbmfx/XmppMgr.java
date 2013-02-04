package com.luzi82.rbmfx;

import java.util.LinkedList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Pattern;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

public class XmppMgr implements IXmppMgr {

	final ScheduledThreadPoolExecutor mExecutor;
	final String mLoginId;
	final String mPassword;
	final String mTarget;
	final LinkedList<String> mMessageList = new LinkedList<String>();
	final String mServer;
	final String mUsername;

	StateOp mStateOp = new StateOffOp();

	public XmppMgr(ScheduledThreadPoolExecutor aExecutor, String aLoginId, String aPassword, String aTarget) {
		mExecutor = aExecutor;
		mLoginId = aLoginId;
		mPassword = aPassword;
		mTarget = aTarget;

		String[] loginidv = mLoginId.split(Pattern.quote("@"));
		mUsername = loginidv[0];
		mServer = loginidv[1];
	}

	public abstract class StateOp implements IXmppMgr {

		public final boolean mEnabled;

		public StateOp(boolean aEnabled) {
			mEnabled = aEnabled;
		}

		@Override
		public void send(String aMsg) {
			synchronized (mMessageList) {
				mMessageList.addLast(aMsg);
			}
		}

		@Override
		public void setEnabled(boolean aValue) {
			if (aValue == mEnabled)
				return;
			StateOp preOp = mStateOp;
			mStateOp = aValue ? (new StateOnOp()) : (new StateOffOp());
			preOp.processExit();
			mStateOp.processEnter();
		}

		@Override
		public boolean getEnabled() {
			return mEnabled;
		}

		public void processEnter() {
		}

		public void processExit() {
		}

	}

	public class StateOffOp extends StateOp {

		public StateOffOp() {
			super(false);
		}

	}

	public class StateOnOp extends StateOp {

		Connection conn = null;
		Chat chat = null;

		public StateOnOp() {
			super(true);
		}

		@Override
		public void send(String aMsg) {
			super.send(aMsg);
			startMaintain();
		}

		public synchronized void maintain() {
			if (mStateOp != this)
				return;
			try {
				if (conn == null) {
					conn = new XMPPConnection(mServer);
					conn.connect();
					conn.addConnectionListener(new CL());
					conn.login(mUsername, mPassword);
				}
				if (chat == null) {
					chat = conn.getChatManager().createChat(mTarget, new MessageListener() {
						@Override
						public void processMessage(Chat arg0, Message arg1) {
							// do nothing
						}
					});
				}
				while (true) {
					synchronized (mMessageList) {
						if (!((!mMessageList.isEmpty()) && (chat != null))) {
							break;
						}
						chat.sendMessage(mMessageList.removeFirst());
					}
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				if ((conn != null) && (conn.isConnected())) {
					conn.disconnect();
				}
				conn = null;
				chat = null;
				startMaintain();
			}
		}

		@Override
		public synchronized void processEnter() {
			startMaintain();
		}

		@Override
		public synchronized void processExit() {
			conn.disconnect();
			conn = null;
		}

		public void startMaintain() {
			mExecutor.execute(new Runnable() {
				@Override
				public void run() {
					maintain();
				}
			});
		}

		public class CL implements ConnectionListener {
			@Override
			public void connectionClosed() {
			}

			@Override
			public void connectionClosedOnError(Exception arg0) {
			}

			@Override
			public void reconnectingIn(int arg0) {
			}

			@Override
			public void reconnectionFailed(Exception arg0) {
			}

			@Override
			public void reconnectionSuccessful() {
				startMaintain();
			}
		}

	}

	@Override
	public void send(String aMsg) {
		mStateOp.send(aMsg);
	}

	@Override
	public void setEnabled(boolean aValue) {
		mStateOp.setEnabled(aValue);
	}

	@Override
	public boolean getEnabled() {
		return mStateOp.getEnabled();
	}

}
