package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GZipBufferedReaderHandler extends BufferedReaderHandler {

	private BufferedReader in = null;
	private GZIPInputStream gzipIn = null;
	
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
