package org.roko.nbt.locking.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.roko.nbt.locking.api.LockingService;
import org.roko.nbt.locking.impl.OptimizedLockingService;
import org.roko.nbt.locking.impl.util.LockingServiceWrapper;

public class OptimizedLockingServiceTest {

	private static final String TEST_ID = "test_id";

	private LockingService lockingService;
	private LockingServiceWrapper lockingServiceWrapper;
	private LockingServiceWrapper anotherLockingServiceWrapper;

	@Before
	public void setup() {
		lockingService = new OptimizedLockingService();

		lockingServiceWrapper = new LockingServiceWrapper(lockingService);
		anotherLockingServiceWrapper = new LockingServiceWrapper(lockingService);
	}

	@Test
	public void multipleReadLocksCanBeAcquiredForASingleId() {
		lockingService.lockForRead(TEST_ID);
		lockingService.lockForRead(TEST_ID);
	}

	@Test
	public void multipleWriteLocksCanNotBeAcquiredForASingleId() {
		lockingServiceWrapper.asyncWriteLock(TEST_ID);
		anotherLockingServiceWrapper.asyncWriteLock(TEST_ID);

		assertEquals("At least one of the write locking threads should acquire lock", true,
				lockingServiceWrapper.writeLockAcquired() || anotherLockingServiceWrapper.writeLockAcquired());
		assertEquals("Only one of the write locking threads should acquire lock", false,
				lockingServiceWrapper.writeLockAcquired() && anotherLockingServiceWrapper.writeLockAcquired());
	}

	@Test
	public void writeLockWaitsForReadLocksToBeUnlocked() {
		lockingService.lockForRead(TEST_ID);
		lockingService.lockForRead(TEST_ID);

		lockingServiceWrapper.asyncWriteLock(TEST_ID);

		assertEquals("Write lock should wait for read locks to be released", false,
				lockingServiceWrapper.writeLockAcquired());

		lockingService.unlockForRead(TEST_ID);
		lockingService.unlockForRead(TEST_ID);

		assertThatWriteLockIsAcquiredShortlyAfter();
	}

	@Test
	public void readLocksWaitForWriteLockToBeUnlocked() {
		lockingService.lockForWrite(TEST_ID);

		lockingServiceWrapper.asyncReadLock(TEST_ID);

		assertEquals("Read lock should wait for write locks to be released", false,
				lockingServiceWrapper.readLockAcquired());

		lockingService.unlockForWrite(TEST_ID);

		assertThatReadLockIsAcquiredShortlyAfter();
	}

	private void assertThatWriteLockIsAcquiredShortlyAfter() {
		if (!lockingServiceWrapper.writeLockAcquired()) {
			sleep();
		}

		assertEquals("Write lock should be acquired after read locks are released", true,
				lockingServiceWrapper.writeLockAcquired());
	}

	private void assertThatReadLockIsAcquiredShortlyAfter() {
		if (!lockingServiceWrapper.readLockAcquired()) {
			sleep();
		}

		assertEquals("Read lock should be acquired after write locks are released", true,
				lockingServiceWrapper.readLockAcquired());
	}

	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
