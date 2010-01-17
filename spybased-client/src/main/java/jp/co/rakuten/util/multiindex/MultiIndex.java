package jp.co.rakuten.util.multiindex;

import java.util.ArrayList;
import java.util.Map;

public class MultiIndex<T> {
	private IndexBy<T> indexBy;
	private ArrayList<Container<T> > dataContainer;
	private final Integer size;
	private final static Integer DEFAULT_SIZE = 4096;
	
	public MultiIndex(IndexBy<T> indexBy ) {
		this.size = DEFAULT_SIZE;
		this.indexBy = indexBy;
		this.dataContainer = new ArrayList<Container<T> >();
		for ( Index<T> index : indexBy )
			index.opInit(this.dataContainer,this.size);
	}
	public MultiIndex(IndexBy<T> indexBy , Integer size ) {
		this.size = size;
		this.indexBy = indexBy;
		this.dataContainer = new ArrayList<Container<T> >();
		for ( Index<T> index : indexBy )
			index.opInit(this.dataContainer,this.size);
	}
	
	public Index<T> index(int n){
		return indexBy.get(n);
	}
	void add(T t) {
		Container<T> c = new Container<T>(t);
		for ( Index<T> index : indexBy )
			index.opAdd(c);
		this.dataContainer.add(c);
	}
	boolean safeAdd(T t) {
		for ( Index<T> index : indexBy )
			if ( index.opExist(t) )
				return false;
		this.add(t);
		return true;
	}
	public void remove(Container<T> c){
		for ( Index<T> index : indexBy ) 
			index.opRemove(c);
		this.dataContainer.remove(c);
	}
	public void modify(Container<T> c , T t) {
		for ( Index<T> index : indexBy ) 
			index.opModify(c,t);
		c.pair.second = t;
	}
	public boolean safeModify(Container<T> c , T t) {
		for ( Index<T> index : indexBy )
			if ( ! index.opCheckModify(c, t) )
				return false;
		this.modify(c,t);
		return true;
	}
	public static void main(String[] args) throws Throwable{

		MultiIndex<Data> mi = new MultiIndex<Data>(
					new IndexBy<Data>(
							new SequenceIndex<Data>(),
							new TreedUniqueIndex<Integer, Data>(Data.class.getField("i")),
							new HashedUniqueIndex<Long, Data>(Data.class.getField("l")),
							new IdentityIndex<Data>()
							)
					);
		mi.add(new Data(9,6l,"abc"));
		mi.add(new Data(8,3l,"efg"));
		mi.add(new Data(7,2l,"hij"));
		mi.add(new Data(6,5l,"klm"));
		mi.add(new Data(5,8l,"nop"));
		mi.add(new Data(4,7l,"qrs"));
		mi.add(new Data(3,1l,"tuv"));
		mi.add(new Data(2,9l,"wxy"));
		mi.add(new Data(1,4l,"z"));
		
		System.out.println("Safe add (false) : "+mi.safeAdd(new Data(22,9l,"WXY")));
		System.out.println("Safe add (true) : "+mi.safeAdd(new Data(22,99l,"WXY")));

		Container<Data> c3 = ((UniqueIndex<Integer,Data>)mi.index(1)).get(1);
		System.out.println("Safe modify (false) : "+mi.safeModify(c3,new Data(2,4l,"Z")));
		System.out.println("Safe modify (true) : "+mi.safeModify(c3,new Data(11,4l,"Z")));
		Container<Data> c4 = ((UniqueIndex<Integer,Data>)mi.index(1)).get(11);
		System.out.println("Safe modify (true) : "+mi.safeModify(c4,new Data(111,4l,"Z")));
		
		Container<Data> c1 = ((UniqueIndex<Integer,Data>)mi.index(1)).get(6);
		mi.remove(c1);
		Container<Data> c2 = ((UniqueIndex<Integer,Data>)mi.index(1)).get(5);
		mi.modify(c2, new Data(55,88l,"NOP"));
		
		System.out.println("Sequence Test");
		SequenceIndex<Data> sequence = (SequenceIndex<Data>)mi.index(0);
		for (Container<Data> c : sequence){
			c.get().dump();
		}
		System.out.println("Unique (first) Test");
		UniqueIndex<Integer,Data> unique1 = (UniqueIndex<Integer,Data>)mi.index(1);
		for (Map.Entry<Integer,Container<Data>> p : unique1.entrySet() ){
			p.getValue().get().dump();
		}
		System.out.println("Unique (second) Test");
		UniqueIndex<Long,Data> unique2 = (UniqueIndex<Long,Data>)mi.index(2);
		for (Map.Entry<Long,Container<Data>> p : unique2.entrySet() ){
			p.getValue().get().dump();
		}
		System.out.println("Identity (first) Test");
		IdentityIndex<Data> identity1 = (IdentityIndex<Data>)mi.index(3);
		for (Data c : identity1.keySet() ){
			identity1.get(c).get().dump();
		}
	}
}

class Data implements Comparable<Data> {
	public Integer i;
	public Long l;
	public String s;
	public Data(Integer i ,Long l, String s) {
		this.i = i;
		this.l = l;
		this.s = s;
	}
	public void dump(){
		System.out.println(String.format("I:%d - L:%d - S:%s", i,l,s));
	}
	@Override
	public boolean equals (Object d) {
		return s.equals(((Data)d).s);
	}
	@Override
	public int compareTo(Data d) {
		return s.compareTo(d.s);
	}
}
