package com.lody.virtual.server.am;

import android.support.v4.util.ArrayMap;
import android.support.v4.util.SparseArrayCompat;

class ProcessMap<E> {
	private final ArrayMap<String, SparseArrayCompat<E>> mMap = new ArrayMap<>();

	public E get(String name, int uid) {
		SparseArrayCompat<E> uids = mMap.get(name);
		if (uids == null)
			return null;
		return uids.get(uid);
	}

	public E put(String name, int uid, E value) {
		SparseArrayCompat<E> uids = mMap.get(name);
		if (uids == null) {
			uids = new SparseArrayCompat<E>(2);
			mMap.put(name, uids);
		}
		uids.put(uid, value);
		return value;
	}

	public E remove(String name, int uid) {
		SparseArrayCompat<E> uids = mMap.get(name);
		if (uids != null) {
			final E old = uids.get(uid);
			uids.remove(uid);
			if (uids.size() == 0) {
				mMap.remove(name);
			}
			return old;
		}
		return null;
	}

	public ArrayMap<String, SparseArrayCompat<E>> getMap() {
		return mMap;
	}
}
