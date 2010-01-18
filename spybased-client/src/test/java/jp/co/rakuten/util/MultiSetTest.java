package jp.co.rakuten.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import junit.framework.TestCase;

class MultiSetData implements Comparable<MultiSetData>{
	static Integer GenUnique = 0;
	public final Integer key;
	public final Integer unique;
	public MultiSetData(Integer key) {
		this.key = key;
		this.unique = ++GenUnique;
	}
	@Override
	public int compareTo(MultiSetData o) {
		return this.key.compareTo(o.key);
	}
	public void dump(){
		System.out.println(String.format("Key : %d , Unique : %d",key,unique));
	}
}
public class MultiSetTest extends TestCase {
	MultiSet<MultiSetData> ms = new MultiSet<MultiSetData>();
	ArrayList<Integer> uniqueOrder = new ArrayList<Integer>();
	@Override
	protected void tearDown() throws Exception {
		ms.clear();
		MultiSetData.GenUnique = 0;
		super.tearDown();
	}
	@Override
	protected void setUp() throws Exception {
		ms.add(new MultiSetData(3)); // Unique = 1
		ms.add(new MultiSetData(1)); // Unique = 2
		ms.add(new MultiSetData(2)); // Unique = 3
		ms.add(new MultiSetData(5)); // Unique = 4
		ms.add(new MultiSetData(4)); // Unique = 5
		ms.add(new MultiSetData(3)); // Unique = 6
		uniqueOrder.add(2);
		uniqueOrder.add(3);
		uniqueOrder.add(1);
		uniqueOrder.add(6);
		uniqueOrder.add(5);
		uniqueOrder.add(4);
		
		super.setUp();
	}
	public void testIterator() {
		Iterator<Integer> exp = uniqueOrder.iterator();
		for ( MultiSetData d : ms ) {
			assertEquals(exp.next(), d.unique);
		}
		assertFalse(exp.hasNext());
	}

	public void testDescendingIterator() {
		Collections.reverse(uniqueOrder);
		Iterator<Integer> exp = uniqueOrder.iterator();
		for ( MultiSetData d : new Iterable<MultiSetData>(){
			@Override
			public Iterator<MultiSetData> iterator() {
				return ms.descendingIterator();
			}
		}) {
			assertEquals(exp.next(), d.unique);
		}
		assertFalse(exp.hasNext());
	}

	public void testClear() {
		ms.clear();
		for ( MultiSetData d : ms ) {
			assertTrue("Should be empty",false);
		}
		assertTrue(ms.isEmpty());
		assertEquals(0, ms.size());
	}

	public void testContains() {
		assertTrue(ms.contains(new MultiSetData(1)));
		assertTrue(ms.contains(new MultiSetData(2)));
		assertTrue(ms.contains(new MultiSetData(3)));
		assertTrue(ms.contains(new MultiSetData(4)));
		assertTrue(ms.contains(new MultiSetData(5)));
		assertFalse(ms.contains(new MultiSetData(0)));
		assertFalse(ms.contains(new MultiSetData(6)));
		ArrayList<MultiSetData> cs= new ArrayList<MultiSetData>();
		cs.add(new MultiSetData(1));
		cs.add(new MultiSetData(2));
		cs.add(new MultiSetData(3));
		cs.add(new MultiSetData(4));
		cs.add(new MultiSetData(5));
		assertTrue(ms.containsAll(cs));
		cs.add(new MultiSetData(6));
		assertFalse(ms.containsAll(cs));
	}

	public void testAddAll() {
		ArrayList<MultiSetData> cs= new ArrayList<MultiSetData>();
		cs.add(new MultiSetData(2)); // Unique = 7
		cs.add(new MultiSetData(3)); // Unique = 8
		cs.add(new MultiSetData(4)); // Unique = 9
		cs.add(new MultiSetData(5)); // Unique = 10
		cs.add(new MultiSetData(6)); // Unique = 11
		ms.addAll(cs);
		uniqueOrder.add(2,7);
		uniqueOrder.add(5,8);
		uniqueOrder.add(7,9);
		uniqueOrder.add(9,10);
		uniqueOrder.add(10,11);
		Iterator<Integer> exp = uniqueOrder.iterator();
		for ( MultiSetData d : ms ) {
			assertEquals(exp.next(), d.unique);
		}
		assertFalse(exp.hasNext());
	}

