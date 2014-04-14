package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class UncompressedPrintStreamHandler extends PrintStreamHandler {

	private PrintStream out = null;
	
	public UncompressedPrintStreamHandler(File file) throws IOException {
		this(file, false);
	}
	
	public UncompressedPrintStreamHandler(File file, boolean append) throws IOException {
		FileOutputStream fout = new FileOutputStream(file,append);
		this.out = new PrintStream(fout);
	}
	
	public PrintStream getPrintStream() {
		return this.out;
	}
	
	public void close() throws IOException {
		this.out.close();
	}
	

}
