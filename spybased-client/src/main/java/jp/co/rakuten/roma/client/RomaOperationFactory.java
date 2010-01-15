package jp.co.rakuten.roma.client;

import jp.co.rakuten.roma.client.ops.RomaMklhashOperation;
import jp.co.rakuten.roma.client.ops.RomaRoutingdumpOperation;
import net.spy.memcached.OperationFactory;

/**
 * Factory that builds operations for protocol handlers.
 */
public interface RomaOperationFactory extends OperationFactory{

	RomaMklhashOperation mklhash(RomaMklhashOperation.Callback cb);
	RomaRoutingdumpOperation routingdump(RomaRoutingdumpOperation.Callback cb);
}
