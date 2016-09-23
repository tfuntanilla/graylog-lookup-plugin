package net.tmcf.graylog.plugins;

import java.util.Set;

import org.graylog2.database.NotFoundException;
import org.graylog2.plugin.database.ValidationException;

public interface LookupService {
	
	LookupMapDescription load(String mappingId) throws NotFoundException;
	
	Set<LookupMapDescription> loadAll() throws NotFoundException;
	
	LookupMapDescription save(LookupMapDescription mapping) throws ValidationException;

	boolean validate(LookupMapDescription mapping);
	
	int delete(String mappingId);

}
