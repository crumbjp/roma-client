package jp.co.rakuten.roma.spybased_client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.rakuten.roma.client.RomaAlist;
import jp.co.rakuten.roma.client.RomaAlistImpl;
import jp.co.rakuten.roma.client.RomaClient;
import jp.co.rakuten.roma.client.RomaClientImpl;
import junit.framework.TestCase;

public class AlistTest extends TestCase {
	static RomaClient c = null;
	static RomaAlist<String> alist;
	static List<String> vlist;
	static {
		try {
		}catch (Exception e) {
			;
		}
	}
	@Override
	protected void setUp() throws Exception {
    	System.err.println("--start--");
    	if ( c == null ) {
    		c = new RomaClientImpl("localhost_11211","localhost_11212");
    		alist = new RomaAlistImpl<String>("foobar",c.getTranscoder());
    	}
    	System.err.println("--construct--" );
    	vlist = new ArrayList<String>();
    	vlist.add("000");
    	vlist.add("001");
    	vlist.add("002");
    	vlist.add("003");
    	vlist.add("004");
    	vlist.add("005");
    	vlist.add("006");
    	vlist.add("007");
    	vlist.add("008");
    	vlist.add("009");
    	vlist.add("010");
    	vlist.add("011");
    	vlist.add("012");
    	vlist.add("013");
    	vlist.add("014");
    	vlist.add("015");
    	vlist.add("016");
    	vlist.add("017");
    	vlist.add("018");
    	vlist.add("019");
    	System.err.println("--setdatas--");
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.deleteAll()));
    	for ( String d : vlist) 
    		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.append(d)));
    	super.setUp();
    }
	@Override
	protected void tearDown() throws Exception {
		//Thread.sleep(2000);
    	//c.shutdown(); c=null;
		super.tearDown();
	}    
    public void testAppendLimit1(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.append("---",21)));
		vlist.add("---");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testAppendLimit2(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.append("---",20)));
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testDelete(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.delete("018")));
		vlist.remove(18);
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testDeleteAt(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.deleteAt(18)));
		vlist.remove(18);
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testPrepend(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.prepend("---")));
		vlist.add(0,"---");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testPrependLimit1(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.prepend("---",21)));
		vlist.add(0,"---");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testPrependLimit2(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.prepend("---",20)));
		vlist.remove(19);
		vlist.add(0,"---");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testInsert(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insert(10,"---")));
		vlist.add(10,"---");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testGetSubList(){
		vlist.remove(0);
		{
			List<String> l = (List<String>)c.extension(alist.getSubList(1,19));
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testGet(){
		assertEquals("010",c.extension(alist.get(10)));
    }
    public void testInsertOrMoveToFirst(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToFirst("010")));
		vlist.remove(10);
		vlist.add(0,"010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testInsertOrMoveToFirstLimit1(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToFirst("010",20)));
		vlist.remove(10);
		vlist.add(0,"010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testInsertOrMoveToFirstLimit2(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToFirst("010",19)));
		vlist.remove(10);
		vlist.add(0,"010");
		vlist.remove(19);
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testInsertOrMoveToFirstExpired() throws InterruptedException{
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToFirst("010",(long)1)));
		vlist.remove(10);
		vlist.add(0,"010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
		// Not expired yet !
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToLast("---", 20,(long)1)));
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
		Thread.sleep(1500);
		// Already expired !
		vlist.clear();
		vlist.add("---");
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToLast("---", 20,(long)1)));
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testInsertOrMoveToFirstExpiredLimit1(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToFirst("010",20,(long)1)));
		vlist.remove(10);
		vlist.add(0,"010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testInsertOrMoveToFirstExpiredLimit2(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToFirst("010",19,(long)1)));
		vlist.remove(10);
		vlist.add(0,"010");
		vlist.remove(19);
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }

    public void testInsertOrMoveToLast(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToLast("010")));
		vlist.remove(10);
		vlist.add("010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testInsertOrMoveToLastLimit1(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToLast("010",20)));
		vlist.remove(10);
		vlist.add("010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testInsertOrMoveToLastLimit2() throws InterruptedException{
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToLast("010",19)));
		vlist.remove(10);
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testInsertOrMoveToLastExpired() throws InterruptedException{
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToLast("010",(long)1)));
		vlist.remove(10);
		vlist.add("010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
		// Not expired yet !
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToLast("---", 20,(long)1)));
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
		Thread.sleep(1500);
		// Already expired !
		vlist.clear();
		vlist.add("---");
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToLast("---", 20,(long)1)));
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testInsertOrMoveToLastExpiredLimit1(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToLast("010",20,(long)1)));
		vlist.remove(10);
		vlist.add("010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testInsertOrMoveToLastExpiredLimit2(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToLast("010",19,(long)1)));
		vlist.remove(10);
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
			assertEquals(l.size(), vlist.size());
		}
    }
    public void testSize(){
		assertEquals(new Integer(20),c.extension(alist.size()));
    }
    public void testGetEntryAll(){
		{
			List<RomaAlist.RomaAlistEntry<String>> l = (List<RomaAlist.RomaAlistEntry<String>>)c.extension(alist.getEntryAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( RomaAlist.RomaAlistEntry<String> e : l ) {
	    		assertEquals(it.next(),e.getValue());
	    		assertTrue(e.getTime() > 0 );
			}
		}
    }
    public void testGetEntrySubList(){
		vlist.remove(0);
		{
			List<RomaAlist.RomaAlistEntry<String>> l = (List<RomaAlist.RomaAlistEntry<String>>)c.extension(alist.getEntrySubList(1,19));
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( RomaAlist.RomaAlistEntry<String> e : l ) {
	    		assertEquals(it.next(),e.getValue());
	    		assertTrue(e.getTime() > 0 );
			}
		}
    }
    public void testGetAndDeleteFirst(){
		vlist.remove(0);
		assertEquals("000",c.extension(alist.getAndDeleteFirst()));
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    public void testGetAndDeleteLast(){
		vlist.remove(19);
		assertEquals("019",c.extension(alist.getAndDeleteLast()));
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }

    public void testPrependConflict(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.prepend("010")));
		vlist.add(0,"010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
				System.err.println("**"+s);
	    		assertEquals(it.next(),s);
			}
		}
    }

    public void testInsertConflict(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insert(10,"010")));
		vlist.add(10,"010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
				System.err.println("**"+s);
	    		assertEquals(it.next(),s);
			}
		}
    }

    // TODO: Reflects first data only.
    public void testInsertOrMoveToFirstConflict(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.append("010")));
		vlist.add("010");
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToFirst("010")));
		vlist.remove(10);
		vlist.add(0,"010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }

    // TODO: Reflects first data only.
    public void testInsertOrMoveToLastConflict(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.append("010")));
		vlist.add("010");
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.insertOrMoveToLast("010")));
		vlist.remove(10);
		vlist.add("010");
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
	    		assertEquals(it.next(),s);
			}
		}
    }
    // TODO: Delete both conflicted datas. 
    public void testDeleteConflict(){
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.append("010")));
		assertEquals(Boolean.TRUE,(Boolean)c.extension(alist.delete("010")));
		vlist.remove(10);
		{
			List<String> l = (List<String>)c.extension(alist.getAll());
			Iterator<String> it = vlist.iterator();
			assertEquals(l.size(), vlist.size());
			for ( String s : l ) {
				System.err.println("**"+s);
	    		assertEquals(it.next(),s);
			}
		}
    }
    
    
    public void test(){
    	
    }

}
