package org.roko.nbt.distribution;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DistributedPersistenceServiceTest {
	
	private static final Object TEST_OBJECT = new Object();
	private static final Object TEST_OBJECT_2 = new Object();

	private static final String TEST_ID = "test-id";

	private static final GetResult<Object> OK_RESULT = new GetResult<Object>(GetResult.GeneralResult.FOUND, TEST_OBJECT);
	private static final GetResult<Object> OK_RESULT_2 = new GetResult<Object>(GetResult.GeneralResult.FOUND, TEST_OBJECT_2);
	
	private static final GetResult<Object> NOT_FOUND_RESULT = new GetResult<Object>(GetResult.GeneralResult.NOT_FOUND);
	
	private static final GetResult<Object> ERROR_RESULT = new GetResult<>(GetResult.GeneralResult.ERROR);
	
	private DistributedPersistenceService<Object> svc;

	@Rule
	public PersistenceServiceRule<Object> persistenceServiceRule1 = new PersistenceServiceRule<Object>();
	
	@Rule
	public PersistenceServiceRule<Object> persistenceServiceRule2 = new PersistenceServiceRule<Object>();

	@Rule
	public PersistenceServiceRule<Object> persistenceServiceRule3 = new PersistenceServiceRule<Object>();

	@Before
	public void setup() {
		svc = new DistributedPersistenceService<>();
	}

	@Test
	public void okResultIsReturned_whenSingleSubserviceReturnsOK() throws DataGetException {
		svc.registerPersistenceService(persistenceServiceRule1.mock);
		
		persistenceServiceRule1.stubGetResult(OK_RESULT);
		
		GetResult<Object> getResult = svc.get(TEST_ID);
		
		assertEquals(TEST_OBJECT, getResult.getValue());
	}

	@Test
	public void notFoundResultIsReturned_whenSingleSubserviceReturnsNotFound() throws DataGetException {
		svc.registerPersistenceService(persistenceServiceRule1.mock);
		
		persistenceServiceRule1.stubGetResult(NOT_FOUND_RESULT);
		
		GetResult<Object> getResult = svc.get(TEST_ID);
		
		assertEquals(GetResult.GeneralResult.NOT_FOUND, getResult.getGeneralResult());
	}
	
	@Test
	public void errorResultIsReturned_whenSingleSubserviceReturnsError() throws DataGetException {
		svc.registerPersistenceService(persistenceServiceRule1.mock);
		
		persistenceServiceRule1.stubGetResult(ERROR_RESULT);
		
		GetResult<Object> getResult = svc.get(TEST_ID);
		
		assertEquals(GetResult.GeneralResult.ERROR, getResult.getGeneralResult());
	}
	
	@Test
	public void getWorksProperly() throws DataGetException {
		svc.registerPersistenceService(persistenceServiceRule1.mock);
		svc.registerPersistenceService(persistenceServiceRule2.mock);
		
		persistenceServiceRule1.stubGetResult(OK_RESULT);
		persistenceServiceRule2.stubGetResult(OK_RESULT);
		
		GetResult<Object> getResult = svc.get(TEST_ID);
		
		assertEquals(GetResult.GeneralResult.FOUND, getResult.getGeneralResult());
		assertEquals(TEST_OBJECT, getResult.getValue());
	}
	
	@Test
	public void differentValuesProduceError() throws DataGetException {
		svc.registerPersistenceService(persistenceServiceRule1.mock);
		svc.registerPersistenceService(persistenceServiceRule2.mock);

		persistenceServiceRule1.stubGetResult(OK_RESULT);
		persistenceServiceRule2.stubGetResult(OK_RESULT_2);
		
		GetResult<Object> getResult = svc.get(TEST_ID);
		
		assertEquals(GetResult.GeneralResult.ERROR, getResult.getGeneralResult());
	}

	@Test
	public void quorumValuesAreTheOnesThatAreReturned() throws DataGetException {
		svc.registerPersistenceService(persistenceServiceRule1.mock);
		svc.registerPersistenceService(persistenceServiceRule2.mock);
		svc.registerPersistenceService(persistenceServiceRule3.mock);
		
		persistenceServiceRule1.stubGetResult(OK_RESULT);
		persistenceServiceRule2.stubGetResult(OK_RESULT);
		persistenceServiceRule3.stubGetResult(OK_RESULT_2);
		
		GetResult<Object> getResult = svc.get(TEST_ID);
		
		assertEquals(GetResult.GeneralResult.FOUND, getResult.getGeneralResult());
		assertEquals(TEST_OBJECT, getResult.getValue());

	}
}
