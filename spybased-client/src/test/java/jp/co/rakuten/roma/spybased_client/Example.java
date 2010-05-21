package jp.co.rakuten.roma.spybased_client;

import jp.co.rakuten.roma.client.RomaAlist;
import jp.co.rakuten.roma.client.RomaExtensionAlist;
import jp.co.rakuten.roma.client.SimpleRomaClient;

public class Example {
	public void example(){
		//*******************
		// Simple case
		SimpleRomaClient client = null; // Maybe get by factory
		client.add("key1", 3600 , new String("value1"));
		String v = (String)client.get("key1");
		//*******************
		// Roma-extention case
		RomaAlist<String> alist = null; // Maybe get by factory
		RomaExtensionAlist ext1 = alist.append(new String("alist1"));
		client.extension(ext1);
		RomaExtensionAlist ext2 = alist.prepend(new String("alist2"));
		client.extension(ext2);
		RomaExtensionAlist ext3 = alist.insertOrMoveToLast(new String("alist1"));
		client.extension(ext3);
	}
}
