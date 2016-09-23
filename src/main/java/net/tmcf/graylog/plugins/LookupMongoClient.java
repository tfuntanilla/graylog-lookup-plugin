package net.tmcf.graylog.plugins;

import com.mongodb.DB;
import com.mongodb.Mongo;

public interface LookupMongoClient {
	Mongo connect();

    DB getDatabase();
}
