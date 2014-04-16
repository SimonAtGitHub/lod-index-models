package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.IOException;

/**
 * Parser for files in NQuad format. Reads single quads from the file and
 * provides them via a simple next() method. The parser is capable to directly
 * read from compressed files.
 * 
 * @author Thomas Gottron
 * 
 */
public class NQuadParser {

	/**
	 * Internal reference to the input file
	 */
	private File inputFile = null;
	/**
	 * Internal reference to a BufferedReaderHandler used for accessing the file
	 */
	private BufferedReaderHandler reader = null;
	/**
	 * Next pre-fetched quad stored internally for the next call of the next()
	 * method and for answering to the hasNext() method.
	 */
	private NQuad buffered = null;

	/**
	 * Initializes the Parser by opening the file and getting ready to read
	 * quads. Internally the first quad is already pre-fetched.
	 * 
	 * @param input
	 *            File from which to read nquads. The file can be a plain .nq
	 *            format, or a gzip or zip compressed nq format.
	 * @throws IOException
	 */
	public NQuadParser(File input) throws IOException {
		this.inputFile = input;
		this.reset();
		this.preFetch();
	}

	/**
	 * Returns the next quad is available. Otherwise the method returns null.
	 * 
	 * @return Next quad from the input file.
	 * @throws IOException
	 */
	public NQuad next() throws IOException {
		// set result to the pre-fetched quad (to be returned)
		NQuad result = this.buffered;
		// pre-fetch the next quad
		this.preFetch();
		return result;
	}

	/**
	 * Methods checks whether or not there is at least one more quad to be
	 * returned from the input file.
	 * 
	 * @return true if another quad can be read (i.e. the next() method will
	 *         return a non-null value)
	 */
	public boolean hasNext() {
		return this.buffered != null;
	}

	/**
	 * Reset the parser to start reading the NQuad file from the beginning.
	 * Internally the file references are closed and re-opened, getting
	 * everything ready for reading. after this method the object should be in
	 * an equivalent state as a newly instantiated parser object with the same
	 * input file.
	 * 
	 * @throws IOException
	 */
	public void reset() throws IOException {
		if (this.reader != null) {
			try {
				this.reader.close();
			} catch (IOException ioe) {

			}
		}
		if (this.inputFile.getName().endsWith(".gz")) {
			this.reader = new GZipBufferedReaderHandler(this.inputFile);
		} else if (this.inputFile.getName().endsWith(".zip")) {
			this.reader = new ZipBufferedReaderHandler(this.inputFile);
		} else {
			this.reader = new UncompressedBufferedReaderHandler(this.inputFile);
		}
	}

	/**
	 * Pre-fetches the next quad from the input file. The pre-fetched quad is
	 * stored internally for answering correctly to the hasNext() method and
	 * will be provided as answer for the the next call to next().
	 * 
	 * @throws IOException
	 */
	private void preFetch() throws IOException {
		String line = this.reader.getBufferedReader().readLine();
		if (line == null) {
			this.buffered = null;
		} else {
			this.buffered = NQuad.fromString(line);
		}
	}

	/**
	 * Returns the input file from which this parser is reading.
	 * 
	 * @return File from which the parser reads quads.
	 */
	public File getInputFile() {
		return inputFile;
	}

}
