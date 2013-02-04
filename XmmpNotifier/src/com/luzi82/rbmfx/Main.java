package com.luzi82.rbmfx;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.luzi82.libmbgwalpurgis.ICallback;
import com.luzi82.libmbgwalpurgis.PlayerStatus;
import com.luzi82.libmbgwalpurgis.RaidBossMatchingFeed;
import com.luzi82.libmbgwalpurgis.RaidBossMatchingFeed.Unit;

public class Main {

	final IXmppMgr mXmpp;
	final MwClientMgr mMw;

	public Main(ScheduledThreadPoolExecutor aExecutor, String aMwLoginId, String aMwPassword, String aXmppLoginId, String aXmppPassword, String aTarget) {
		mXmpp = new XmppMgr(aExecutor, aXmppLoginId, aXmppPassword, aTarget);
		mMw = new MwClientMgr(aExecutor, aMwLoginId, aMwPassword);
		mMw.setUnitCallback(new ICallback<RaidBossMatchingFeed.Unit>() {
			@Override
			public void callback(Unit aResult) {
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
				mXmpp.send(aResult.toString());
			}
		});
		mMw.setExceptionListener(new ICallback<Exception>() {
			@Override
			public void callback(Exception aResult) {
				mXmpp.send(aResult.toString());
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

			Main main = new Main(new ScheduledThreadPoolExecutor(10), props.getProperty("login_id"), props.getProperty("login_pw"), props.getProperty("xmpp_from_id"), props.getProperty("xmpp_from_pw"), props.getProperty("xmpp_to_id"));
			main.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
