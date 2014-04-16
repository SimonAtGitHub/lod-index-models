package de.unikoblenz.west.ldim.index.aggregator;

import java.io.PrintStream;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.index.KeyValueEntry;
import de.unikoblenz.west.ldim.tools.Utilities;

/**
 * Class to produce an inverted index file. The class relies on input data
 * coming sorted by the keys. The implementation at least relies that all values
 * for a specific key are coming as an uninterrupted sequence of key-value
 * pairs.
 * 
 * Effectively the inverted index is stored in a line based format as posting
 * lists. Each posting list begins with the index key, followed by a separator
 * and a list of related items. The end of a posting list is marked by a
 * specific token and the end of the line.
 * 
 * @author Thomas Gottron
 * 
 */
public class SortedInvertedIndexAggregator extends IndexAggregator {

	/**
	 * Last observed key value. Needed to detect when a new key is coming in
	 * order to flush all observations of the previous key to the index file.
	 */
	private String lastKey = null;
	/**
	 * Marker for the end of a line.
	 */
	public static String EOL_TOKEN = ":::";

	@Override
	protected void initialize(PrintStream out) {
		this.lastKey = null;
	}

	@Override
	protected void notify(KeyValueEntry entry, PrintStream out) {
		// check if new key as then we need to finalize the output and prepare
		// the next one
		if ((this.lastKey == null) || (!entry.value.equals(this.lastKey))) {
			// if previously open posting list exists, close it
			if (this.lastKey != null) {
				this.closeEntry(out);
			}
			// begin new posting list with a new key value.
			this.writeKey(out, entry.value);
			this.lastKey = entry.value;
		}
		// add value to current posting list.
		this.writeValue(out, entry.key);
	}

	@Override
	protected void finalize(PrintStream out) {
		this.closeEntry(out);
	}

	/**
	 * Writes the key to the output and appends a separator to denote the
	 * beginning of the list of the related items. Essentially starts a new
	 * posting list.
	 * 
	 * @param out
	 *            output stream to write to.
	 * @param key
	 *            key value for which to ass a new posting list
	 */
	private void writeKey(PrintStream out, String key) {
		out.print(key + IndexWriter.SEPARATOR);
		this.lastKey = null;
	}

	/**
	 * Writes a single value and appends it to the current posting list.
	 * 
	 * @param out
	 *            output stream to write to.
	 * @param value
	 *            value to add into the posting list
	 */
	private void writeValue(PrintStream out, String value) {
		out.print(value.trim().replaceAll(Utilities.CONCATENATION_TOKEN + "",
				Utilities.CONCATENATION_SUBSTITUTE + "")
				+ Utilities.CONCATENATION_TOKEN);
	}

	/**
	 * Closes the current posting list. This marks the end of the list with a
	 * specific token.
	 * 
	 * @param out
	 */
	private void closeEntry(PrintStream out) {
		out.println(EOL_TOKEN);
		this.lastKey = null;
	}

}
