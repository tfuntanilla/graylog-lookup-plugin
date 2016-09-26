package net.tmcf.graylog.plugins;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.filters.MessageFilter;

public class Lookup implements MessageFilter {
	
	private static final String NAME = "Lookup";
	private static final String DOCTYPE_ = "doctype_";
	private static final String FLAG = "flag";	// for tagging messages that should be automatically skipped
	private static final String ALPHANUM_REGEX = "[^a-zA-Z0-9]+";	// for removing special chars for doctype_ values

	@Override
	public boolean filter(Message msg) {
		try {

			for (Map.Entry<Pair<String, String>, Map<String, String>> entry : LookupPluginMap.dataMap.entrySet()) {

				Pair<String, String> kvPair = entry.getKey();
				String existingField = kvPair.getLeft();
				String newField = kvPair.getRight();

				if (msg.hasField(existingField)) {				

					String existingFieldValue = (String) msg.getField(existingField);			
					Map<String, String> mappings = entry.getValue();

					if (!mappings.isEmpty()) {

						String newFieldValue = mappings.get(existingFieldValue);

						if (newFieldValue != null) {

							String[] newFieldsArray = newField.split(",");
							for (String nf : newFieldsArray) {
								if (nf.equals(DOCTYPE_)) {
									String alphanum_newFieldValue = newFieldValue.replaceAll(ALPHANUM_REGEX, "");
									msg.addField(nf, alphanum_newFieldValue);
								} else {
									msg.addField(nf, newFieldValue);
								}
							}

						} else { // lookup failed; there is no mapping
							// check if doctype_ is supposed to be set based on the mapping for the value of this existing field
							if (newField.contains(DOCTYPE_)) {
								// if so, make doctype_ = value of existing field (remove all special chars)
								String alphanum_existingFieldValue = existingFieldValue.replaceAll(ALPHANUM_REGEX, "");							
								msg.addField(DOCTYPE_, alphanum_existingFieldValue);
							}
						}
					}
				} else {
					// if doctype_ is supposed to be set based on the mapping for the value of this existing field, 
					// if the existing field itself is missing from the msg, the msg is automatically invalid
					if (newField.contains(DOCTYPE_)) {
						msg.addField(FLAG, "");	// add parsing_failed field
					}						
				}
			}

			// if doctype_ was resolved in the end, remove parsing_failed field if it's present
			if (msg.hasField(DOCTYPE_) && msg.hasField(FLAG)) {
				msg.removeField(FLAG);
			}

		} catch(Exception e) {
			msg.addField(FLAG, "");
		}

		return false;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getPriority() {
		// run this filter after extractors
		return 11;
	}
}
