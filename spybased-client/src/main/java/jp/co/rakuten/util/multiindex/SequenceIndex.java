package jp.co.rakuten.util.multiindex;

import java.util.List;

import jp.co.rakuten.util.UnmodifiableListWrapper;

public class SequenceIndex<T> extends UnmodifiableListWrapper<Container<T>> implements Index<T> , List<Container<T>>{
	public SequenceIndex() {
		// Nothing to do.
	}
	public void opInit   (List<Container<T>> origin,Integer size){
		this.container = origin;
	}
	public void opAdd(Container<T> c) {
		;
	}
	public void opRemove(Container<T> c){
		;
	}
	public void opModify(Container<T> c, T t) {
		;
	}
	public boolean opExist(T t) {
		return false;
	}
	public boolean opCheckModify(Container<T> c, T t) {
		return true;
	}
}
