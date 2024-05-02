package uk.co.oliford.cache.common;

import java.util.Random;


import uk.co.oliford.cache.randomAccessCache.RACache;
import uk.co.oliford.cache.randomAccessCache.RACacheService;
import uk.co.oliford.jolu.SettingsManager;

public class CacheStressTest implements Runnable {
	
	static{ new SettingsManager("minerva", true);	}
	
	private static CacheService getService(){
		//replace with whatever implementation you want to test
		return new RACacheService(); 
	}
	
	
	/** Delete the file, then run this lots of time, in separate VMs */
	public static void main(String[] args) {
		long calcDelay = 1000;
		if(args.length > 0){
			calcDelay = Long.parseLong(args[0]);
		}
		
		CacheService cacheService = getService();
		int nThreads = 1;
		for(int i=0; i < nThreads; i++){
			(new Thread(new CacheStressTest(cacheService, calcDelay))).start();
		}
		try {
			Thread.sleep(1000000);
		} catch (InterruptedException e) { }
	}
	
	public CacheStressTest(CacheService cacheService, long calcDelay) { 
		this.cacheService = cacheService;
		this.calcDelay = calcDelay;
	}
	
	private CacheService cacheService;
	private long calcDelay;
	
	@Override
	public void run() {
		
		try{
			int nSets = 10000000;		
			int objSize = 10000;
			
			if(cacheService instanceof RACacheService){
				((RACache)cacheService.getCache("test")).cleanAllSets("stressTest", true);
			}
			
			Random randGen = new Random();
			
			for(int i=0; i < nSets; i++){			
				
				double obj[] = new double[objSize];
				for(int j=0; j < objSize; j++) {
					obj[j] = randGen.nextDouble();
				}
				
				byte key = (byte) randGen.nextInt(50);
				
				//one with a key that will get rehit quite often
				cacheService.put("test", "stressTest", key, obj);
				
				//one with a simple int key but the same big data so we can read back
				cacheService.put("test", "stressTest", i, obj);
				
				if(i == 0)
					continue;
				
				//do a random get, of the things we know have been written
				int j = randGen.nextInt(i);
				try{
					long thisDelay = (long)(calcDelay * (0.8 + randGen.nextDouble() * 0.4));
					Thread.sleep(thisDelay); //simulated calculation time, with jitter
				}catch(InterruptedException e){ }
				
				Object x = cacheService.get("test", "stressTest", j);
				//assertTrue(x != null);
				if(x == null)
					System.err.println("Cache missed for item " + j);
				
				
				System.out.println(i + " / " + nSets);
				
				/*if((i % 10) == 0){
					if(cacheService instanceof RACacheService)
						((RACache)cacheService.getCache("test")).getAllSets("stressTest")[0].dumpCacheMemoryStats();
				}*/
				
				//periodically, and randomly, clean the cache			
				if(randGen.nextInt(400) == 0){
					cacheService.getCache("test").organise("stressTest");
				}
			
			}
		}catch(Exception e){
			e.printStackTrace();
			throw(new RuntimeException(e));
		}
	}
	
}
