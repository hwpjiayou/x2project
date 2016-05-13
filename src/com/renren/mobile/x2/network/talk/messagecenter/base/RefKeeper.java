package com.renren.mobile.x2.network.talk.messagecenter.base;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RefKeeper<T> {

	private final LinkedList<T> strongSet = new LinkedList<T>();
	private final LinkedList<WeakReference<T>> weakList = new LinkedList<WeakReference<T>>();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public static interface Iter<T> {
		/**
		 * @param t
		 *            data
		 * @return true to continue
		 */
		boolean iter(T t);
	}

	public RefKeeper() {
	}

	public RefKeeper(RefKeeper<T> keeper) {
		if (keeper == null) {
			return;
		}

		keeper.lock.readLock().lock();
		lock.writeLock().lock();

		strongSet.addAll(keeper.strongSet);
		for (WeakReference<T> wp : keeper.weakList) {
			T $t = wp.get();
			if ($t != null) {
				weakList.add(new WeakReference<T>($t));
			}
		}

		lock.writeLock().unlock();
		keeper.lock.readLock().unlock();
	}

	public void add(boolean weak, T t) {
		if (t == null) {
			return;
		}
		lock.writeLock().lock();
		try {
			if (weak) {
				Iterator<WeakReference<T>> i = weakList.iterator();
				while (i.hasNext()) {
					T $t = i.next().get();
					if ($t == null) {
						i.remove();
					} else if ($t == t) {
						return;
					}
				}
				weakList.add(new WeakReference<T>(t));
			} else {
				strongSet.add(t);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void remove(T t) {
		if (t == null) {
			return;
		}
		lock.writeLock().lock();
		try {
			Iterator<WeakReference<T>> i = weakList.iterator();
			while (i.hasNext()) {
				T $t = i.next().get();
				if ($t == null || $t == t) {
					i.remove();
				}
			}

			Iterator<T> i2 = strongSet.iterator();
			while (i2.hasNext()) {
				T $t = i2.next();
				if ($t == null || $t == t) {
					i2.remove();
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void clean(boolean strong, boolean weak) {
		lock.writeLock().lock();
		if (strong) {
			strongSet.clear();
		}
		if (weak) {
			weakList.clear();
		}
		lock.writeLock().unlock();
	}

	public void clean() {
		clean(true, true);
	}

	public void iter(Iter<T> i) {
		if (i == null) {
			return;
		}
		lock.readLock().lock();
		try {
			for (T sp : strongSet) {
				if (i.iter(sp)) {
					break;
				}
			}
			Iterator<WeakReference<T>> iterator = weakList.iterator();
			while (iterator.hasNext()) {
				T $t = iterator.next().get();
				if ($t == null) {
					iterator.remove();
				} else if (i.iter($t)) {
					break;
				}
			}
		} finally {
			lock.readLock().unlock();
		}
	}

}
