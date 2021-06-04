package org.roko.nbt.distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DistributedPersistenceService<T> implements PersistenceService<T> {
	
	private final GetResult<T> ERROR_GET_RESULT = new GetResult<T>(GetResult.GeneralResult.ERROR);

	private ExecutorService executorService = Executors.newFixedThreadPool(100);
	
	private final List<PersistenceService<T>> persistenceServices = new ArrayList<>();
	private int persistenceServicesCount = 0;
	private int quorumPersistenceServicesCount = 0;
	
	public void registerPersistenceService(PersistenceService<T> persistenceService) {
		persistenceServices.add(persistenceService);
		persistenceServicesCount++;
		quorumPersistenceServicesCount = persistenceServicesCount / 2 + 1;
	}
	
	@Override
	public GetResult<T> get(String id) throws DataGetException {
		final List<Future<GetResult<T>>> futures = new ArrayList<>(persistenceServicesCount);
		final Map<GetResult<T>, Integer> results = new HashMap<>(persistenceServicesCount);
		
		persistenceServices.stream()
			.forEach(persistenceService -> {
				futures.add(executorService.submit(new GetCallable<T>(persistenceService, id)));
			});
		
		boolean quorumResultReceived = false;
		boolean allFuturesRetrieved = false;
		
		while (!quorumResultReceived && !allFuturesRetrieved) {
			registerFutureResult(results, futures.remove(0));

			quorumResultReceived = evaluateQuorumResultReceived(results);
			allFuturesRetrieved = evaluateAllFuturesRetrieved(futures);
		}

		return retrieveQuorumResult(results);
	}

	private void registerFutureResult(Map<GetResult<T>, Integer> results, Future<GetResult<T>> future) {
		try {
			GetResult<T> getResult = future.get();
			
			registerGetresultInMap(results, getResult);
		} catch (InterruptedException | ExecutionException e) {
			registerGetresultInMap(results, ERROR_GET_RESULT);
		}
	}

	private void registerGetresultInMap(Map<GetResult<T>, Integer> results, GetResult<T> getResult) {
		Integer cnt = results.getOrDefault(getResult, new Integer(0));
		results.put(getResult, ++cnt);
	}

	private boolean evaluateQuorumResultReceived(Map<GetResult<T>, Integer> results) {
		int numberOfResultsReceived = 
					results.values().stream()
						.reduce(0, Integer::sum);
		return numberOfResultsReceived >= quorumPersistenceServicesCount;
	}


	private boolean evaluateAllFuturesRetrieved(final List<Future<GetResult<T>>> futures) {
		return futures.size() == 0;
	}

	private GetResult<T> retrieveQuorumResult(Map<GetResult<T>, Integer> results) {
		Integer mostPopularResultOccurenceCount = results.values().stream()
			.reduce(0, Integer::max);
		
		int numberOfMostPopularResults = results.values().stream()
			.map(cnt -> {
				if (cnt.intValue() == mostPopularResultOccurenceCount.intValue()) {
					return cnt;
				}
				return cnt;
			})
			.filter(cnt -> {
				return cnt != null;
			})
			.collect(Collectors.toList())
			.size();
		
		if (numberOfMostPopularResults > 1) {
			return new GetResult<T>(GetResult.GeneralResult.ERROR);
		}

		Optional<Entry<GetResult<T>, Integer>> entryToReturn = results.entrySet().stream()
			.filter(entry -> {
				return entry.getValue().intValue() == mostPopularResultOccurenceCount.intValue();
			})
			.findFirst();
		
		return entryToReturn.get().getKey();
	}

}
