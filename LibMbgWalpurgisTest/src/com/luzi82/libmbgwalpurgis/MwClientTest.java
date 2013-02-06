package com.luzi82.libmbgwalpurgis;

import java.util.Properties;
import java.util.concurrent.Executors;

import org.junit.BeforeClass;

import com.luzi82.libmbgwalpurgis.common.Prop;

public class MwClientTest extends AbstractMwClientTest {

	static Properties props;

	@BeforeClass
	public static void oneTimeSetUp() {
		try {
			props=Prop.getAuthProperties();
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
