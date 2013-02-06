package com.luzi82.libmbgwalpurgis;

import org.junit.Assert;
import org.junit.Test;

import com.luzi82.libmbgwalpurgis.IMwClient;

public abstract class AbstractMwClientTestX {

	@Test
	public void testConnect() {
		// newMmClient()
		IMwClient client = newMmClient();
		CallCount<IMwClient.State> stateChange = new CallCount<IMwClient.State>();
		client.setStateChangeCallback(stateChange);

		// IMmClient.connect
		CallWait<Void> connectWait = new CallWait<Void>();
		client.connect(getLoginId(), getPassword(), connectWait.newCallback(), connectWait.newExceptionHandler());
		Assert.assertEquals(IMwClient.State.CONNECTING, stateChange.waitDone(1));
		Assert.assertEquals(IMwClient.State.ONLINE, stateChange.waitDone(2));
		Assert.assertTrue(connectWait.waitDone());
		Assert.assertEquals(IMwClient.State.ONLINE, client.getState());
	}

	@Test
	public void testDisconnect() {
		// newMmClient()
		IMwClient client = newMmClient();
		CallCount<IMwClient.State> stateChange = new CallCount<IMwClient.State>();
		client.setStateChangeCallback(stateChange);

		// IMmClient.connect
		CallWait<Void> connectWait = new CallWait<Void>();
		client.connect(getLoginId(), getPassword(), connectWait.newCallback(), connectWait.newExceptionHandler());
		Assert.assertTrue(connectWait.waitDone());
		Assert.assertEquals(IMwClient.State.ONLINE, client.getState());

		stateChange.clear();

		// IMmClient.disconnect
		CallWait<Void> disConnectWait = new CallWait<Void>();
		client.disconnect(disConnectWait.newCallback());
		Assert.assertEquals(IMwClient.State.DISCONNECING, stateChange.waitDone(1));
		Assert.assertEquals(IMwClient.State.OFFLINE, stateChange.waitDone(2));
		Assert.assertTrue(disConnectWait.waitDone());
		Assert.assertEquals(IMwClient.State.OFFLINE, client.getState());
	}

	@Test
	public void testGetFeed() {
		// newMmClient()
		IMwClient client = newMmClient();
		CallCount<IMwClient.State> stateChange = new CallCount<IMwClient.State>();
		client.setStateChangeCallback(stateChange);

		// IMmClient.connect
		CallWait<Void> connectWait = new CallWait<Void>();
		client.connect(getLoginId(), getPassword(), connectWait.newCallback(), connectWait.newExceptionHandler());
		Assert.assertTrue(connectWait.waitDone());
		Assert.assertEquals(IMwClient.State.ONLINE, client.getState());

		// IMmClient.getFeed
		CallWait<RaidBossMatchingFeed> feedWait = new CallWait<RaidBossMatchingFeed>();
		client.getFeed(feedWait.newCallback(), feedWait.newExceptionHandler());
		Assert.assertTrue(feedWait.waitDone());
		RaidBossMatchingFeed feed = feedWait.mCallbackReturn;
		if (feed.mUnitList.size() >= 1) {
			RaidBossMatchingFeed.Unit u = feed.mUnitList.get(0);
			System.out.println(u.mSide);
			System.out.println(u.mTime);
			System.out.println(u.mPlayer);
			System.out.println(u.mMessage);
		}
	}

	@Test
	public void testGetStatus() {
		// newMmClient()
		IMwClient client = newMmClient();
		CallCount<IMwClient.State> stateChange = new CallCount<IMwClient.State>();
		client.setStateChangeCallback(stateChange);

		// IMmClient.connect
		CallWait<Void> connectWait = new CallWait<Void>();
		client.connect(getLoginId(), getPassword(), connectWait.newCallback(), connectWait.newExceptionHandler());
		Assert.assertTrue(connectWait.waitDone());
		Assert.assertEquals(IMwClient.State.ONLINE, client.getState());

		// IMmClient.getFeed
		CallWait<PlayerStatus> statusWait = new CallWait<PlayerStatus>();
		client.getStatus(statusWait.newCallback(), statusWait.newExceptionHandler());
		Assert.assertTrue(statusWait.waitDone());
		PlayerStatus status = statusWait.mCallbackReturn;
		System.out.println(status.toString());
	}

	protected abstract IMwClient newMmClient();

	protected abstract String getLoginId();

	protected abstract String getPassword();

}
