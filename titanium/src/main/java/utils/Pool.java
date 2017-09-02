package utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Pool {
	private final static int POOL_SIZE = 4;
	
	private static ExecutorService pool = null;
	
	public static void submit(Runnable task) {
		if (pool == null)
			pool = Executors.newFixedThreadPool(POOL_SIZE);
		
		pool.submit(task);
	}
	
	public static void close() {
		if (pool != null)
			pool.shutdown();
	}
	
}
