package jp.co.rakuten.roma.client.ops;

import net.spy.memcached.ops.Operation;
import net.spy.memcached.ops.OperationCallback;

public interface RomaMklhashOperation extends Operation {
	interface Callback extends OperationCallback {
		void gotData(String data);
	}

}