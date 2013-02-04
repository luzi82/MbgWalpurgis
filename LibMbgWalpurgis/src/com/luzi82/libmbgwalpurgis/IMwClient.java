package com.luzi82.libmbgwalpurgis;

public interface IMwClient {

	enum State {
		OFFLINE, ONLINE, CONNECTING, DISCONNECING
	}

	public void connect(String aLoginId, String aPassword, ICallback<Void> aCallback, ICallback<Exception> aException);

	public void disconnect(ICallback<Void> aCallback);

	public String getLoginId();

	public State getState();

	public void setStateChangeCallback(ICallback<State> aCallback);

	public void getFeed(ICallback<RaidBossMatchingFeed> aCallback, ICallback<Exception> aExceptionCallback);

	public void getStatus(ICallback<PlayerStatus> aCallback, ICallback<Exception> aExceptionCallback);

}
