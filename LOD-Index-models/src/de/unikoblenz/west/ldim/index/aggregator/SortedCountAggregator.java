package de.unikoblenz.west.ldim.index.aggregator;

import java.io.OutputStream;
import java.io.PrintStream;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.index.KeyValueEntry;

public class SortedCountAggregator extends IndexAggregator {

	private String lastKey = null;
	private int count = 0;
	
	@Override
	protected void initialize(PrintStream out) {
		this.lastKey = null;
		this.count = 0;
	}

	@Override
	protected void notify(KeyValueEntry entry, PrintStream out) {
		if ( (this.lastKey == null) || (! entry.value.equals(this.lastKey)) ) {
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
	
	private void writeKey(PrintStream out) {
		if ( this.lastKey != null) {
			out.println(this.lastKey+IndexWriter.SEPARATOR+this.count);
			this.lastKey = null;
			this.count = 0;
		}
	}

}
