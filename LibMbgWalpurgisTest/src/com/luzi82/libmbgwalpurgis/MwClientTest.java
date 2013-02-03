package com.luzi82.libmbgwalpurgis;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.junit.BeforeClass;

public class MwClientTest extends AbstractMwClientTest {

	static Properties props;

	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			props = new Properties();
			FileInputStream authFileIn;
			authFileIn = new FileInputStream("auth.properties");
			props.load(authFileIn);
			authFileIn.close();
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	@Override
	protected IMwClient newMmClient() {
		return new MwClient(Executors.newCachedThreadPool());
	}

	@Override
	protected String getLoginId() {
		return props.getProperty("login_id");
	}

	@Override
	protected String getPassword() {
		return props.getProperty("login_pw");
	}

}
