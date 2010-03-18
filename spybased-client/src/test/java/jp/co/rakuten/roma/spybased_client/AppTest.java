package jp.co.rakuten.roma.spybased_client;

import java.util.ArrayList;
import java.util.List;

import net.spy.memcached.CASValue;

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
    	List<String> l = DataGen.gen();
    	System.err.println("--construct--" + l.size());
		for(String k :  l){
			c.set(k, 0, k);
		}
    	c.set("foo", 10000 , new String("fooaaaaaaabbb"));
    	c.set("goo", 10000 , new String("gooaaaaaaabbb"));
    	c.set("hoo", 10000 , new String("hooaaaaaaabbb"));
    	c.set("ioo", 10000 , new String("iooaaaaaaabbb"));
    	c.set("joo", 10000 , new String("jooaaaaaaabbb"));
    	c.set("koo", 10000 , new String("kooaaaaaaabbb"));
    	c.set("loo", 10000 , new String("looaaaaaaabbb"));
    	c.set("moo", 10000 , new String("mooaaaaaaabbb"));
    	c.set("bar", 10000 , new String("baraaaaaaabbb"));
    	c.set("baz", 10000 , new String("bazaaaaaaabbb"));
    	CASValue<Object> cas1 = c.gets("foo",5000);
//    	System.out.println("CAS:"+cas1.getCas());
//    	c.set("foo", 10000 , new String("YYYY"));
//    	CASValue<Object> cas2 = c.gets("foo",5000);
//    	System.out.println("CAS:"+cas2.getCas());
//    	c.append(cas1.getCas(),"foo",new String("XXXX"));
//    	c.cas("foo",cas1.getCas(),new String("XXXX"));
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
    	ArrayList<String> keys = new ArrayList<String>(3);
    	keys.add("foo");
    	keys.add("goo");
    	keys.add("hoo");
    	keys.add("ioo");
    	keys.add("joo");
    	keys.add("koo");
    	keys.add("loo");
    	keys.add("moo");
    	keys.add("bar");
    	keys.add("baz");
    	long tmpTime = System.currentTimeMillis();
    	try {
        	for(int i = 0 ; i < 100000000 ; i++) {
//        	for(int i = 0 ; i < 2 ; i++) {
        		try {
//    	        	Thread.sleep(2000);
//        			c.get("foo");
//        			c.get("goo");
//        			c.get("hoo");
//        			c.get("ioo");
//        			c.get("joo");
//        			c.get("koo");
//        			c.get("loo");
//        			c.get("moo");
//        			c.get("bar");
//        			c.get("baz");
        			c.getBulk(keys);
//    	        	System.out.println("HASH:"+c.mklhash());
//    	        	System.out.println("DUMP:"+c.routingdump());
        		}catch(Throwable e) {
        			System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        	    	//c.reconstruction(); cannot...
        		}
        		if ( (i % 1000) == 0 ) {
        			long newTime = System.currentTimeMillis();
        			System.err.println("I : " + String.valueOf(i) + " : " + (newTime-tmpTime));
        			tmpTime = newTime;
        		}
        		if ( (i % 100000) == 0 ) {
    	        	System.out.println("GET(foo):"+c.get("foo"));
    	        	System.out.println("GET(goo):"+c.get("goo"));
    	        	System.out.println("GET(hoo):"+c.get("hoo"));
    	        	System.out.println("GET(ioo):"+c.get("ioo"));
    	        	System.out.println("GET(joo):"+c.get("joo"));
    	        	System.out.println("GET(koo):"+c.get("koo"));
    	        	System.out.println("GET(loo):"+c.get("loo"));
    	        	System.out.println("GET(moo):"+c.get("moo"));
    	        	System.out.println("GET(bar):"+c.get("bar"));
    	        	System.out.println("GET(baz):"+c.get("baz"));
    	        	System.out.println("GETS:"+c.getBulk(keys));
        		}
        	}
    	}catch(Throwable e) {
    		e.printStackTrace();
    		assertTrue(false);
    	}
    }
}
