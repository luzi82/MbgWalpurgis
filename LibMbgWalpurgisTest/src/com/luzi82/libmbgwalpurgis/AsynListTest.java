package com.luzi82.libmbgwalpurgis;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

public class AsynListTest {

	@Test
	public void test() {
		Executor e = Executors.newCachedThreadPool();
		final CallWait<Void> cw = new CallWait<Void>();
		final AsynList cl = new AsynList();
		final int[] a = { 0 };
		final int[] b = { 0 };
		final int[] c = { 0 };
		final int[] i = { 10 };
		cl.addCallback(new ICallback<Void>() {
			@Override
			public void callback(Void aV) {
				a[0] = i[0]++;
				cl.startNext(null);
			}
		});
		cl.addCallback(new ICallback<Void>() {
			@Override
			public void callback(Void aV) {
				b[0] = i[0]++;
				cl.startNext(null);
			}
		});
		cl.addCallback(new ICallback<Void>() {
			@Override
			public void callback(Void aV) {
				c[0] = i[0]++;
				cl.startNext(null);
			}
		});
		cl.addCallback(new ICallback<Void>() {
			@Override
			public void callback(Void aV) {
				cw.newCallback().callback(null);
			}
		});

		cl.start(e);
		Assert.assertTrue(cw.waitDone());
		Assert.assertEquals(10, a[0]);
		Assert.assertEquals(11, b[0]);
		Assert.assertEquals(12, c[0]);
	}

}
