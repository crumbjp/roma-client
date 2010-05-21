package jp.co.rakuten.roma.client;

import net.spy.memcached.internal.OperationFuture;
import jp.co.rakuten.roma.client.ops.RomaExtensionOperation;

public interface RomaExtension {
	static enum OperationType{
		BROADCAST,
		RANDOM,
		KEY,
	};
	Pair<RomaExtensionOperation,OperationFuture<Object>> getOperation(long timeout);
	String getKey();
	OperationType getType();
}
