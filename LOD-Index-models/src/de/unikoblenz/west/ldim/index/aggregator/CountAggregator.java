package de.unikoblenz.west.ldim.index.aggregator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.index.KeyValueEntry;

public class CountAggregator extends IndexAggregator {

	private HashMap<String, Integer> counter = new HashMap<String, Integer>();
	
	@Override
	protected void initialize(PrintStream out) {
		// ignore
	}

	@Override
	protected void notify(KeyValueEntry entry, PrintStream out) {
		int freq = 0;
		if (counter.containsKey(entry.value)) {
			freq = counter.get(entry.value);
		}
		freq++;
		counter.put(entry.value, freq);
	}

	@Override
	protected void finalize(PrintStream out) {
		for (String hash: counter.keySet()) {
			out.println(hash+IndexWriter.SEPARATOR+counter.get(hash));
		}
	}

}
