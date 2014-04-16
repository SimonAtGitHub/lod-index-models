package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.IOException;

/**
 * Writes quads to an output file. The class encapsulates the serialization of
 * NQuad objects and is capable of writing to zip and gzip compressed files.
 * 
 * @author Thomas Gottron
 * 
 */
public class NQuadWriter {

	/**
	 * Internal reference to the output file
	 */
	private File outputFile = null;
	/**
	 * Internal reference to the PrintStreamhandler used for writing to the
	 * output file.
	 */
	private PrintStreamHandler writer = null;

	/**
	 * Initializes an NQuadWriter to write to the given file. The given file is
	 * overwritten.
	 * 
	 * @param output
	 *            Output file. If the filename ends in .gz a gzip compressed
	 *            file is written, if the filename ends in .zip a zip compressed
	 *            file is written. All other filename suffixes lead to an
	 *            uncompressed file.
	 * @throws IOException
	 */
	public NQuadWriter(File output) throws IOException {
		this(output, false);
	}

	/**
	 * Initializes an NQuadWriter to write to the given file. This constructor
	 * allows for specifying whether or not to append to the given file (or to
	 * overwrite it).
	 * 
	 * @param output
	 *            Output file. If the filename ends in .gz a gzip compressed
	 *            file is written, if the filename ends in .zip a zip compressed
	 *            file is written. All other filename suffixes lead to an
	 *            uncompressed file. Please not that appending to a zip file is
	 *            not supported.
	 * @param append
	 *            Flag whether or not to append nquads to the given file.
	 * @throws IOException
	 */
	public NQuadWriter(File output, boolean append) throws IOException {
		this.outputFile = output;
		if (this.outputFile.getName().endsWith(".gz")) {
			this.writer = new GZipPrintStreamHandler(this.outputFile, append);
		} else if (this.outputFile.getName().endsWith(".zip")) {
			if (append) {
				throw new UnsupportedOperationException(
						"Appending to ZIP compressed files not supported");
			} else {
				this.writer = new ZipPrintStreamHandler(this.outputFile);
			}
		} else {
			this.writer = new UncompressedPrintStreamHandler(this.outputFile,
					append);
		}
	}

	/**
	 * Writes a single quad to the output file.
	 * 
	 * @param nq
	 *            Quad to write to the output file.
	 */
	public void write(NQuad nq) {
		this.writer.getPrintStream().println(nq);
	}

	/**
	 * Closes the output file and all helper tools.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.writer.close();
	}

	/**
	 * Returns the file this NQuadWriter is writing to.
	 * 
	 * @return Output file
	 */
	public File getOutputFile() {
		return outputFile;
	}

}
