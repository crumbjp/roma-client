package jp.co.rakuten.roma.spybased_client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import jp.co.rakuten.roma.client.RomaAlist;
import jp.co.rakuten.roma.client.RomaAlistImpl;
import jp.co.rakuten.roma.client.SimpleRomaClient;
import jp.co.rakuten.roma.client.SimpleRomaClientImpl;
import junit.framework.TestCase;

public class AlistPushTest extends TestCase {
	static {
		try {
		}catch (Exception e) {
			;
		}
	}
	@Override
	protected void setUp() throws Exception {
    	super.setUp();
    }
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	static class T implements Runnable {
		static Random rd = new Random();
		SimpleRomaClient c = null;
		RomaAlist<String> alist;
		public T() {
			rd.setSeed(System.currentTimeMillis());
	    	System.err.println("--start--");
    		try {
				c = new SimpleRomaClientImpl("localhost_11211","localhost_11212");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
    		String key = String.valueOf(rd.nextInt()); 
    		alist = new RomaAlistImpl<String>(key,c.getTranscoder());
		}
		@Override
		public void run() {
	    	System.err.println("--run--" );
			try {
				String val = "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
				for(int i = 0 ; i < 30; i++ ) {
//					assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.prepend(val)));
					assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.append(val)));
				}
				for(int i = 0 ; i < 100000; i++ ) {
//					assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToFirst(val, 30)));
					List<String> l = (List<String>)c.extension(alist.getAll());
				}
			}finally {
		    	System.err.println("--finnish--" );
			}
		}
	}
    public void testPrepend() throws InterruptedException{
		long ss = System.currentTimeMillis();
		List<Thread> tl = new ArrayList<Thread>();
    	for(int i = 0 ; i < 20; i++ ) {
    		Thread t = new Thread(new T());
    		tl.add(t);
    		t.start();
    	}
    	for (Thread t: tl) {
    		t.join();
    	}
		long se = System.currentTimeMillis();
		System.out.println("Push: " + (se - ss));
    }
    
    public void test(){
    	
    }

}
