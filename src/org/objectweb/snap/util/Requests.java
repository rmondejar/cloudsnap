package org.objectweb.snap.util;

public class Requests {
	
	private static final int SECONDS = 10;	
	private static long[] requests = new long[SECONDS];	
	private static int lastSecond = 0;
	
	private synchronized void clear(int second) {
        int sec = second;		
		while (lastSecond!=sec) {
			requests[sec] = 0;
			sec = (sec-1+SECONDS)%SECONDS;	
		}			
		lastSecond = second;
    }
    
	public long requestArrive() {
		
		long total = System.currentTimeMillis()/1000;
		int second = (int) total%SECONDS;
		//clear diference between last <--> current
		if (second!=lastSecond) clear(second);
				
		requests[second]++;
		//int previous = (second-1+SECONDS)%SECONDS;	
		long acum = 0; 
		for (long slice : requests) {
			acum+=slice;
			//System.out.print(slice+",");
		}
		return acum;		
	}
	

}
