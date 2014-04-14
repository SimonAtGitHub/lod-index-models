package de.unikoblenz.west.ldim.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipInputStream;

public class ZipBufferedReaderHandler extends BufferedReaderHandler {

	private BufferedReader in = null;
	private ZipInputStream zipIn = null;
	
	public ZipBufferedReaderHandler(File zipFile) throws IOException {
		this.zipIn = new ZipInputStream(new FileInputStream(zipFile));
		// entry should be called "data"
		// ZipEntry ze = zipIn.getNextEntry();
		zipIn.getNextEntry();
		this.in = new BufferedReader(new InputStreamReader(zipIn));
	}
	
	public BufferedReader getBufferedReader() {
		return this.in;
	}
	
	public void close() throws IOException {
		this.in.close();
		this.zipIn.close();
	}
	

	
	
}