	public void testPollFirst1() {
		ms.add(new MultiSetData(0)); // Unique = 7
		assertEquals(new Integer(7), ms.pollFirst().unique);
		Iterator<Integer> exp = uniqueOrder.iterator();
		for ( MultiSetData d : ms ) {
			assertEquals(exp.next(), d.unique);
		}
		assertFalse(exp.hasNext());
	}
	public void testPollFirst2() {
		ms.add(new MultiSetData(1)); // Unique = 7
		uniqueOrder.set(0, 7);
		assertEquals(new Integer(2), ms.pollFirst().unique);
		
		Iterator<Integer> exp = uniqueOrder.iterator();
		for ( MultiSetData d : ms ) {
			assertEquals(exp.next(), d.unique);
		}
		assertFalse(exp.hasNext());
	}
	public void testPollLast1() {
		ms.add(new MultiSetData(6)); // Unique = 7
		assertEquals(new Integer(7), ms.pollLast().unique);
		Iterator<Integer> exp = uniqueOrder.iterator();
		for ( MultiSetData d : ms ) {
			assertEquals(exp.next(), d.unique);
		}
		assertFalse(exp.hasNext());
	}
	public void testPollLast2() {
		ms.add(new MultiSetData(5)); // Unique = 7
		assertEquals(new Integer(7), ms.pollLast().unique);
		
		Iterator<Integer> exp = uniqueOrder.iterator();
		for ( MultiSetData d : ms ) {
			assertEquals(exp.next(), d.unique);
		}
		assertFalse(exp.hasNext());
	}
	
	public void testFirst() {
		assertEquals(new Integer(2), ms.first().unique);
	}

	public void testLast() {
		assertEquals(new Integer(4), ms.last().unique);
	}

	public void testCeiling() {
		assertEquals(new Integer(1), ms.ceiling(new MultiSetData(3)).unique);
		assertEquals(new Integer(5), ms.ceiling(new MultiSetData(4)).unique);
	}

	public void testFloor() {
		assertEquals(new Integer(6), ms.floor(new MultiSetData(3)).unique);
		assertEquals(new Integer(5), ms.floor(new MultiSetData(4)).unique);
	}

	public void testHigher() {
		assertEquals(new Integer(5), ms.higher(new MultiSetData(3)).unique);
		assertEquals(new Integer(1), ms.higher(new MultiSetData(2)).unique);
		assertNull(ms.higher(new MultiSetData(5)));
	}

	public void testLower() {
		assertEquals(new Integer(6), ms.lower(new MultiSetData(4)).unique);
		assertEquals(new Integer(3), ms.lower(new MultiSetData(3)).unique);
		assertNull(ms.lower(new MultiSetData(1)));
	}
	public void testRemove1() {
		Iterator<MultiSetData> it = ms.iterator();
		it.next();
		it.next();
		it.remove(); // 2 - 3
		it.next();
		it.remove(); // 3 - 1
		it.next();
		it.remove(); // 3 - 6
		it.next();
		it.next();
		it.remove(); // 5 - 4
		uniqueOrder.clear();
		uniqueOrder.add(new Integer(2));
		uniqueOrder.add(new Integer(5));
		Iterator<Integer> exp = uniqueOrder.iterator();
		for ( MultiSetData d : ms ) {
			assertEquals(exp.next(), d.unique);
		}
		assertFalse(exp.hasNext());
	}
	public void testRemove2() {
		Iterator<MultiSetData> it = ms.descendingIterator();
		it.next();
		it.next();
		it.remove(); // 4 - 5
		it.next();
		it.remove(); // 3 - 6
		it.next();
		it.remove(); // 3 - 1
		it.next();
		it.next();
		it.remove(); // 1 - 2
		uniqueOrder.clear();
		uniqueOrder.add(new Integer(4));
		uniqueOrder.add(new Integer(3));
		Iterator<Integer> exp = uniqueOrder.iterator();
		for ( MultiSetData d : new Iterable<MultiSetData>() {
			public Iterator<MultiSetData> iterator() {
				return ms.descendingIterator();
			}
		}) {
			assertEquals(exp.next(), d.unique);
		}
		assertFalse(exp.hasNext());
	}
	public void testRemove3() {
		Iterator<MultiSetData> it = ms.iterator();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		assertTrue(ms.isEmpty());
	}
	public void testRemove4() {
		Iterator<MultiSetData> it = ms.descendingIterator();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		assertTrue(ms.isEmpty());
	}
	public void testEqualRange1() {
		uniqueOrder.clear();
		uniqueOrder.add(1);
		uniqueOrder.add(6);
		Iterator<Integer> exp = uniqueOrder.iterator();
		for ( MultiSetData d : new Iterable<MultiSetData>() {
			public Iterator<MultiSetData> iterator() {
				return ms.equalRange(new MultiSetData(3));
			}
		}) {
			assertEquals(exp.next(), d.unique);
		}
		assertFalse(exp.hasNext());
	}
	public void testEqualRange2() {
		uniqueOrder.clear();
		uniqueOrder.add(3);
		uniqueOrder.add(1);
		uniqueOrder.add(6);
		uniqueOrder.add(5);
		Iterator<Integer> exp = uniqueOrder.iterator();
		for ( MultiSetData d : new Iterable<MultiSetData>() {
			public Iterator<MultiSetData> iterator() {
				return ms.equalRange(new MultiSetData(2),new MultiSetData(4));
			}
		}) {
				assertEquals(exp.next(), d.unique);
		}
		assertFalse(exp.hasNext());
	}
}
