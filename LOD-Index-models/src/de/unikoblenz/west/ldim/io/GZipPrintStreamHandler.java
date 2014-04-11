package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GZipPrintStreamHandler extends PrintStreamHandler {

	private PrintStream out = null;
	private GZIPOutputStream gzipOut = null;
	
	public GZipPrintStreamHandler(File gzipFile) throws IOException {
		this.gzipOut = new GZIPOutputStream(new FileOutputStream(gzipFile));
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
