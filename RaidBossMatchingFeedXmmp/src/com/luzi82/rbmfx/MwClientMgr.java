package com.luzi82.rbmfx;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.luzi82.libmbgwalpurgis.ICallback;
import com.luzi82.libmbgwalpurgis.IMwClient;
import com.luzi82.libmbgwalpurgis.IMwClient.State;
import com.luzi82.libmbgwalpurgis.RaidBossMatchingFeed.Unit;
import com.luzi82.libmbgwalpurgis.MwClient;
import com.luzi82.libmbgwalpurgis.RaidBossMatchingFeed;

public class MwClientMgr {

	final ScheduledThreadPoolExecutor mExecutor;
	final IMwClient mMwClient;
	final String mLoginId;
	final String mPassword;
	final LinkedList<RaidBossMatchingFeed.Unit> mHistory = new LinkedList<RaidBossMatchingFeed.Unit>();

	public MwClientMgr(ScheduledThreadPoolExecutor aExecutor, String aLoginId,
			String aPassword) {
		mExecutor = aExecutor;
		mMwClient = new MwClient(mExecutor);
		mLoginId = aLoginId;
		mPassword = aPassword;
	}

	public void start() {
		mExecutor.scheduleAtFixedRate(new MaintainRunnable(), 0, 15,
				TimeUnit.SECONDS);
	}

	public synchronized void maintain() {
		IMwClient.State state = mMwClient.getState();
		if (state == State.OFFLINE) {
			mMwClient.connect(mLoginId, mPassword,
					new MaintainCallback<Void>(), new ExceptionCallback());
		} else if (state == State.ONLINE) {
			mMwClient.getFeed(new ICallback<RaidBossMatchingFeed>() {
				@Override
				public void callback(RaidBossMatchingFeed aResult) {
					Iterator<RaidBossMatchingFeed.Unit> itr = aResult.mUnitList
							.descendingIterator();
					while (itr.hasNext()) {
						RaidBossMatchingFeed.Unit u = itr.next();
						if (!mHistory.contains(u)) {
							if (mUnitCallback != null) {
								mUnitCallback.callback(u);
							}
							mHistory.addFirst(u);
						}
						while (mHistory.size() > 10) {
							mHistory.removeLast();
						}
					}
				}
			}, new ExceptionCallback());
		}
	}

	ICallback<RaidBossMatchingFeed.Unit> mUnitCallback;

	public void setUnitCallback(ICallback<RaidBossMatchingFeed.Unit> aCallback) {
		mUnitCallback = aCallback;
	}

	public class MaintainRunnable implements Runnable {
		@Override
		public void run() {
			maintain();
		}
	}

	public class MaintainCallback<T> implements ICallback<T> {
		@Override
		public void callback(T aResult) {
			maintain();
		}
	}

	public class ExceptionCallback implements ICallback<Exception> {
		@Override
		public void callback(Exception aResult) {
			aResult.printStackTrace();
			maintain();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Properties props = new Properties();
			FileInputStream authFileIn = new FileInputStream("auth.properties");
			props.load(authFileIn);
			authFileIn.close();

			MwClientMgr main = new MwClientMgr(new ScheduledThreadPoolExecutor(
					10), props.getProperty("login_id"),
					props.getProperty("login_pw"));
			main.setUnitCallback(new ICallback<RaidBossMatchingFeed.Unit>() {
				@Override
				public void callback(Unit aResult) {
					aResult.trace();
				}
			});
			main.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
