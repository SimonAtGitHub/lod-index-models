package index.sorting;

import io.BufferedReaderHandler;
import io.GZipBufferedReaderHandler;
import io.GZipPrintStreamHandler;
import io.PrintStreamHandler;
import io.UncompressedBufferedReaderHandler;
import io.UncompressedPrintStreamHandler;
import io.ZipBufferedReaderHandler;
import io.ZipPrintStreamHandler;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

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
