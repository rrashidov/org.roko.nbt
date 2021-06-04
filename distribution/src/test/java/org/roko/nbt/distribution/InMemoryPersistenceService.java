package org.roko.nbt.distribution;

public class InMemoryPersistenceService<T> implements PersistenceService<Object> {

	private static final Object O = new Object();
	
	@Override
	public GetResult<Object> get(String id) throws DataGetException {
		return new GetResult<Object>(GetResult.GeneralResult.FOUND, O);
	}

}
