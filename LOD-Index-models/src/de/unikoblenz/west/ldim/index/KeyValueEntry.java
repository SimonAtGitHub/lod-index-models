package de.unikoblenz.west.ldim.index;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class KeyValueEntry {
	@PrimaryKey
	public String key = null;
	public String value = null;
	public KeyValueEntry() {
		
	}
	public KeyValueEntry(String key, String value) {
		this.key = key;
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String k) {
		this.key = k;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String v) {
		this.value = v;
	}
	
	public String toString() {
		return this.key+IndexWriter.SEPARATOR+this.value;
	}
	
	public static KeyValueEntry fromString(String in) {
		String[] parts = in.split(IndexWriter.SEPARATOR);
		String k = parts[0];
		String v = parts[1];
		return new KeyValueEntry(k, v);
	}
	
	

	
}