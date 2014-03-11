package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class UncompressedBufferedReaderHandler extends BufferedReaderHandler {

	private BufferedReader in = null;
	
	public UncompressedBufferedReaderHandler(File file) throws IOException {
		this.in = new BufferedReader(new FileReader(file));
	}
	
	public BufferedReader getBufferedReader() {
		return this.in;
	}
	
	public void close() throws IOException {
		this.in.close();
	}
	

}
