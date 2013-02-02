package com.luzi82.libmbgwalpurgis;

import java.util.concurrent.Executor;

public class Utils {

	public static <T> void startCallback(final ICallback<T> aCallback, final T aValue, Executor aExecutor) {
		if (aCallback == null)
			return;
		aExecutor.execute(new Runnable() {
			@Override
			public void run() {
				aCallback.callback(aValue);
			}
		});
	}

}
