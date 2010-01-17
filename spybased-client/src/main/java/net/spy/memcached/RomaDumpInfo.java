package net.spy.memcached;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RomaDumpInfo {
	/* locaterに任せるべきのよう@@@ */
	static class Capability {
		public int digestBits;
		public int maskBitRange;
		public int rn;
		public Capability(int digestBits , int maskBitRange , int rn) {
			this.digestBits = digestBits;
			this.maskBitRange = maskBitRange;
			this.rn = rn;
		}
	}
	Map<String,InetSocketAddress> nodeMap = new TreeMap<String, InetSocketAddress>();
	static final Pattern p = Pattern.compile("^(.+)_(\\d+)$");
	public void append(String nodeName){
		Matcher m = p.matcher(nodeName);
		if ( m.matches() ) 
			System.out.println("SPLIT:" + m.group(1) + "-" + m.group(2));
//		nodeMap.put(nodeName, new InetSocketAddress(n[0],Integer.parseInt(n[1])));
	}
	Map<String,InetSocketAddress> virtualMap = new HashMap<String, InetSocketAddress>();
}
