package com.luzi82.libmbgwalpurgis;

public abstract class CallbackE<T> implements ICallback<T> {

	final ICallback<Exception> mExceptionCallback;

	public CallbackE(ICallback<Exception> aExceptionCallback) {
		mExceptionCallback = aExceptionCallback;
	}

	protected abstract void cb(T aResult) throws Exception;

	@Override
	public final void callback(T aResult) {
		try {
			cb(aResult);
		} catch (Exception e) {
			mExceptionCallback.callback(e);
		}
	}
}
