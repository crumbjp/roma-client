package net.spy.memcached;

import net.spy.memcached.ops.RomaMklhashOperation;
import net.spy.memcached.ops.RomaRoutingdumpOperation;

/**
 * Factory that builds operations for protocol handlers.
 */
public interface RomaOperationFactory extends OperationFactory{

	RomaMklhashOperation mklhash(RomaMklhashOperation.Callback cb);
	RomaRoutingdumpOperation routingdump(RomaRoutingdumpOperation.Callback cb);
}
