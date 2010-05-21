package jp.co.rakuten.roma.client;

import jp.co.rakuten.roma.client.ops.RomaExtensionOperation;
import net.spy.memcached.CachedData;
import net.spy.memcached.internal.OperationFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import net.spy.memcached.ops.OperationStatus;
import net.spy.memcached.transcoders.Transcoder;

import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistClearOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistDeleteAtOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistDeleteOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistGetOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistGetsOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistGetsTimeOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistInsertOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistLengthOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistPopOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistPushOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistShiftOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistSizedInsertOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistSizedPushOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistSwapInsertOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistSwapPushOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistSwapSizedInsertOpImpl;
import jp.co.rakuten.roma.client.protocol.ascii.RomaAlistSwapSizedPushOpImpl;

/**
 * Roma extension.
 * 
 * @author hiroaki.kubota@mail.rakuten.co.jp
 *
 * @param <T> Data type of the list
 */
public class RomaAlistImpl<T> implements RomaAlist<T>{
	private final String key;
	private Transcoder<Object> transcoder;

	public RomaAlistImpl(String k,Transcoder<Object> tc) {
		key = k;
		transcoder = tc;
	}
	@Override
	public void setTranscoder(Transcoder<Object> tc) {
		transcoder = tc;
	}

	@Override
	public void setExpire(long exp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RomaExtensionAlist delete(T val) {
		final CachedData data = transcoder.encode(val);
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistDeleteOpImpl(
								new RomaExtensionOperation.Callback() {
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(status.isSuccess());
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
									}
								},key,data.getData()
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist deleteAt(final Integer index) {
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistDeleteAtOpImpl(
								new RomaExtensionOperation.Callback() {
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(status.isSuccess());
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
									}
								},key,index
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist deleteAll() {
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistClearOpImpl(
								new RomaExtensionOperation.Callback() {
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(status.isSuccess());
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
									}
								},key
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist get(final Integer index) {
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistGetOpImpl(
								new RomaExtensionOperation.Callback() {
									private T val = null;
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(val);
									}
									@Override
									public void gotData(int f,byte[] d) {
										val = (T)transcoder.decode(new CachedData(f, d,transcoder.getMaxSize()));
									}
									@Override
									public void complete() {
										latch.countDown();
									}
								},key,index
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist getAndDeleteFirst() {
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistShiftOpImpl(
								new RomaExtensionOperation.Callback() {
									private T val = null;
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(val);
									}
									@Override
									public void gotData(int f,byte[] d) {
										val = (T)transcoder.decode(new CachedData(f, d,transcoder.getMaxSize()));
									}
									@Override
									public void complete() {
										latch.countDown();
									}
								},key
						),rv);
			}
		};
	}
	
	@Override
	public RomaExtensionAlist getAndDeleteLast() {
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistPopOpImpl(
								new RomaExtensionOperation.Callback() {
									private T val = null;
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(val);
									}
									@Override
									public void gotData(int f,byte[] d) {
										val = (T)transcoder.decode(new CachedData(f, d,transcoder.getMaxSize()));
									}
									@Override
									public void complete() {
										latch.countDown();
									}
								},key
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist getAll() {
		return getSubList(null, null);
	}

	@Override
	public RomaExtensionAlist getSubList(final Integer index, final Integer len) {
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistGetsOpImpl(
								new RomaExtensionOperation.Callback() {
									private List<T> list = null;
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(list);
									}
									@Override
									public void gotData(int f,byte[] d) {
										if ( list == null) {
											list = new ArrayList<T>();
										}else {
											list.add((T)transcoder.decode(new CachedData(f, d,transcoder.getMaxSize())));
										}
									}
									@Override
									public void complete() {
										latch.countDown();
									}
								},key,index,len
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist insertOrMoveToFirst(T val) {
		final CachedData data = transcoder.encode(val);
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistSwapInsertOpImpl(
								new RomaExtensionOperation.Callback() {
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(status.isSuccess());
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
									}
								},key,data.getData()
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist insertOrMoveToFirst(T val,final Integer limitSize) {
		final CachedData data = transcoder.encode(val);
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistSwapSizedInsertOpImpl(
								new RomaExtensionOperation.Callback() {
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(status.isSuccess());
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
									}
								},key,limitSize,data.getData()
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist insertOrMoveToLast(T val) {
		final CachedData data = transcoder.encode(val);
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistSwapPushOpImpl(
								new RomaExtensionOperation.Callback() {
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(status.isSuccess());
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
									}
								},key,data.getData()
						),rv);
			}
		};
	}
	@Override
	public RomaExtensionAlist insertOrMoveToLast(T val,final Integer limitSize) {
		final CachedData data = transcoder.encode(val);
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistSwapSizedPushOpImpl(
								new RomaExtensionOperation.Callback() {
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(status.isSuccess());
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
									}
								},key,limitSize,data.getData()
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist append(T val) {
		final CachedData data = transcoder.encode(val);
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistPushOpImpl(
								new RomaExtensionOperation.Callback() {
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(status.isSuccess());
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
									}
								},key,data.getData()
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist append(T val,final Integer limitSize) {
		final CachedData data = transcoder.encode(val);
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistSizedPushOpImpl(
								new RomaExtensionOperation.Callback() {
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(status.isSuccess());
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
									}
								},key,limitSize,data.getData()
						),rv);
			}
		};
	}

	public RomaExtensionAlist insert(final Integer index, T val) {
		final CachedData data = transcoder.encode(val);
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistInsertOpImpl(
								new RomaExtensionOperation.Callback() {
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(status.isSuccess());
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
									}
								},key,index,data.getData()
						),rv);
			}
		};
	}
	@Override
	public RomaExtensionAlist prepend(T val) {
		return insert(0, val);
	}
	@Override
	public RomaExtensionAlist prepend(T val,final Integer limitSize) {
		final CachedData data = transcoder.encode(val);
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistSizedInsertOpImpl(
								new RomaExtensionOperation.Callback() {
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(status.isSuccess());
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
									}
								},key,limitSize,data.getData()
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist size() {
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistLengthOpImpl(
								new RomaExtensionOperation.Callback() {
									Integer val = null;
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(val);
									}
									@Override
									public void complete() {
										latch.countDown();
									}
									@Override
									public void gotData(int flg,byte[] data) {
										val = Integer.parseInt(new String(data));
									}
								},key
						),rv);
			}
		};
	}

	@Override
	public RomaExtensionAlist getEntryAll() {
		return getEntrySubList(null, null);
	}

	@Override
	public RomaExtensionAlist getEntrySubList(final Integer index,final Integer len) {
		return new RomaExtensionAlist(key) {
			@Override
			public Pair<RomaExtensionOperation, OperationFuture<Object>> getOperation(long timeout) {
				final CountDownLatch latch=new CountDownLatch(1);
				final OperationFuture<Object> rv=new OperationFuture<Object>(latch, timeout);
				return new Pair<RomaExtensionOperation, OperationFuture<Object>>(
						new RomaAlistGetsTimeOpImpl(
								new RomaExtensionOperation.Callback() {
									private List<RomaAlist.RomaAlistEntry<T>> list = null;
									private RomaAlistEntryImpl<T> current = null;
									@Override
									public void receivedStatus(OperationStatus status) {
										rv.set(list);
									}
									@Override
									public void gotData(int f,byte[] d) {
										if ( list == null) {
											list = new ArrayList<RomaAlist.RomaAlistEntry<T>>();
										}else {
											if ( current == null ) {
												current = new RomaAlistEntryImpl<T>();
												current.val = (T)transcoder.decode(new CachedData(f, d,transcoder.getMaxSize())); 
											} else {
												current.time = Long.parseLong((String)transcoder.decode(new CachedData(f, d,transcoder.getMaxSize())));
												list.add(current);
												current = null;
											}
										}
									}
									@Override
									public void complete() {
										latch.countDown();
									}
								},key,index,len
						),rv);
			}
		};
	}
	
	static class RomaAlistEntryImpl<T> implements RomaAlist.RomaAlistEntry<T> {
		public long time;
		public T val;
		@Override
		public Long getTime() {
			return time;
		}
		@Override
		public T getValue() {
			return val;
		}
	}
}
