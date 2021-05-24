package org.roko.nbt.persistence.api;

import org.roko.nbt.domain.api.DomainEntity;

public interface PersistenceService {

	public DomainEntity save(DomainEntity domainEntity);
}
