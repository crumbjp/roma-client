package net.spy.memcached.ops;

public interface RomaRoutingdumpOperation extends Operation {
	interface Callback extends OperationCallback {
		void gotData(String data);
	}

}