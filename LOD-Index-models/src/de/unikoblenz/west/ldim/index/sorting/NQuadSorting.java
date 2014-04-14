package de.unikoblenz.west.ldim.index.sorting;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import de.unikoblenz.west.ldim.io.BufferedReaderHandler;
import de.unikoblenz.west.ldim.io.GZipBufferedReaderHandler;
import de.unikoblenz.west.ldim.io.GZipPrintStreamHandler;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.PrintStreamHandler;
import de.unikoblenz.west.ldim.io.UncompressedBufferedReaderHandler;
import de.unikoblenz.west.ldim.io.UncompressedPrintStreamHandler;
import de.unikoblenz.west.ldim.io.ZipBufferedReaderHandler;
import de.unikoblenz.west.ldim.io.ZipPrintStreamHandler;

/**
 * Sorting for RDF data in N-Quad format. The sorting can be done by all four
 * entries in an N-Quad but is always done lexicographically. If the chosen
 * entry is equivalent for two N-Quads, the method falls back to the feault
 * comparison of N-Quad objects. In this way there is always a well defined
 * order for all objects, unless they are equal.
 * 
 * @author Thomas Gottron
 * 
 */
public class NQuadSorting extends Sorting<NQuad> {

	/**
	 * Constant for sorting N-Quads by the subject entry
	 */
	public static final byte SUBJECT_SORT = 1;
	/**
	 * Constant for sorting N-Quads by the predicate entry
	 */
	public static final byte PREDICATE_SORT = 2;
	/**
	 * Constant for sorting N-Quads by the object entry
	 */
	public static final byte OBJECT_SORT = 3;
	/**
	 * Constant for sorting N-Quads by the context entry
	 */
	public static final byte CONTEXT_SORT = 4;

	public class NQuadComparator implements Comparator<NQuad> {

		/**
		 * Internal flag of how to sort. See the constant values defined in
		 * NQuadSorting
		 */
		private byte mode = NQuadSorting.SUBJECT_SORT;

		/**
		 * Initialize the NQuadComparator with a given operational mode.
		 * 
		 * @param mode
		 */
		public NQuadComparator(byte mode) {
			this.mode = mode;
		}

		/**
		 * Compare two N-Quads. The implementation and exection of the
		 * comparison depends on the mode settings.
		 */
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

	/**
	 * Internal flag on how to sort.
	 */
	private byte sortMode = NQuadSorting.SUBJECT_SORT;

	/**
	 * Initialized using a given sorting mode (subject, predicate, object or
	 * context)
	 * 
	 * @param sortCriterion
	 */
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

	/**
	 * This method takes care that the Reader Handler is able to work with .gz
	 * and .zip files as well. This is recognized by the file extension. Any
	 * other file is attempted to read as plain uncompressed NQuad format.
	 */
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

	/**
	 * This method takes care that the Printer Handler is able to work with .gz
	 * and .zip files as well. This is recognized by the file extension. Any
	 * other file is written as plain uncompressed NQuad format.
	 */
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
