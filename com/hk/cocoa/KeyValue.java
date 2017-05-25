package com.tvb.hk.anywhere.sdk.bos;

import com.tvb.hk.anywhere.util.Util;

public class KeyValue {
	
	public String key;
	public String value;
	
	public KeyValue(String k,String v){
		this.key = k;
		this.value = Util.encodeUrlParameter(v);
	}
	
	public KeyValue(String k,int v){
		this.key = k;
		this.value = v+"";
	}

}
