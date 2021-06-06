package org.roko.nbt.distribution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		DistributedPersistenceService<Object> persistenceService = new DistributedPersistenceService<Object>();
		
		PersistenceService<Object> directPersistenceService1 = new TestPersistenceService<Object>();
		PersistenceService<Object> directPersistenceService2 = new TestPersistenceService<Object>();
		PersistenceService<Object> directPersistenceService3 = new TestPersistenceService<Object>();
		
		persistenceService.registerPersistenceService(directPersistenceService1);
		persistenceService.registerPersistenceService(directPersistenceService2);
		persistenceService.registerPersistenceService(directPersistenceService3);
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		long cnt = 0;
		
		long lastTimeStatisticsPrinted = System.currentTimeMillis();
		
		while (true) {
			Thread.sleep(1000);
			
			List<Future<Object>> futures = new ArrayList<>();

			long start = System.currentTimeMillis();

			for (int i = 0; i < 1000; i++) {
				
				futures.add(executorService.submit(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						persistenceService.get("test-id");
						return null;
					}
				}));
				
				futures.stream()
				  .forEach(future -> {
					  try {
						future.get();
					} catch (InterruptedException | ExecutionException e) {
					}
				  });

			}
			
			long end = System.currentTimeMillis();
			
			System.out.println("1000 reads executed for " + (end - start));
			
			cnt += 1000;
			
			if ((System.currentTimeMillis() - lastTimeStatisticsPrinted) > (10 * 1000)) {
				System.out.println("Statistics: " + cnt + " reads processed so far");
				lastTimeStatisticsPrinted = System.currentTimeMillis();
			}
		}
	}
}
