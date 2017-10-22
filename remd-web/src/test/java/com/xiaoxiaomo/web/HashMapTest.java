package com.xiaoxiaomo.web;

import java.util.HashMap;
import java.util.Map;

public class HashMapTest {

	public static void main(String[] args) {
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		map.put("k2", 5);
		map.put("k1", 5);
		
		for (String str : map.keySet()) {
			System.out.println(str);
		}
		
	}
	
}
