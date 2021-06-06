package org.roko.nbt.distribution;

import org.roko.nbt.distribution.GetResult.GeneralResult;

public class TestPersistenceService<T> implements PersistenceService<Object> {

	private static final Object o = new Object();
	
	@Override
	public GetResult<Object> get(String id) throws DataGetException {
		return new GetResult<>(GeneralResult.FOUND, o);
	}

}
