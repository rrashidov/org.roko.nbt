package org.roko.nbt.locking.impl;

import java.util.HashMap;
import java.util.Map;

import org.roko.nbt.locking.api.LockService;

public class OptimizedLockingService implements LockService {

	private final Object MONITOR = new Object();

	private Map<String, LockInfo> locksMap = new HashMap<>();

	@Override
	public void lockForRead(String id) {
		LockInfo lockInfo = getLockInfo(id);
		
		boolean processed = false;
		
		while (!processed) {
			synchronized (lockInfo) {
				if (lockInfo.locked()) {
					try {
						lockInfo.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				} else {
					lockInfo.inc();
					lockInfo.notifyAll();
					processed = true;
				}
			}
		}
	}

	@Override
	public void unlockForRead(String id) {
		LockInfo lockCounter = getLockInfo(id);
		synchronized (lockCounter) {
			lockCounter.dec();
			lockCounter.notifyAll();
		}
	}

	@Override
	public void lockForWrite(String id) {
		LockInfo lockCounter = getLockInfo(id);
		boolean processed = false;
		while (!processed) {
			synchronized (lockCounter) {
				if (lockCounter.readLocks() > 0) {
					try {
						lockCounter.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				} else if (lockCounter.locked()) {
					try {
						lockCounter.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				} else {
					lockCounter.lock();
					lockCounter.notifyAll();
					processed = true;
				}
			}
		}
	}

	@Override
	public void unlockForWrite(String id) {
		LockInfo lockCounter = getLockInfo(id);
		synchronized (lockCounter) {
			lockCounter.unlock();
			lockCounter.notifyAll();
		}
	}

	private LockInfo getLockInfo(String id) {
		LockInfo o = locksMap.get(id);

		if (o == null) {
			synchronized (MONITOR) {
				o = new LockInfo();
				locksMap.put(id, o);
			}
		}

		return o;
	}
}
