package com.luzi82.libmbgwalpurgis;

import java.util.LinkedList;

import com.luzi82.libmbgwalpurgis.ICallback;

public class CallCount<T> implements ICallback<T> {

	LinkedList<T> mResultList = new LinkedList<T>();

	@Override
	public synchronized void callback(T aResult) {
		mResultList.addLast(aResult);
		notify();
	}

	public synchronized int size() {
		return mResultList.size();
	}

	public synchronized void clear() {
		mResultList.clear();
	}

	public synchronized T waitDone(int aSize) {
		while (mResultList.size() < aSize) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
			}
		}
		return mResultList.get(aSize - 1);
	}

}
