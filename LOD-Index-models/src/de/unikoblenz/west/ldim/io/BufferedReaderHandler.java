package de.unikoblenz.west.ldim.io;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class BufferedReaderHandler {

	public abstract BufferedReader getBufferedReader();
	
	public abstract void close() throws IOException;

	
}
