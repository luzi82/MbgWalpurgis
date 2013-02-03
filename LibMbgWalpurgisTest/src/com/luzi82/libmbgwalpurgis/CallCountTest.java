package com.luzi82.libmbgwalpurgis;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.Test;

public class CallCountTest {

	@Test
	public void test() {
		CallCount<Void> ccv = new CallCount<Void>();
		Executor exec = Executors.newCachedThreadPool();

		Utils.startCallback(ccv, null, exec);
		ccv.waitDone(1);
	}

}
