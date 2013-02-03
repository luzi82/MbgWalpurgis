package com.luzi82.rbmfx;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;

public class XmppTest {

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
			String[] xmpp_from_id_s = xmpp_from_id.split(Pattern.quote("@"));
			String name = xmpp_from_id_s[0];
			String server = xmpp_from_id_s[1];

			Connection conn = new XMPPConnection(server);
			conn.connect();
			conn.login(name, xmpp_from_pw);
			Chat chat = conn.getChatManager().createChat(xmpp_to_id,
					new MessageListener() {
						@Override
						public void processMessage(Chat arg0, Message arg1) {
						}
					});
			chat.sendMessage("HelloWorld");
			chat.sendMessage("http://www.yahoo.com.hk");
			chat.sendMessage("A\nhttp://www.yahoo.com.hk");
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
