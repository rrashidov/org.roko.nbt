package org.roko.nbt.distribution;

import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;

import org.junit.rules.ExternalResource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PersistenceServiceRule<T> extends ExternalResource {

	@Mock
	public PersistenceService<T> mock;

	@Override
	protected void before() throws Throwable {
		MockitoAnnotations.initMocks(this);
	}
	
	public void stubGetResult(GetResult<T> getResult) throws DataGetException {
		when(mock.get(anyString())).thenReturn(getResult);
	}
}
