package org.roko.nbt.locking.impl.util;

import org.roko.nbt.locking.api.LockService;

public class LockingServiceWrapper {

	private final ThreadMonitor readThreadMonitor = new ThreadMonitor();
	private final ThreadMonitor writeThreadMonitor = new ThreadMonitor();
	
	private LockService lockingService;
	
	private ReadLockerThread readLockerThread;
	private WriteLockerThread writeLockerThread;
	
	public LockingServiceWrapper(LockService lockingService) {
		this.lockingService = lockingService;
	}
	
	public void asyncReadLock(String id) {
		readLockerThread = new ReadLockerThread(readThreadMonitor, id, lockingService);
		readLockerThread.start();
		
		synchronized(readThreadMonitor){
			if (!readThreadMonitor.isScheduled()) {
				try {
					readThreadMonitor.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	public void syncReadLock(String id) {
		lockingService.lockForRead(id);
	}
	
	public void unlockReadLock(String id) {
		lockingService.unlockForRead(id);
	}
	
	public void asyncWriteLock(String id) {
		writeLockerThread = new WriteLockerThread(writeThreadMonitor, id, lockingService);
		writeLockerThread.start();
		
		synchronized(writeThreadMonitor){
			if (!writeThreadMonitor.isScheduled()) {
				try {
					writeThreadMonitor.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	public void syncWriteLock(String id) {
		lockingService.lockForWrite(id);
	}
	
	public void unlockWriteLock(String id) {
		lockingService.unlockForWrite(id);
	}
	
	public boolean readLockAcquired() {
		return readLockerThread.isLockAcquired();
	}
	
	public boolean writeLockAcquired() {
		return writeLockerThread.isLockAcquired();
	}
}
