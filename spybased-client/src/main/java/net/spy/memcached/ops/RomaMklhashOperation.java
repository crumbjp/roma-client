package net.spy.memcached.ops;

public interface RomaMklhashOperation extends Operation {
	interface Callback extends OperationCallback {
		void gotData(String data);
	}

}