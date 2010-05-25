package jp.co.rakuten.roma.spybased_client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;

import jp.co.rakuten.roma.client.RomaAlist;
import jp.co.rakuten.roma.client.RomaAlistImpl;
import jp.co.rakuten.roma.client.SimpleRomaClient;
import jp.co.rakuten.roma.client.SimpleRomaClientImpl;
import junit.framework.TestCase;

public class RomaTest extends TestCase {
	static SimpleRomaClient c = null;
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
    		c = new SimpleRomaClientImpl("localhost_11211","localhost_11212");
    	}
    	System.err.println("--construct--" );
    	System.err.println("--setdatas--");
    	
    	assertEquals(Boolean.TRUE,c.set("000",0,"000"));
    	assertEquals(Boolean.TRUE,c.set("001",0,"001"));
    	assertEquals(Boolean.TRUE,c.set("002",0,"002"));
    	assertEquals(Boolean.TRUE,c.set("003",0,"003"));
    	assertEquals(Boolean.TRUE,c.set("004",0,"004"));
    	assertEquals(Boolean.TRUE,c.set("005",0,"005"));
    	assertEquals(Boolean.TRUE,c.set("006",0,"006"));
    	assertEquals(Boolean.TRUE,c.set("007",0,"007"));
    	assertEquals(Boolean.TRUE,c.set("008",0,"008"));
    	assertEquals(Boolean.TRUE,c.set("009",0,"009"));
    	assertEquals(Boolean.TRUE,c.set("010",0,"010"));
    	assertEquals(Boolean.TRUE,c.set("011",0,"011"));
    	assertEquals(Boolean.TRUE,c.set("012",0,"012"));
    	assertEquals(Boolean.TRUE,c.set("013",0,"013"));
    	assertEquals(Boolean.TRUE,c.set("014",0,"014"));
    	assertEquals(Boolean.TRUE,c.set("015",0,"015"));
    	assertEquals(Boolean.TRUE,c.set("016",0,"016"));
    	assertEquals(Boolean.TRUE,c.set("017",0,"017"));
    	assertEquals(Boolean.TRUE,c.set("018",0,"018"));
    	assertEquals(Boolean.TRUE,c.set("019",0,"019"));
    	c.delete("---");
    	c.delete("020");
    	c.delete("021");
    	super.setUp();
    }
	@Override
	protected void tearDown() throws Exception {
		//Thread.sleep(2000);
    	//c.shutdown(); c=null;
		super.tearDown();
	}    

    public void testDelete() throws InterruptedException{
    	assertEquals(Boolean.TRUE,c.delete("010"));
    	assertEquals(Boolean.TRUE,c.delete("011",(long)1000000));
    	assertEquals("000",c.get("000"));
    	assertEquals("001",c.get("001"));
    	assertEquals("002",c.get("002"));
    	assertEquals("003",c.get("003"));
    	assertEquals("004",c.get("004"));
    	assertEquals("005",c.get("005"));
    	assertEquals("006",c.get("006"));
    	assertEquals("007",c.get("007"));
    	assertEquals("008",c.get("008"));
    	assertEquals("009",c.get("009"));
    	assertNull(c.get("010"));
    	assertNull(c.get("011"));
    	assertEquals("012",c.get("012"));
    	assertEquals("013",c.get("013"));
    	assertEquals("014",c.get("014"));
    	assertEquals("015",c.get("015"));
    	assertEquals("016",c.get("016"));
    	assertEquals("017",c.get("017"));
    	assertEquals("018",c.get("018"));
    	assertEquals("019",c.get("019"));
    }
    public void testPrependAppend() throws InterruptedException{
    	assertEquals(Boolean.TRUE,c.prepend(0, "008", "p"));
    	assertEquals(Boolean.TRUE,c.prepend(0, "009", "p",1000));
    	assertEquals(Boolean.TRUE,c.append(0, "010", "a"));
    	assertEquals(Boolean.TRUE,c.append(0, "011", "a",1000));
    	assertEquals("000",c.get("000"));
    	assertEquals("001",c.get("001"));
    	assertEquals("002",c.get("002"));
    	assertEquals("003",c.get("003"));
    	assertEquals("004",c.get("004"));
    	assertEquals("005",c.get("005"));
    	assertEquals("006",c.get("006"));
    	assertEquals("007",c.get("007"));
    	assertEquals("p008",c.get("008"));
    	assertEquals("p009",c.get("009"));
    	assertEquals("010a",c.get("010"));
    	assertEquals("011a",c.get("011"));
    	assertEquals("012",c.get("012"));
    	assertEquals("013",c.get("013"));
    	assertEquals("014",c.get("014"));
    	assertEquals("015",c.get("015"));
    	assertEquals("016",c.get("016"));
    	assertEquals("017",c.get("017"));
    	assertEquals("018",c.get("018"));
    	assertEquals("019",c.get("019"));
    }
    public void testCas() throws InterruptedException{
    	CASValue<Object> cas1 = c.gets("010");
    	assertEquals(CASResponse.EXISTS,c.cas("010", cas1.getCas()-1, "0010"));
    	assertEquals(CASResponse.OK,c.cas("010", cas1.getCas(), "0010"));
    	CASValue<Object> cas2 = c.gets("011",(long)1000);
    	assertEquals(CASResponse.EXISTS,c.cas("011", cas2.getCas()-1, "0011",(long)1000));
    	assertEquals(CASResponse.OK,c.cas("011", cas2.getCas(), "0011",(long)1000));
    	assertEquals("000",c.get("000"));
    	assertEquals("001",c.get("001"));
    	assertEquals("002",c.get("002"));
    	assertEquals("003",c.get("003"));
    	assertEquals("004",c.get("004"));
    	assertEquals("005",c.get("005"));
    	assertEquals("006",c.get("006"));
    	assertEquals("007",c.get("007"));
    	assertEquals("008",c.get("008"));
    	assertEquals("009",c.get("009"));
    	assertEquals("0010",c.get("010"));
    	assertEquals("0011",c.get("011"));
    	assertEquals("012",c.get("012"));
    	assertEquals("013",c.get("013"));
    	assertEquals("014",c.get("014"));
    	assertEquals("015",c.get("015"));
    	assertEquals("016",c.get("016"));
    	assertEquals("017",c.get("017"));
    	assertEquals("018",c.get("018"));
    	assertEquals("019",c.get("019"));
    }

    public void testDecr() throws InterruptedException{
    	assertEquals(7,c.decr("009", 2));  // nomal
    	assertEquals(0,c.decr("010", 20)); // under flow
    	assertEquals(-1,c.decr("020", 2)); // not found
    	assertEquals(20,c.decr("020", 2,(long)20)); // default
    	assertEquals(10,c.decr("012", 2,(long)20,(long)1000)); // default
    	Thread.sleep(2000);
    	assertEquals("000",c.get("000"));
    	assertEquals("001",c.get("001"));
    	assertEquals("002",c.get("002"));
    	assertEquals("003",c.get("003"));
    	assertEquals("004",c.get("004"));
    	assertEquals("005",c.get("005"));
    	assertEquals("006",c.get("006"));
    	assertEquals("007",c.get("007"));
    	assertEquals("008",c.get("008"));
    	assertEquals("7",c.get("009"));
    	assertEquals("0",c.get("010"));
    	assertEquals("011",c.get("011"));
    	assertEquals("10",c.get("012"));
    	assertEquals("013",c.get("013"));
    	assertEquals("014",c.get("014"));
    	assertEquals("015",c.get("015"));
    	assertEquals("016",c.get("016"));
    	assertEquals("017",c.get("017"));
    	assertEquals("018",c.get("018"));
    	assertEquals("019",c.get("019"));
    	assertEquals("20",c.get("020"));
    }
    public void testIncr() throws InterruptedException{
    	assertEquals(11,c.incr("009", 2));  // nomal
    	assertEquals(0,c.incr("010", -20)); // under flow
    	assertEquals(-1,c.incr("020", 2)); // not found
    	assertEquals(20,c.incr("020", 2,(long)20)); // default
    	assertEquals(14,c.incr("012", 2,(long)20,(long)1000)); // default
    	Thread.sleep(2000);
    	assertEquals("000",c.get("000"));
    	assertEquals("001",c.get("001"));
    	assertEquals("002",c.get("002"));
    	assertEquals("003",c.get("003"));
    	assertEquals("004",c.get("004"));
    	assertEquals("005",c.get("005"));
    	assertEquals("006",c.get("006"));
    	assertEquals("007",c.get("007"));
    	assertEquals("008",c.get("008"));
    	assertEquals("11",c.get("009"));
    	assertEquals("0",c.get("010"));
    	assertEquals("011",c.get("011"));
    	assertEquals("14",c.get("012"));
    	assertEquals("013",c.get("013"));
    	assertEquals("014",c.get("014"));
    	assertEquals("015",c.get("015"));
    	assertEquals("016",c.get("016"));
    	assertEquals("017",c.get("017"));
    	assertEquals("018",c.get("018"));
    	assertEquals("019",c.get("019"));
    	assertEquals("20",c.get("020"));
    }
    public void testGetBulk() throws InterruptedException{
    	Map<String,Object> r = c.getBulk(	"000","001","002","003","004","005","006","007","008","009",
    											"010","011","012","013","014","015","016","017","018","019");
    	assertEquals("001",r.get("001"));
    	assertEquals("002",r.get("002"));
    	assertEquals("003",r.get("003"));
    	assertEquals("004",r.get("004"));
    	assertEquals("005",r.get("005"));
    	assertEquals("006",r.get("006"));
    	assertEquals("007",r.get("007"));
    	assertEquals("008",r.get("008"));
    	assertEquals("009",r.get("009"));
    	assertEquals("010",r.get("010"));
    	assertEquals("011",r.get("011"));
    	assertEquals("012",r.get("012"));
    	assertEquals("013",r.get("013"));
    	assertEquals("014",r.get("014"));
    	assertEquals("015",r.get("015"));
    	assertEquals("016",r.get("016"));
    	assertEquals("017",r.get("017"));
    	assertEquals("018",r.get("018"));
    	assertEquals("019",r.get("019"));
    }
    public void testSetWithExpire() throws InterruptedException{
    	assertEquals(Boolean.TRUE,c.set("000",1,"0000",(long)1000));
    	assertEquals("0000",c.get("000",(long)1000));
    	assertEquals(Boolean.TRUE,c.set("020",1,"0020"));
    	assertEquals("0020",c.get("020"));
    	Thread.sleep(2000);
    	
    	assertNull(c.get("000"));
    	assertEquals("001",c.get("001"));
    	assertEquals("002",c.get("002"));
    	assertEquals("003",c.get("003"));
    	assertEquals("004",c.get("004"));
    	assertEquals("005",c.get("005"));
    	assertEquals("006",c.get("006"));
    	assertEquals("007",c.get("007"));
    	assertEquals("008",c.get("008"));
    	assertEquals("009",c.get("009"));
    	assertEquals("010",c.get("010"));
    	assertEquals("011",c.get("011"));
    	assertEquals("012",c.get("012"));
    	assertEquals("013",c.get("013"));
    	assertEquals("014",c.get("014"));
    	assertEquals("015",c.get("015"));
    	assertEquals("016",c.get("016"));
    	assertEquals("017",c.get("017"));
    	assertEquals("018",c.get("018"));
    	assertEquals("019",c.get("019"));
    	assertNull(c.get("020"));
    }    

    public void testAddWithExpire() throws InterruptedException{
    	assertEquals(Boolean.FALSE,c.add("000",1,"0000",(long)1000));
    	assertEquals("000",c.get("000",(long)1000));
    	assertEquals(Boolean.TRUE,c.add("020",1,"0020"));
    	assertEquals("0020",c.get("020"));
    	Thread.sleep(2000);
    	
    	assertEquals("000",c.get("000"));
    	assertEquals("001",c.get("001"));
    	assertEquals("002",c.get("002"));
    	assertEquals("003",c.get("003"));
    	assertEquals("004",c.get("004"));
    	assertEquals("005",c.get("005"));
    	assertEquals("006",c.get("006"));
    	assertEquals("007",c.get("007"));
    	assertEquals("008",c.get("008"));
    	assertEquals("009",c.get("009"));
    	assertEquals("010",c.get("010"));
    	assertEquals("011",c.get("011"));
    	assertEquals("012",c.get("012"));
    	assertEquals("013",c.get("013"));
    	assertEquals("014",c.get("014"));
    	assertEquals("015",c.get("015"));
    	assertEquals("016",c.get("016"));
    	assertEquals("017",c.get("017"));
    	assertEquals("018",c.get("018"));
    	assertEquals("019",c.get("019"));
    	assertNull(c.get("020"));
    }    
    
    public void test(){
    	
    }

}
