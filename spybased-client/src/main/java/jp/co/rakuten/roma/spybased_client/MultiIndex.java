package jp.co.rakuten.roma.spybased_client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

interface Index <T>{
	void add(T t,List<T> l);
}
class Sequence<T> implements Index<T>{
	List l;
	public void add(T t,List<T> l) {
		this.l = l;
	};
}
class IndexBy<T> extends ArrayList<Index<T>>{
	List <Index<T>> list;
	public IndexBy(Index<T> ...i) {
		list = Arrays.asList(i);
	}
	public Index<T> get(int n) {
		return list.get(n);
	}
}

public class MultiIndex<T> {
	private IndexBy<T> indexBy;
	private ArrayList<T> dataContainer;
	public MultiIndex(IndexBy<T> indexBy) {
		this.indexBy = indexBy;
		this.dataContainer = new ArrayList<T>();
	}
	public Index<T> index(int n){
		return indexBy.get(n);
	}
	boolean add(T t){
		if ( ! this.dataContainer.add(t) )
			return false;
		for ( Index<T> index : indexBy ) {
			index.add(t, Collections.unmodifiableList(dataContainer));
		}
		return true;
	}
	public static void main(String[] args){
		MultiIndex<Data> mi = new MultiIndex<Data>(new IndexBy<Data>(new Sequence<Data>()));
		mi.add(new Data(1,"abc"));
		mi.add(new Data(3,"abc"));
		mi.add(new Data(2,"abc"));
		
		for (Data d : ){
			
		}
	}
}

class Data {
	int i;
	String s;
	public Data(int i , String s) {
		this.i = i;
		this.s = s;
	}
}
