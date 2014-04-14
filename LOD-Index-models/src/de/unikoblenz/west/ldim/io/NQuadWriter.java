package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.IOException;

public class NQuadWriter {

	private File outputFile = null;
	private PrintStreamHandler writer = null;
	
	
	public NQuadWriter(File output) throws IOException {
		this(output, false);
	}
		
	public NQuadWriter(File output, boolean append) throws IOException {
		this.outputFile = output;
		if (this.outputFile.getName().endsWith(".gz")) {
			this.writer = new GZipPrintStreamHandler(this.outputFile, append);
		} else if (this.outputFile.getName().endsWith(".zip")) {
			if (append) {
				throw new UnsupportedOperationException("Appending to ZIP compressed files not supported");
			} else {
				this.writer = new ZipPrintStreamHandler(this.outputFile);
			}
		} else {
			this.writer = new UncompressedPrintStreamHandler(this.outputFile, append); 
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
