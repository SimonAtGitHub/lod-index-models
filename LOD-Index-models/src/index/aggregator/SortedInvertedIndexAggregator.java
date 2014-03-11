package index.aggregator;

import index.IndexWriter;
import index.KeyValueEntry;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.TreeSet;

import tools.Utilities;

public class SortedInvertedIndexAggregator extends IndexAggregator {

	private String lastKey = null;
	public static String EOL_TOKEN = ":::";
	
	@Override
	protected void initialize(PrintStream out) {
		this.lastKey = null;
	}

	@Override
	protected void notify(KeyValueEntry entry, PrintStream out) {
		if ( (this.lastKey == null) || (! entry.value.equals(this.lastKey)) ) {
			if (this.lastKey != null) {
				this.closeEntry(out);
			}
			this.writeKey(out, entry.value);
			this.lastKey = entry.value;
		}
		this.writeValue(out, entry.key);
	}

	@Override
	protected void finalize(PrintStream out) {
		this.closeEntry(out);
	}
	
	private void writeKey(PrintStream out, String key) {
		out.print(key+IndexWriter.SEPARATOR);
		this.lastKey = null;
	}
	
	private void writeValue(PrintStream out, String value) {
		out.print(value.trim().replaceAll(Utilities.CONCATENATION_TOKEN+"", Utilities.CONCATENATION_SUBSTITUTE+"")+Utilities.CONCATENATION_TOKEN);
	}
	
	private void closeEntry(PrintStream out) {
		out.println(EOL_TOKEN);
		this.lastKey = null;
	}
	


}
