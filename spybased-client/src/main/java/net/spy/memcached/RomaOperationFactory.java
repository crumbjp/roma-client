package net.spy.memcached;

import net.spy.memcached.ops.RomaMklhashOperation;

/**
 * Factory that builds operations for protocol handlers.
 */
public interface RomaOperationFactory extends OperationFactory{

	RomaMklhashOperation mklhash(RomaMklhashOperation.Callback cb);

}
