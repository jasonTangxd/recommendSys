package com.xiaoxiaomo.web;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapInitDataTest {

	private static final Map<String, String> myMap;
	static
	{
		myMap = new HashMap<String, String>();
		myMap.put("a", "b");
		myMap.put("c", "d");
	}



	public static void main(String[] args) {

		HashMap<String, String > h = new HashMap<String, String>(){{
			put("a","b");
		}};

		Map<String, Integer> left = ImmutableMap.of("a", 1, "b", 2, "c", 3);
		//或者
		Map<String, String> test = ImmutableMap.<String, String>builder()
				.put("k1", "v1")
				.put("k2", "v2")
    	.build();

	}

}
