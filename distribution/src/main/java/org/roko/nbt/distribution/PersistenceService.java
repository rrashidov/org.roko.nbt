package org.roko.nbt.distribution;

public interface PersistenceService<T> {

	public GetResult<T> get(String id) throws DataGetException;
}
