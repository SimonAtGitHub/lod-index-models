package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.GZIPOutputStream;

public class UncompressedPrintStreamHandler extends PrintStreamHandler {

	private PrintStream out = null;
	
	public UncompressedPrintStreamHandler(File file) throws IOException {
		this.out = new PrintStream(file);
	}
	
	public PrintStream getPrintStream() {
		return this.out;
	}
	
	public void close() throws IOException {
		this.out.close();
	}
	

}
