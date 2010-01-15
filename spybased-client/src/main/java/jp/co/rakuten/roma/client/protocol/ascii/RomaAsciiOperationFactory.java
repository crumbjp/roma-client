package jp.co.rakuten.roma.client.protocol.ascii;


import net.spy.memcached.protocol.ascii.AsciiOperationFactory;
import jp.co.rakuten.roma.client.RomaOperationFactory;
import jp.co.rakuten.roma.client.ops.RomaMklhashOperation;
import jp.co.rakuten.roma.client.ops.RomaRoutingdumpOperation;

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
