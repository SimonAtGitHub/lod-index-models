package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.GZIPOutputStream;

/**
 * A PrintStreamHandler which is capable of writing to GZip compressed output files.
 * @author Thomas Gottron
 *
 */
public class GZipPrintStreamHandler extends PrintStreamHandler {

	/**
	 * Internal reference to the PrintStream object
	 */
	private PrintStream out = null;
	/**
	 * Internal reference to the GZip compressed output stream
	 */
	private GZIPOutputStream gzipOut = null;
	
	/**
	 * Opens a GZip compressed output stream and connects it to a PrintStream object. If the file exists, it is overwritten.
	 *  
	 * @param gzipFile the output file to write to
	 * @throws IOException 
	 */
	public GZipPrintStreamHandler(File gzipFile) throws IOException {
		this(gzipFile, false);
	}

	public GZipPrintStreamHandler(File gzipFile, boolean append) throws IOException {
		this.gzipOut = new GZIPOutputStream(new FileOutputStream(gzipFile, append));
		this.out = new PrintStream(gzipOut);
	}

	public PrintStream getPrintStream() {
		return this.out;
	}
	
	public void close() throws IOException {
		this.out.close();
		this.gzipOut.close();
	}
	

}
