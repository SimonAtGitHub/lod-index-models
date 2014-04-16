package de.unikoblenz.west.ldim.index.aggregator;

import java.io.PrintStream;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.index.KeyValueEntry;

/**
 * The SortedCountAggregator relies on input data coming in sorted way by the
 * keys. The implementation at least relies that all values for a specific key
 * are coming as an uninterrupted sequence of key-value pairs.
 * 
 * @author Thomas Gottron
 * 
 */
public class SortedCountAggregator extends IndexAggregator {

	/**
	 * Keep a reference to the last observed key. Needed to identify when a new key has been observed.
	 */
	private String lastKey = null;
	/**
	 * keep track of the number of references to a specific key.
	 */
	private int count = 0;

	@Override
	protected void initialize(PrintStream out) {
		this.lastKey = null;
		this.count = 0;
	}

	@Override
	protected void notify(KeyValueEntry entry, PrintStream out) {
		// check if key is different from previous and if yes -- flush the data to output 
		if ((this.lastKey == null) || (!entry.value.equals(this.lastKey))) {
			this.writeKey(out);
			this.lastKey = entry.value;
			this.count++;
		} else {
			this.count++;
		}
	}

	@Override
	protected void finalize(PrintStream out) {
		this.writeKey(out);
	}

	/**
	 * Flush out the information (i.e. count) collected for one key.
	 * @param out
	 */
	private void writeKey(PrintStream out) {
		if (this.lastKey != null) {
			out.println(this.lastKey + IndexWriter.SEPARATOR + this.count);
			this.lastKey = null;
			this.count = 0;
		}
	}

}
