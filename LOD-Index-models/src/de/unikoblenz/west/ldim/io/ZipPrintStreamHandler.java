package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipPrintStreamHandler extends PrintStreamHandler {

	private PrintStream out = null;
	private ZipOutputStream zipOut = null;
	
	public ZipPrintStreamHandler(File zipFile) throws IOException {
		this.zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
		zipOut.putNextEntry(new ZipEntry("data"));
		this.out = new PrintStream(zipOut);
	}
	
	public PrintStream getPrintStream() {
		return this.out;
	}
	
	public void close() throws IOException {
		this.out.close();
		this.zipOut.closeEntry();
		this.zipOut.close();
	}
	
	
}
