package com.luzi82.rbmfx;

import com.luzi82.libmbgwalpurgis.ICallback;

public interface IXmppMgr {

	public void send(String aMsg);

	public void setEnabled(boolean aValue);

	public boolean getEnabled();

	public void setMessageCallback(ICallback<String> aMessage);

}
