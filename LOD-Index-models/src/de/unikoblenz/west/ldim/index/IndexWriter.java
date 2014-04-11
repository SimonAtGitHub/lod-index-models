package de.unikoblenz.west.ldim.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.tools.Utilities;

public class IndexWriter {
	
	public final static String EMPTY_LIST = "NIL";
	public final static String SEPARATOR = " : ";
	public final static String SEPARATOR_SUB = ":";
	public final static String KEY_SEPARATOR = "___";

	private PrintStream out = null;

	private File indexOutput = null;
	
	
	public IndexWriter(File indexFile) throws FileNotFoundException {
		this.indexOutput = indexFile;
		this.out = new PrintStream(this.indexOutput);
	}
	
	public void addToIndex(String key, TreeSet<String> values) {
		while (key.contains(SEPARATOR)) {
			key = key.replaceAll(SEPARATOR, SEPARATOR_SUB);
		}
		KeyValueEntry entry = new KeyValueEntry();
		entry.setKey(key);
		if ((values == null) || (values.size()==0)) {
			entry.setValue(IndexWriter.EMPTY_LIST);
		} else {
			entry.setValue(Utilities.staticHash(values));
		}
		out.println(entry.toString());
	}
	
	public void close() {
		this.out.close();
	}

}
