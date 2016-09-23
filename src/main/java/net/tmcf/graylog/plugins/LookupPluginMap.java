package net.tmcf.graylog.plugins;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;

public class LookupPluginMap {
	
	public static ConcurrentHashMap<Pair<String, String>, Map<String, String>> dataMap = new ConcurrentHashMap<Pair<String, String>, Map<String, String>>();

}
