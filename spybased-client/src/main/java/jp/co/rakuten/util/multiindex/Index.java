package jp.co.rakuten.util.multiindex;
import java.util.List;

public interface Index <T>{
	public void opInit   (List<Container<T>> origin,Integer size);
	public void opAdd    (Container<T> c);
	public void opRemove (Container<T> c);
	public void opModify (Container<T> c,T t);
	public boolean opExist(T t);
	public boolean opCheckModify(Container<T> c,T t);
}
