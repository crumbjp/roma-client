package jp.co.rakuten.roma.spybased_client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.spy.memcached.RomaClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Throwable
    {
    	List<InetSocketAddress> l = new ArrayList<InetSocketAddress>();
    	l.add(new InetSocketAddress("localhost",11211));
    	l.add(new InetSocketAddress("localhost",11212));
    	RomaClient c = new RomaClient(l);
    	c.set("foo", 1 , new String("aaaaaaabbb"));
    	System.out.println("GET:"+c.get("foo"));
    	System.out.println("HASH:"+c.mklhash());
    }
}
