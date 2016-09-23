package net.tmcf.graylog.plugins;

import java.util.Objects;

public class TagFieldEqualityCondition extends LookupMapDescription {
	
    public boolean matchesFieldStringValue(Object val) {
        return (val != null) && value.equalsIgnoreCase(String.valueOf(val));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagFieldEqualityCondition)) return false;

        TagFieldEqualityCondition that = (TagFieldEqualityCondition) o;

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

}