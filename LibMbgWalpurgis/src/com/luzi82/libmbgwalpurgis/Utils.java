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

	public static <T> void parseCheck(T aExpect, T aActual) throws ParseException {
		if (aExpect == aActual) {
			return;
		}
		if (aExpect.getClass() != aActual.getClass()) {
			throw new ParseException(aExpect + " != " + aActual);
		}
		if (aExpect instanceof String) {
			String e = (String) aExpect;
			String a = (String) aActual;
			if (e.equals(a)) {
				return;
			}
		}
		if (aExpect.equals(aActual)) {
			return;
		}
		throw new ParseException(aExpect + " != " + aActual);
	}

	public static void parseCheck(boolean aValue) throws ParseException {
		if (!aValue) {
			throw new ParseException();
		}
	}

}
