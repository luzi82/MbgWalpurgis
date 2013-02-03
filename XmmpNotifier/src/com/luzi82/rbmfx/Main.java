package com.luzi82.rbmfx;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.luzi82.libmbgwalpurgis.ICallback;
import com.luzi82.libmbgwalpurgis.RaidBossMatchingFeed;
import com.luzi82.libmbgwalpurgis.RaidBossMatchingFeed.Unit;

public class Main {

	final IXmppMgr mXmpp;
	final MwClientMgr mMw;

	public Main(ScheduledThreadPoolExecutor aExecutor, String aMwLoginId,
			String aMwPassword, String aXmppLoginId, String aXmppPassword,
			String aTarget) {
		mXmpp = new XmppMgr(aExecutor, aXmppLoginId, aXmppPassword, aTarget);
		mMw = new MwClientMgr(aExecutor, aMwLoginId, aMwPassword);
		mMw.setUnitCallback(new ICallback<RaidBossMatchingFeed.Unit>() {
			@Override
			public void callback(Unit aResult) {
				String msg = String.format("%s\n%s\n%s\n%s", aResult.mSide,
						aResult.mTime, aResult.mPlayer, aResult.mMessage);
				mXmpp.send(msg);
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

			Main main = new Main(new ScheduledThreadPoolExecutor(10),
					props.getProperty("login_id"),
					props.getProperty("login_pw"),
					props.getProperty("xmpp_from_id"),
					props.getProperty("xmpp_from_pw"),
					props.getProperty("xmpp_to_id"));
			main.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
