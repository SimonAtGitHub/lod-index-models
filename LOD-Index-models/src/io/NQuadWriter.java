package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class NQuadWriter {

	private File outputFile = null;
	private PrintStreamHandler writer = null;
	
	
	public NQuadWriter(File output) throws IOException {
		this.outputFile = output;
		if (this.outputFile.getName().endsWith(".gz")) {
			this.writer = new GZipPrintStreamHandler(this.outputFile);
		} else if (this.outputFile.getName().endsWith(".zip")) {
			this.writer = new ZipPrintStreamHandler(this.outputFile);
		} else {
			this.writer = new UncompressedPrintStreamHandler(this.outputFile); 
		}
	}
	
	public void write(NQuad nq) {
		this.writer.getPrintStream().println(nq);
	}
	
	public void close() throws IOException {
		this.writer.close();
	}

	public File getOutputFile() {
		return outputFile;
	}

}
