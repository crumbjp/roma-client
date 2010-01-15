package jp.co.rakuten.roma.spybased_client;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.co.rakuten.roma.client.RomaClient;

import net.spy.memcached.MemcachedClient;

/**
 * Hello world!
 *
 */
public class App 
{
	public static List<String> genData () {
		List ret = new ArrayList<String>();
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
	
    public static void main( String[] args ) throws Throwable
    {
    	List<String> data = genData();
    	List<String> l = new ArrayList<String>();
    	l.add("localhost_11211");
    	l.add("localhost_11212");
    	RomaClient c = new RomaClient(l);
    	try {
//    	MemcachedClient mc = new MemcachedClient(new InetSocketAddress("localhost",11211));
//    	System.out.println("GET:"+mc.get("foo"));
    		for(String k : data){
    			c.set(k, 0, k);
    		}
    	c.set("foo", 10000 , new String("fooaaaaaaabbb"));
    	c.set("bar", 10000 , new String("baraaaaaaabbb"));
    	c.set("baz", 10000 , new String("bazaaaaaaabbb"));
    	System.out.println("GET:"+c.get("foo"));
    	System.out.println("GET:"+c.get("bar"));
    	System.out.println("GET:"+c.get("baz"));
    	ArrayList<String> keys = new ArrayList<String>(3);
    	keys.add("foo");
    	keys.add("bar");
    	keys.add("baz");
    	for(int i = 0 ; i < 1000 ; i++) {
    		try {
	        	Thread.sleep(2000);
	        	System.out.println("GET:"+c.get("foo"));
	        	System.out.println("GET:"+c.get("bar"));
	        	System.out.println("GET:"+c.get("baz"));
	        	System.out.println("GETS:"+c.getBulk(keys));
	        	System.out.println("HASH:"+c.mklhash());
	        	System.out.println("DUMP:"+c.routingdump());
    		}catch(Throwable e) {
    	    	c.reconstruct();
    		}
    	}
		}catch(Throwable e) {
			e.printStackTrace();
		}
		Thread.sleep(10000);
    	c.shutdown();
    }
}
