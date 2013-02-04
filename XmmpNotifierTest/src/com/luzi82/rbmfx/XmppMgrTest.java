package com.luzi82.rbmfx;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Pattern;

import org.junit.Test;

public class XmppMgrTest {

	@Test
	public void test() {
		try {
			Properties props = new Properties();
			FileInputStream authFileIn = new FileInputStream("auth.properties");
			props.load(authFileIn);
			authFileIn.close();

			String xmpp_from_id = props.getProperty("xmpp_from_id");
			String xmpp_from_pw = props.getProperty("xmpp_from_pw");
			String xmpp_to_id = props.getProperty("xmpp_to_id");

			XmppMgr xmpp = new XmppMgr(new ScheduledThreadPoolExecutor(10),
					xmpp_from_id, xmpp_from_pw, xmpp_to_id);
			xmpp.setEnabled(true);
			xmpp.send("XmppMgrTest");

			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}