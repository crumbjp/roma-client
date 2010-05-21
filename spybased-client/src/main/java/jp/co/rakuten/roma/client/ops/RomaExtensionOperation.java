package jp.co.rakuten.roma.client.ops;

import net.spy.memcached.ops.Operation;
import net.spy.memcached.ops.OperationCallback;

public interface RomaExtensionOperation extends Operation {
	interface Callback extends OperationCallback {
		void gotData(int flg,byte[] data);
	}
}