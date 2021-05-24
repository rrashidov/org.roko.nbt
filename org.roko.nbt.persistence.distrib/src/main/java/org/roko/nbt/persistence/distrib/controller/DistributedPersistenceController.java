package org.roko.nbt.persistence.distrib.controller;

import org.roko.nbt.persistence.api.PersistenceService;

public interface DistributedPersistenceController {

	public PersistenceService get(String id);
}
