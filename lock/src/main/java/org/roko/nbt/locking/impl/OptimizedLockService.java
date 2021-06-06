package org.roko.nbt.locking.impl;

import java.util.HashMap;
import java.util.Map;

import org.roko.nbt.locking.api.LockService;

public class OptimizedLockService implements LockService {

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
		LockInfo lockInfo = getLockInfo(id);
		synchronized (lockInfo) {
			lockInfo.dec();
			lockInfo.notifyAll();
		}
	}

	@Override
	public void lockForWrite(String id) {
		LockInfo lockInfo = getLockInfo(id);
		boolean processed = false;
		while (!processed) {
			synchronized (lockInfo) {
				if (lockInfo.readLocks() > 0) {
					try {
						lockInfo.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				} else if (lockInfo.locked()) {
					try {
						lockInfo.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				} else {
					lockInfo.lock();
					lockInfo.notifyAll();
					processed = true;
				}
			}
		}
	}

	@Override
	public void unlockForWrite(String id) {
		LockInfo lockInfo = getLockInfo(id);
		synchronized (lockInfo) {
			lockInfo.unlock();
			lockInfo.notifyAll();
		}
	}

	private LockInfo getLockInfo(String id) {
		LockInfo lockInfo = locksMap.get(id);

		if (lockInfo == null) {
			synchronized (MONITOR) {
				lockInfo = locksMap.get(id);
				if (lockInfo == null) {
					lockInfo = new LockInfo();
					locksMap.put(id, lockInfo);
				}
			}
		}

		return lockInfo;
	}
}
