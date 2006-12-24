package descent.internal.core.parser;

import java.util.Hashtable;
import java.util.Map;

public class StringTable {
	
	private Map<String, StringValue> map = new Hashtable<String, StringValue>();
	
	public StringValue update(char[] input, int start, int length) {
		return update(new String(input, start, length));
	}
	
	public StringValue update(String string) {
		StringValue value = map.get(string);
		if (value == null) {
			value = new StringValue();
			value.lstring = string;
			map.put(string, value);
		}
		return value;
	}
	
	public StringValue insert(char[] input, int start, int length) {
		return insert(new String(input, start, length));
	}
	
	public StringValue insert(String string) {
		StringValue value = new StringValue();
		value.lstring = string;
		map.put(string, value);
		return value;
	}
	
	public StringValue lookup(char[] input, int start, int length) {
		return lookup(new String(input, start, length));
	}
	
	public StringValue lookup(String string) {
		return map.get(string);
	}

}
