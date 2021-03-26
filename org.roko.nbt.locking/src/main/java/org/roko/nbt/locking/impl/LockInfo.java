package org.roko.nbt.locking.impl;

public class LockInfo {

	private boolean lockedForWrite = false;
	private int readLocksCount = 0;
	
	public void lockForWrite() {
		lockedForWrite = true;
	}
	
	public void inc() {
		readLocksCount++;
	}
	
	public void dec() {
		readLocksCount--;
	}
	
	public void lock() {
		lockedForWrite = true;
	}
	
	public void unlock() {
		lockedForWrite = false;
	}
	
	public boolean locked() {
		return lockedForWrite;
	}
	
	public int readLocks() {
		return readLocksCount;
	}
}
