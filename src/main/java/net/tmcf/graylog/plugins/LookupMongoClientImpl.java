package net.tmcf.graylog.plugins;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class LookupMongoClientImpl implements LookupMongoClient {
	
	private static final Logger LOG = LoggerFactory.getLogger(LookupMongoClient.class);
	
	private final MongoClientURI mongoClientURI;

    private MongoClient m = null;
    private DB db = null;
    
    @Inject
    public LookupMongoClientImpl(final LookupMongoDbConfig configuration) {
    	this(configuration.getMongoClientURI());
    }
    
    LookupMongoClientImpl(MongoClientURI mongoClientURI) {
        this.mongoClientURI = checkNotNull(mongoClientURI);
    }

	@Override
	public Mongo connect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DB getDatabase() {
		// TODO Auto-generated method stub
		return null;
	}

}
