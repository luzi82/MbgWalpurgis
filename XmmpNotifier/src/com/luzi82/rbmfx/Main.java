package com.luzi82.rbmfx;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.luzi82.libmbgwalpurgis.ICallback;
import com.luzi82.libmbgwalpurgis.PlayerStatus;
import com.luzi82.libmbgwalpurgis.RaidBossMatchingFeed;
import com.luzi82.libmbgwalpurgis.RaidBossMatchingFeed.Unit;
import com.luzi82.nagatoquery.NagatoQuery;
import com.luzi82.nagatoquery.NqSession;
import com.luzi82.nagatoquery.NqSession.CommandListener;
import com.luzi82.nagatoquery.UtilCommand;

public class Main {

	final IXmppMgr mXmpp;
	final MwClientMgr mMw;
	final NagatoQuery mNagatoQuery;
	final NqSession mNqSession;
	public static Main mMain;
	boolean mMsgEnabled = true;

	public Main(ScheduledThreadPoolExecutor aExecutor, String aMwLoginId, String aMwPassword, String aXmppLoginId, String aXmppPassword, String aTarget) {
		mXmpp = new XmppMgr(aExecutor, aXmppLoginId, aXmppPassword, aTarget);
		mMw = new MwClientMgr(aExecutor, aMwLoginId, aMwPassword);

		mNagatoQuery = new NagatoQuery(aExecutor);
		mNagatoQuery.loadClass(UtilCommand.class);
		mNagatoQuery.loadClass(NqSession.class);
		mNagatoQuery.loadClass(MainCmd.class);
		mNqSession = new NqSession(mNagatoQuery);

		mMw.setUnitCallback(new ICallback<RaidBossMatchingFeed.Unit>() {
			@Override
			public void callback(Unit aResult) {
				if (!mMsgEnabled)
					return;
				String msg = String.format("%s\n%s: %s\n%s\n", aResult.mTime, aResult.mSide, aResult.mPlayer, aResult.mMessage);
				PlayerStatus ps = mMw.getLastPlayerStatus();
				if (ps != null) {
					msg += ps.toString();
				}
				mXmpp.send(msg);
			}
		});
		mMw.setPlayerStatusCallback(new ICallback<PlayerStatus>() {
			@Override
			public void callback(PlayerStatus aResult) {
				if (!mMsgEnabled)
					return;
				StringBuffer sb = new StringBuffer();
				sb.append(aResult.toString());
				if (mMw.mLastFullLp)
					sb.append("\n* FULL LP");
				if (mMw.mLastFullBp)
					sb.append("\n* FULL BP");
				if (mMw.mLastExpUp)
					sb.append("\n* EXP UP");
				if (mMw.mLastFullCard)
					sb.append("\n* FULL CARD");
				mXmpp.send(sb.toString());
			}
		});
		mMw.setExceptionListener(new ICallback<Exception>() {
			@Override
			public void callback(Exception aResult) {
				mXmpp.send(aResult.toString());
			}
		});
		mXmpp.setMessageCallback(new ICallback<String>() {
			@Override
			public void callback(String aResult) {
				mNqSession.execute(aResult, new CommandListener() {
					@Override
					public void commandTrace(String aMessage) {
						mXmpp.send(aMessage);
					}

					@Override
					public void commandReturn(String aResult) {
						mXmpp.send(aResult);
					}

					@Override
					public void commandError(String aError) {
						mXmpp.send(aError);
					}
				});
			}
		});
	}

	public void start() {
		mXmpp.setEnabled(true);
		mMw.start();
		mXmpp.send("Start");
	}

	public static void main(String[] argv) {
		try {
			Properties props = new Properties();
			FileInputStream authFileIn = new FileInputStream("auth.properties");
			props.load(authFileIn);
			authFileIn.close();

			mMain = new Main(new ScheduledThreadPoolExecutor(10), props.getProperty("login_id"), props.getProperty("login_pw"), props.getProperty("xmpp_from_id"), props.getProperty("xmpp_from_pw"), props.getProperty("xmpp_to_id"));
			mMain.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
