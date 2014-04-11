package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.IOException;

public class NQuadParser {

	private File inputFile = null;
	private BufferedReaderHandler reader = null;
	private NQuad buffered = null;
	
	public NQuadParser(File input) throws IOException {
		this.inputFile = input;
		this.reset();
		this.preFetch();
	}
	
	public NQuad next() throws IOException {
		NQuad result = this.buffered;
		this.preFetch();
		return result;
	}
	
	public boolean hasNext() {
		return this.buffered != null;
	}
	
	public void reset() throws IOException {
		if (this.reader!= null) {
			try {
				this.reader.close();
			} catch (IOException ioe) {
			
			}
		}
		if (this.inputFile.getName().endsWith(".gz")) {
			this.reader = new GZipBufferedReaderHandler(this.inputFile);
		} else if (this.inputFile.getName().endsWith(".zip")) {
			this.reader = new ZipBufferedReaderHandler(this.inputFile);
		} else {
			this.reader = new UncompressedBufferedReaderHandler(this.inputFile);
		}
	}
	
	private void preFetch() throws IOException {
		String line = this.reader.getBufferedReader().readLine();
		if (line == null) {
			this.buffered = null;
		} else {
			this.buffered = NQuad.fromString(line);
		}
	}
	
	public File getInputFile() {
		return inputFile;
	}


}
