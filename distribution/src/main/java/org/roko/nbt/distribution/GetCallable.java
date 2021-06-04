package org.roko.nbt.distribution;

import java.util.concurrent.Callable;

public class GetCallable<T> implements Callable<GetResult<T>> {

	private PersistenceService<T> svc;
	private String id;
	
	public GetCallable(PersistenceService<T> svc, String id) {
		this.svc = svc;
		this.id = id;
	}

	@Override
	public GetResult<T> call() throws Exception {
		
		try {
			return svc.get(id);
		} catch (DataGetException e) {
			return new GetResult<>(GetResult.GeneralResult.ERROR);
		}
	}

}
