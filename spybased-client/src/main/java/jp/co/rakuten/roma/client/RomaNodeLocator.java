package jp.co.rakuten.roma.client;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.spy.memcached.MemcachedNode;
import net.spy.memcached.NodeLocator;
import net.spy.memcached.compat.SpyObject;

/**
 * NodeLocator implementation for dealing with simple array lookups using a
 * modulus of the hash code and node list length.
 */
public final class RomaNodeLocator extends SpyObject implements NodeLocator {

	static class Capability {
		int digestBits;
		int maskBits;
		int rn;
		public Capability(int digestBits,int maskBits,int rn){
			this.digestBits = digestBits;
			this.maskBits = maskBits;
			this.rn = rn;
		}
		public int calcVirtualNodeIndex(long n) {
			return (int)((n << (64-this.digestBits)) >>> (this.digestBits - this.maskBits + (64-this.digestBits)));
		}
	}
	Capability capability = null;
	public int getRn(){
		if ( capability == null ){
			return 0;
		}
		return capability.rn;
	}
	Map<String,MemcachedNode> constNodeMap = null;
	ArrayList<ArrayList<MemcachedNode> > virtualNodes = null;
	/**
	 * Construct an ArraymodNodeLocator over the given array of nodes and
	 * using the given hash algorithm.
	 *
	 * @param n the array of nodes
	 * @param alg the hash algorithm
	 */
	public RomaNodeLocator() {
		super();
	}
	public void firstUpdate(Map<String,MemcachedNode> nodeMap){
		this.constNodeMap = nodeMap;
	}
	public void update(Map<String,BigDecimal> capability,Map<String,MemcachedNode> nodeMap, Map<String,List<String> > virtual){
		getLogger().info("Routing info update !");
		this.capability = new Capability( capability.get("dgst_bits").intValue(), capability.get("div_bits").intValue(), capability.get("rn").intValue());
		this.constNodeMap = nodeMap;

		ArrayList<ArrayList<MemcachedNode> > newVirtualNodes = new ArrayList<ArrayList<MemcachedNode>>();
		for ( String strVirtualNodeId : virtual.keySet() ){
			long virtualNodeId = Long.parseLong(strVirtualNodeId);
			int index = this.capability.calcVirtualNodeIndex(virtualNodeId);
			ArrayList<MemcachedNode> nodeList = new ArrayList<MemcachedNode>();
			for ( String nodeName : virtual.get(strVirtualNodeId)) {
				nodeList.add(nodeMap.get(nodeName));
			}
			newVirtualNodes.add(index, nodeList);
		}
		this.virtualNodes = newVirtualNodes;
	}

	public Collection<MemcachedNode> getAll() {
		return constNodeMap.values();
	}
	private int hash(String key){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			// SHA1 = 20 bytes
			ByteBuffer digest = ByteBuffer.wrap(md.digest(key.getBytes()));
			// Long = 8 bytes
			digest.position(12);
			return this.capability.calcVirtualNodeIndex(digest.getLong());
		} catch (NoSuchAlgorithmException e) {
			; // 
		}
		return 0;
	}
	public MemcachedNode getPrimary(String k) {
		if(capability == null || virtualNodes == null){
			getLogger().warn("Too first operation ! : The locator has not initialized yet.");
			return constNodeMap.values().iterator().next();
		}
System.err.println("getPrimary("+k+"):"+virtualNodes.get(hash(k)).get(0).toString());
		return virtualNodes.get(hash(k)).get(0);
	}

	public Iterator<MemcachedNode> getSequence(String k) {
		if(capability == null || virtualNodes == null){
			getLogger().warn("Too first operation ! : The locator has not initialized yet.");
			return new NodeIterator(Collections.unmodifiableList(new ArrayList(constNodeMap.values())));
		}
		for ( MemcachedNode n : Collections.unmodifiableList(virtualNodes.get(hash(k))))
System.err.println("getSequence("+k+"):"+n.toString());
		return new NodeIterator(Collections.unmodifiableList(virtualNodes.get(hash(k))));
	}

	public NodeLocator getReadonlyCopy() {
		throw new UnsupportedOperationException("Can't return ...");
	}


	class NodeIterator implements Iterator<MemcachedNode> {
		private final List<MemcachedNode> virtualNodes;
		private int next=0;

		public NodeIterator(List<MemcachedNode> virtualNodes) {
			this.virtualNodes = virtualNodes;
		}

		public boolean hasNext() {
			return virtualNodes.size() > next;
		}

		public MemcachedNode next() {
			return virtualNodes.get(next++);
		}

		public void remove() {
			throw new UnsupportedOperationException("Can't remove a node");
		}

	}
}
