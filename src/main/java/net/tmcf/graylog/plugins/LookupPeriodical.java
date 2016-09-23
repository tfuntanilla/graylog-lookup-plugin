package net.tmcf.graylog.plugins;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.graylog2.database.MongoConnection;
import org.graylog2.plugin.periodical.Periodical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class LookupPeriodical extends Periodical {

	private static final Logger LOG = LoggerFactory.getLogger(LookupPeriodical.class);

	private static final String LOOKUP = "lookup";

	private final MongoConnection mongoConnection;

	@Inject
	public LookupPeriodical(MongoConnection mongoConnection) {
		this.mongoConnection = mongoConnection;
	}

	@Override
	public void doRun() {
		
		ConcurrentHashMap<Pair<String, String>, Map<String, String>> reloadMap = new ConcurrentHashMap<Pair<String, String>, Map<String, String>>();

		try {
			
			MongoCollection<Document> dbCollection = mongoConnection.getMongoDatabase().getCollection(LOOKUP);
			FindIterable<Document> iterable = dbCollection.find();
			iterable.forEach(new Block<Document>() {
			    public void apply(final Document document) {
			    	
			    	JsonParser parser = new JsonParser();
					JsonElement je = parser.parse(document.toString());
					JsonObject json = je.getAsJsonObject();
					
					String key = json.get("key").getAsString();
					String value = json.get("value").getAsString();
					Pair<String, String> kvPair = new ImmutablePair<String, String>(key, value);
					
					JsonArray mappings = json.getAsJsonArray("mappings");
					JsonElement mappingsObject = mappings.get(0);	
					JsonObject map = mappingsObject.getAsJsonObject();
					
					LOG.debug("Adding <" + key + "> : <" + value + "> pair mappings...");

					Map<String, String> dm = new HashMap<String, String>();
					for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
						dm.put(entry.getKey(), entry.getValue().getAsString());
					}
					
					LOG.debug("Data map for <" + key + "> : <" + value + "> pair has " + dm.size() + " mappings.");
					
					// compare: if lookup data map doesn't already have this key-value pair, put it
					if (!LookupPluginMap.dataMap.containsKey(kvPair)) {
						LookupPluginMap.dataMap.put(kvPair, dm);
					}
					
					// place key-value pair and mapping to reload map for further comparison
					reloadMap.put(kvPair, dm);    	
			    }
			});
			
			// final compare: remove those that weren't part of the reload
			for (Map.Entry<Pair<String, String>, Map<String, String>> entry : LookupPluginMap.dataMap.entrySet()) {
				Pair<String, String> kvPair = entry.getKey();
				if (!reloadMap.containsKey(kvPair)) {
					LookupPluginMap.dataMap.remove(kvPair);
				}
			}
			
			LOG.info("Completed lookup data map refresh.");

		} catch(Exception e) {
			LOG.error("Exception while loading lookup data map.", e);
		}
		
	}

	@Override
	public int getInitialDelaySeconds() {
		return 0;
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	public int getPeriodSeconds() {
		// run every 4 hours
		return 14400;
	}

	@Override
	public boolean isDaemon() {
		return false;
	}

	@Override
	public boolean masterOnly() {
		return false;
	}

	@Override
	public boolean runsForever() {
		return false;
	}

	@Override
	public boolean startOnThisNode() {
		return true;
	}

	@Override
	public boolean stopOnGracefulShutdown() {
		return true;
	}

}
