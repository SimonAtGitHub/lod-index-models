package de.unikoblenz.west.ldim.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.tools.Utilities;

/**
 * Generic class for writing an index construct into a file. The file format is
 * very simple. It contains the index key element followed by a separator and
 * the list of relevant items.
 * 
 * @author Thomas Gottron
 * 
 */
public class IndexWriter {

	/**
	 * Constant value for an empty list of references
	 */
	public final static String EMPTY_LIST = "NIL";
	/**
	 * Constant for the separator string between the key and the list of
	 * relevant entries
	 */
	public final static String SEPARATOR = " : ";
	/**
	 * Constant used for substituting a potential occurrence of the separator in
	 * the key string. Each occurrence of the separator string in the key will
	 * we replace with this value.
	 */
	public final static String SEPARATOR_SUB = ":";
	/**
	 * Constant used to separate multiple entries in the key. This constant is
	 * used externally by index classes to concatenate, e.g., sets of elements
	 * which constitute an index key.
	 */
	public final static String KEY_SEPARATOR = "___";

	/**
	 * Internal reference to the PrintStream used for serializing the index.
	 */
	private PrintStream out = null;

	/**
	 * File used to write the index to.
	 */
	private File indexOutput = null;

	/**
	 * Initializes the IndexWriter by opening the indexFile and getting it ready
	 * to write to.
	 * 
	 * @param indexFile
	 *            File to store the index in.
	 * @throws FileNotFoundException
	 */
	public IndexWriter(File indexFile) throws FileNotFoundException {
		this.indexOutput = indexFile;
		this.out = new PrintStream(this.indexOutput);
	}

	/**
	 * Adds an entry to the index. This entry is directly serialized to the
	 * specified file. There is no checking whether the key value has already
	 * been used. The values are serialized using the Utilities class for
	 * generating a concatenated String representation of the value entries.
	 * 
	 * @param key Indey key entry to store data
	 * @param values the values associated/relevant to the given key.
	 */
	public void addToIndex(String key, TreeSet<String> values) {
		while (key.contains(SEPARATOR)) {
			key = key.replaceAll(SEPARATOR, SEPARATOR_SUB);
		}
		KeyValueEntry entry = new KeyValueEntry();
		entry.setKey(key);
		if ((values == null) || (values.size() == 0)) {
			entry.setValue(IndexWriter.EMPTY_LIST);
		} else {
			entry.setValue(Utilities.staticHash(values));
		}
		out.println(entry.toString());
	}

	/**
	 * Closes the IndexWriter. Closes the file the index is written to. No further data can be added.
	 */
	public void close() {
		this.out.close();
	}

}
