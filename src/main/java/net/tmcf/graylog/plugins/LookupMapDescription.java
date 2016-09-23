package net.tmcf.graylog.plugins;

import java.util.List;

import javax.persistence.Id;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TagFieldEqualityCondition.class, name = "string"),
})
public class LookupMapDescription {
	
	@Id
    @org.mongojack.ObjectId
    @JsonProperty("id")
    public ObjectId _id;
	
	public String key;
	
	public String value;
	
	public List<String> mappings;

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + 
				"_id=" + _id + 
				", key=" + key + 
				", value=" + value + 
				", mappings=" + mappings
				+ "}";
	}

}
