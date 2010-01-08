package net.spy.memcached.ops;

public interface RomaMklhashOperation extends KeyedOperation {
	interface Callback extends OperationCallback {
		void gotData(String data);
	}

}