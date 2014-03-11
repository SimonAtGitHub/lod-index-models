package index.aggregator;

import index.IndexWriter;
import index.KeyValueEntry;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.TreeSet;

import tools.Utilities;

public class InvertedIndexAggregator extends IndexAggregator {

	private String lastKey = null;
	private TreeSet<String> entries = new TreeSet<String>();
	public static String EOL_TOKEN = ":::";
	
	@Override
	protected void initialize(PrintStream out) {
		this.lastKey = null;
		this.entries = new TreeSet<String>();
	}

	@Override
	protected void notify(KeyValueEntry entry, PrintStream out) {
		if ( (this.lastKey == null) || (! entry.value.equals(this.lastKey)) ) {
			this.writeKey(out);
			this.lastKey = entry.value;
			this.entries.add(entry.key);
		} else {
			this.entries.add(entry.key);
		}
	}

	@Override
	protected void finalize(PrintStream out) {
		this.writeKey(out);
	}
	
	private void writeKey(PrintStream out) {
		if ( this.lastKey != null) {
			out.println(this.lastKey+IndexWriter.SEPARATOR+Utilities.concatenate(entries)+EOL_TOKEN);
			this.lastKey = null;
			this.entries = new TreeSet<String>();
		}
	}


}
