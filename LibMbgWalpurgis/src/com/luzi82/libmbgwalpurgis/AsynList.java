package com.luzi82.libmbgwalpurgis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executor;

public class AsynList extends LinkedList<ICallback<Object>> {

	private static final long serialVersionUID = 7971318935647578117L;

	Executor mExecutor = null;
	Iterator<ICallback<Object>> itr = null;

	public <T> void addCallback(final ICallback<T> aCb) {
		add(new ICallback<Object>() {
			@Override
			public void callback(Object aResult) {
				@SuppressWarnings("unchecked")
				T t = (T) aResult;
				aCb.callback(t);
			}
		});
	}

	public void addRunnable(final Runnable aRunnable) {
		add(new ICallback<Object>() {
			@Override
			public void callback(Object aResult) {
				aRunnable.run();
			}
		});
	}

	public void startNext(Object aArg) {
		Utils.startCallback(itr.next(), aArg, mExecutor);
	}

	public void start(Executor aExecutor) {
		mExecutor = aExecutor;
		itr = iterator();
		startNext(null);
	}

	public <T> ICallback<T> createStartNextCallback(T[] aV) {
		return new ICallback<T>() {
			@Override
			public void callback(T aResult) {
				startNext(aResult);
			}
		};
	}

}
