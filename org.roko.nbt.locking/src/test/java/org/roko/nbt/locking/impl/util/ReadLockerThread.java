package org.roko.nbt.locking.impl.util;

import org.roko.nbt.locking.api.LockingService;

public class ReadLockerThread extends Thread{

	private ThreadMonitor monitor;
	private String id;
	private LockingService lockingService;
	
	private boolean lockAcquired = false;
	
	public ReadLockerThread(ThreadMonitor monitor, String id, LockingService lockingService) {
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
		
		lockingService.lockForRead(id);
		
		lockAcquired = true;
	}

	public boolean isLockAcquired() {
		return lockAcquired;
	}

}
