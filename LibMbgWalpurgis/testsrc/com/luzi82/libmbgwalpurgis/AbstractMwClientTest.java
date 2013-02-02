package com.luzi82.libmbgwalpurgis;

import org.junit.Assert;
import org.junit.Test;

import com.luzi82.libmbgwalpurgis.IMwClient;

public abstract class AbstractMwClientTest {

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

	protected abstract IMwClient newMmClient();

	protected abstract String getLoginId();

	protected abstract String getPassword();

}
