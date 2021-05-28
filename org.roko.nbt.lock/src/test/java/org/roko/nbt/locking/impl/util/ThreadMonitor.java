package org.roko.nbt.locking.impl.util;

public class ThreadMonitor {

	private boolean scheduled = false;

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}
	
}
