package net.spy.memcached.protocol.ascii;


import net.spy.memcached.RomaOperationFactory;
import net.spy.memcached.ops.RomaMklhashOperation;
import net.spy.memcached.ops.RomaRoutingdumpOperation;

/**
 * Operation factory for the ascii protocol.
 */
public class RomaAsciiOperationFactory extends AsciiOperationFactory implements RomaOperationFactory{
	@Override
	public RomaMklhashOperation mklhash(RomaMklhashOperation.Callback cb) {
		return new RomaMklhashOpImpl(cb);
	}
	@Override
	public RomaRoutingdumpOperation routingdump(RomaRoutingdumpOperation.Callback cb) {
		return new RomaRoutingdumpImpl(cb);
	}

}
