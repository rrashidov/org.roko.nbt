package org.roko.nbt.locking.impl.util;

import org.roko.nbt.locking.api.LockService;

public class WriteLockerThread extends Thread{

	private ThreadMonitor monitor;
	private String id;
	private LockService lockingService;
	
	private boolean lockAcquired = false;
	
	public WriteLockerThread(ThreadMonitor monitor, String id, LockService lockingService) {
		this.monitor = monitor;
		this.id = id;
		this.lockingService = lockingService;
	}

	@Override
	public void run() {
		synchronized(monitor) {
			monitor.setScheduled(true);
			monitor.notify();
		}
		
		lockingService.lockForWrite(id);
		
		lockAcquired = true;
	}

	public boolean isLockAcquired() {
		return lockAcquired;
	}

}
