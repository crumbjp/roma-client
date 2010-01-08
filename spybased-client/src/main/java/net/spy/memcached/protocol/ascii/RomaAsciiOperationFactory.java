package net.spy.memcached.protocol.ascii;


import net.spy.memcached.RomaOperationFactory;
import net.spy.memcached.ops.RomaMklhashOperation;
import net.spy.memcached.ops.RomaMklhashOperation.Callback;

/**
 * Operation factory for the ascii protocol.
 */
public class RomaAsciiOperationFactory extends AsciiOperationFactory implements RomaOperationFactory{

	public RomaMklhashOperation mklhash(Callback cb) {
		return new RomaMklhashOpImpl(cb);
	}

}
