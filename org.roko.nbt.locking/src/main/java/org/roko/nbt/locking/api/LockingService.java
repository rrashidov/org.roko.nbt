package org.roko.nbt.locking.api;

/**
 * Agile locking service that provides optimised locking features:
 * <ul>
 * 	<li>multiple read locks can be acquired for a single id</li>
 *  <li>only a single write lock can be acquired for a single id</li>
 *  <li>if there are read locks, write lock waits</li>
 *  <li>if there is a write lock, read locks wait</li>
 * </ul>
 *
 */
public interface LockingService {

	/**
	 * Acquires read lock for the given id
	 * @param id Id to acquire read lock for
	 */
	public void lockForRead(String id);
	
	/**
	 * Releases read lock for the given id
	 * @param id Id to release read lock for
	 */
	public void unlockForRead(String id);
	
	/**
	 * Acquires read lock for the given id
	 * @param id Id to acquire read lock for
	 */
	public void lockForWrite(String id);
	
	/**
	 * Releases write lock for the given id
	 * @param id Id to release write lock for
	 */
	public void unlockForWrite(String id);
}
