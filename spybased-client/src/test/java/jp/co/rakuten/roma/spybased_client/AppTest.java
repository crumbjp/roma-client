package jp.co.rakuten.roma.spybased_client;

import java.util.ArrayList;
import java.util.List;

import jp.co.rakuten.roma.client.RomaClient;
import jp.co.rakuten.roma.client.RomaClientImpl;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    public AppTest( String testName ) {
        super( testName );
    }
    public static Test suite() {
        return new TestSuite( AppTest.class );
    }

    
	static RomaClient c;
	static class DataGen { 
		static List<String> gen() {
			List<String> ret = new ArrayList<String>();
			String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			for ( int l = 0 ; l < str.length(); l++){
				String c = str.substring(l, l+1);
				String k = c;
				for ( int i = 0 ; i < 100 ; i++ ){
					k = k+c;
					ret.add(k);
				}
			}
			return ret;
		}
	}
    
    @Override
    protected void setUp() throws Exception {
    	System.err.println("--start--");
    	c = new RomaClientImpl("localhost_11211","localhost_11212"); 
    	System.err.println("--construct--");
		for(String k : DataGen.gen() ){
			c.set(k, 0, k);
		}
    	c.set("foo", 10000 , new String("fooaaaaaaabbb"));
    	c.set("bar", 10000 , new String("baraaaaaaabbb"));
    	c.set("baz", 10000 , new String("bazaaaaaaabbb"));
    	System.err.println("--setdatas--");
    	super.setUp();
    }
	@Override
	protected void tearDown() throws Exception {
		Thread.sleep(2000);
    	c.shutdown();
		super.tearDown();
	}    
    public void test() {
    	System.out.println("GET:"+c.get("foo",5000));
    	System.out.println("GET:"+c.get("bar"));
    	System.out.println("GET:"+c.get("baz"));
    	ArrayList<String> keys = new ArrayList<String>(3);
    	keys.add("foo");
    	keys.add("bar");
    	keys.add("baz");
    	try {
//        	for(int i = 0 ; i < 1000 ; i++) {
        	for(int i = 0 ; i < 2 ; i++) {
        		try {
    	        	Thread.sleep(2000);
    	        	System.out.println("GET(foo):"+c.get("foo"));
    	        	System.out.println("GET(bar):"+c.get("bar"));
    	        	System.out.println("GET(baz):"+c.get("baz"));
    	        	System.out.println("GETS:"+c.getBulk(keys));
//    	        	System.out.println("HASH:"+c.mklhash());
//    	        	System.out.println("DUMP:"+c.routingdump());
        		}catch(Throwable e) {
        	    	c.reconstruction();
        		}
        	}
    	}catch(Throwable e) {
    		e.printStackTrace();
    		assertTrue(false);
    	}
    }
}
