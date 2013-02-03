package com.luzi82.libmbgwalpurgis;

import com.luzi82.libmbgwalpurgis.ICallback;

public class CallWait<T> {

	public boolean mCallbackDone;
	public T mCallbackReturn;

	public boolean mExceptionDone;
	public Exception mException;

	public ICallback<T> newCallback() {
		return new ICallback<T>() {
			@Override
			public void callback(T aResult) {
				synchronized (CallWait.this) {
					mCallbackDone = true;
					mCallbackReturn = aResult;
					CallWait.this.notify();
				}
			}
		};
	}

	public ICallback<Exception> newExceptionHandler() {
		return new ICallback<Exception>() {
			@Override
			public void callback(Exception aResult) {
				synchronized (CallWait.this) {
					mExceptionDone = true;
					mException = aResult;
					aResult.printStackTrace();
					CallWait.this.notify();
				}
			}
		};
	}

	public synchronized boolean waitDone() {
		while (!(mCallbackDone || mExceptionDone)) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
			}
		}
		return mCallbackDone;
	}

}
