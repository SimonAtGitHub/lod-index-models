package de.unikoblenz.west.ldim.index.sorting;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import de.unikoblenz.west.ldim.index.KeyValueEntry;
import de.unikoblenz.west.ldim.io.BufferedReaderHandler;
import de.unikoblenz.west.ldim.io.PrintStreamHandler;
import de.unikoblenz.west.ldim.io.UncompressedBufferedReaderHandler;
import de.unikoblenz.west.ldim.io.UncompressedPrintStreamHandler;

public class IndexSorting extends Sorting<KeyValueEntry> {

	public static final byte KEY_SORT = 0; 
	public static final byte VALUE_SORT = 1; 
	
	private byte mode = KEY_SORT;
	
	public class IndexEntryComparator implements Comparator<KeyValueEntry> {

		private byte mode = IndexSorting.KEY_SORT;

		public IndexEntryComparator(byte mode) {
			this.mode = mode;
		}
		
		@Override
		public int compare(KeyValueEntry entry1, KeyValueEntry entry2) {
			int result = 0;
			switch (this.mode) {
			case IndexSorting.KEY_SORT:
				result = entry1.getKey().compareTo(entry2.getKey());
				if (result == 0) {
					// backup: default comparator if equal values 
					result = entry1.getValue().compareTo(entry2.getValue());
				}
				break;
			case IndexSorting.VALUE_SORT:
				result = entry1.getValue().compareTo(entry2.getValue());
				if (result == 0) {
					// backup: default comparator if equal values 
					result = entry1.getKey().compareTo(entry2.getKey());
				}
				break;
			}
			return result;
		}

	}
	
	public IndexSorting(byte m) {
		this.mode = m;
	}
	
	@Override
	protected Comparator<KeyValueEntry> getComparator() {
		return new IndexEntryComparator(this.mode);
	}

	@Override
	protected KeyValueEntry fromLine(String line) {
		return KeyValueEntry.fromString(line);
	}

	@Override
	protected String toLine(KeyValueEntry object) {
		return object.toString();
	}

	@Override
	protected BufferedReaderHandler getBufferedReaderHandler(File inputFile) throws IOException {
		return new UncompressedBufferedReaderHandler(inputFile);
	}

	@Override
	protected PrintStreamHandler getPrintStreamHandler(File outputFile) throws IOException {
		return new UncompressedPrintStreamHandler(outputFile);
	}

}
