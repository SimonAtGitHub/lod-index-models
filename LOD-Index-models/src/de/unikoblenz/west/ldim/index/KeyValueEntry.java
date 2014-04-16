package de.unikoblenz.west.ldim.index;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * A entry of a index key and its value(s) encapsulated in a dedicated object.
 * The object is marked up to be suitable for storing in a Berkeley DB
 * persistent hashtable implementation (which currently is not used any more).
 * The class also provide methods for serialization to and from Strings.
 * 
 * @author Thomas Gottron
 * 
 */
@Entity
public class KeyValueEntry {

	/**
	 * The key part of an index entry
	 */
	@PrimaryKey
	public String key = null;
	/**
	 * The value part of an index entry
	 */
	public String value = null;

	/**
	 * Creates an empty object for a key-value pair
	 */
	public KeyValueEntry() {
		// bogus default method needed for persistence layer
	}

	/**
	 * Creates a key-value pair with initial values
	 * 
	 * @param key
	 * @param value
	 */
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

	/**
	 * Serializes the entry for storage in a line based Index format
	 */
	public String toString() {
		return this.key + IndexWriter.SEPARATOR + this.value;
	}

	/**
	 * De-serializes an entry from a string. Assumes the key entry followed by a
	 * separator as defined in IndexWriter followed by the String serialization
	 * of the values (e.g. concatenated hashes).
	 * 
	 * @param in String from which to de-serialize
	 * @return
	 */
	public static KeyValueEntry fromString(String in) {
		String[] parts = in.split(IndexWriter.SEPARATOR);
		String k = parts[0];
		String v = parts[1];
		return new KeyValueEntry(k, v);
	}

}