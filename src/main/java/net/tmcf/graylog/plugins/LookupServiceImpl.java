package net.tmcf.graylog.plugins;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.bson.types.ObjectId;
import org.graylog2.bindings.providers.MongoJackObjectMapperProvider;
import org.graylog2.database.MongoConnection;
import org.graylog2.database.NotFoundException;
import org.graylog2.plugin.database.ValidationException;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

@Singleton
public class LookupServiceImpl implements LookupService {
	
	public static final String LOOKUP = "lookup";

	private final JacksonDBCollection<LookupMapDescription, ObjectId> dbCollection;
	
	@Inject
	protected LookupServiceImpl(MongoConnection mongoConnection, MongoJackObjectMapperProvider mapper) {

		dbCollection = JacksonDBCollection.wrap(mongoConnection.getDatabase().getCollection(LOOKUP),
				LookupMapDescription.class, ObjectId.class, mapper.get());
		
	}

	@Override
	public LookupMapDescription load(String mappingId) throws NotFoundException {
		final LookupMapDescription mapping = dbCollection.findOneById(new ObjectId(mappingId));
		if (mapping == null) {
			throw new NotFoundException();
		}
		return mapping;
	}

	@Override
	public Set<LookupMapDescription> loadAll() throws NotFoundException {
		final DBCursor<LookupMapDescription> lookupMapDescriptions = dbCollection.find();
		Set<LookupMapDescription> mappings = Sets.newHashSet();
		if (lookupMapDescriptions.hasNext()) {
			Iterators.addAll(mappings, lookupMapDescriptions);
		}
		return mappings;
	}

	@Override
	public LookupMapDescription save(LookupMapDescription mapping) throws ValidationException {
		if (!validate(mapping)) {
			throw new ValidationException("Validation failed. The property 'field' cannot be null or empty.");
		}	
		final WriteResult<LookupMapDescription, ObjectId> writeResult = dbCollection.save(mapping);
		
		return writeResult.getSavedObject();
	}

	@Override
	public boolean validate(LookupMapDescription mapping) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int delete(String mappingId) {
		return dbCollection.removeById(new ObjectId(mappingId)).getN();
	}

}
