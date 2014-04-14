package de.unikoblenz.west.ldim.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 * Provides the abstraction layer for opening and closing a BufferedReader ontop
 * of a GZip compressed input file.
 * 
 * @author Thomas Gottron
 * 
 */
public class GZipBufferedReaderHandler extends BufferedReaderHandler {

	/**
	 * Internal reference to the BufferedReader
	 */
	private BufferedReader in = null;
	/**
	 * Internal reference to a gzip input stream which feeds the BuefferedReader
	 */
	private GZIPInputStream gzipIn = null;

	/**
	 * Opens the input file and connects it via a GZip enables input stream to a BufferedReader.
	 * 
	 * @param gzipFile input file (GZip compressed) to read from
	 * @throws IOException
	 */
	public GZipBufferedReaderHandler(File gzipFile) throws IOException {
		this.gzipIn = new GZIPInputStream(new FileInputStream(gzipFile));
		this.in = new BufferedReader(new InputStreamReader(gzipIn));
	}

	public BufferedReader getBufferedReader() {
		return this.in;
	}

	public void close() throws IOException {
		this.in.close();
		this.gzipIn.close();
	}

}
