package index.sorting;

import io.BufferedReaderHandler;
import io.GZipBufferedReaderHandler;
import io.GZipPrintStreamHandler;
import io.NQuad;
import io.PrintStreamHandler;
import io.UncompressedBufferedReaderHandler;
import io.UncompressedPrintStreamHandler;
import io.ZipBufferedReaderHandler;
import io.ZipPrintStreamHandler;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;


public class NQuadSorting extends Sorting<NQuad> {
	public static final byte SUBJECT_SORT = 1;
	public static final byte PREDICATE_SORT = 2;
	public static final byte OBJECT_SORT = 3;
	public static final byte CONTEXT_SORT = 4;

	public class NQuadComparator implements Comparator<NQuad> {

		private byte mode = NQuadSorting.SUBJECT_SORT;

		public NQuadComparator(byte mode) {
			this.mode = mode;
		}
		
		@Override
		public int compare(NQuad quad1, NQuad quad2) {
			int result = 0;
			switch (this.mode) {
			case NQuadSorting.SUBJECT_SORT:
				result = quad1.subject.compareTo(quad2.subject);
				break;
			case NQuadSorting.PREDICATE_SORT:
				result = quad1.predicate.compareTo(quad2.predicate);
				break;
			case NQuadSorting.OBJECT_SORT:
				result = quad1.object.compareTo(quad2.object);
				break;
			case NQuadSorting.CONTEXT_SORT:
				result = quad1.context.compareTo(quad2.context);
				break;
			}
			if (result == 0) {
				// backup: default comparator if equal values 
				result = quad1.compareTo(quad2);
			}
			return result;
		}

	}

	
	private byte sortMode = NQuadSorting.SUBJECT_SORT;
	
	public NQuadSorting(byte sortCriterion) {
		this.sortMode = sortCriterion;
	}

	@Override
	protected Comparator<NQuad> getComparator() {
		return new NQuadComparator(this.sortMode);
	}

	@Override
	protected NQuad fromLine(String line) {
		return NQuad.fromString(line);
	}

	@Override
	protected String toLine(NQuad object) {
		return object.toString();
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
