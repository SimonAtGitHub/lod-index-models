package de.unikoblenz.west.ldim.io.sorting;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import de.unikoblenz.west.ldim.io.BufferedReaderHandler;
import de.unikoblenz.west.ldim.io.GZipBufferedReaderHandler;
import de.unikoblenz.west.ldim.io.GZipPrintStreamHandler;
import de.unikoblenz.west.ldim.io.PrintStreamHandler;
import de.unikoblenz.west.ldim.io.UncompressedBufferedReaderHandler;
import de.unikoblenz.west.ldim.io.UncompressedPrintStreamHandler;
import de.unikoblenz.west.ldim.io.ZipBufferedReaderHandler;
import de.unikoblenz.west.ldim.io.ZipPrintStreamHandler;


/**
 * The default sorting which compares all lines in file using the String comparison
 * 
 * @author Thomas Gottron
 *
 */
public class DefaultSorting extends Sorting<String> {

	@Override
	protected Comparator<String> getComparator() {
		return new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
			
		};
	}

	@Override
	protected String fromLine(String line) {
		return line;
	}

	@Override
	protected String toLine(String object) {
		return object;
	}

	@Override
	protected BufferedReaderHandler getBufferedReaderHandler(File inputFile)
			throws IOException {
		BufferedReaderHandler readerHandler = null;
		if (inputFile.getName().endsWith(".gz")) {
			readerHandler = new GZipBufferedReaderHandler(inputFile);
		} else if (inputFile.getName().endsWith(".zip")) {
			readerHandler = new ZipBufferedReaderHandler(inputFile);
		} else {
			readerHandler = new UncompressedBufferedReaderHandler(inputFile);
		}
		return readerHandler;
	}

	@Override
	protected PrintStreamHandler getPrintStreamHandler(File outputFile)
			throws IOException {
		PrintStreamHandler streamHandler = null;
		if (outputFile.getName().endsWith(".gz")) {
			streamHandler = new GZipPrintStreamHandler(outputFile);
		} else if (outputFile.getName().endsWith(".zip")) {
			streamHandler = new ZipPrintStreamHandler(outputFile);
		} else {
			streamHandler = new UncompressedPrintStreamHandler(outputFile);
		}
		return streamHandler;
	}

}
