package de.unikoblenz.west.ldim.io;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Abstraction layer for working with BufferedReaders. This allows for using
 * different types of Readers, e.g. for reading from compressed files, while
 * maintaining the same method interface.
 * 
 * @author Thomas Gottron
 * 
 */
public abstract class BufferedReaderHandler {

	/**
	 * Provides the actual BufferedReader.
	 * @return
	 */
	public abstract BufferedReader getBufferedReader();

	/**
	 * Closes the BufferedReader and all input streams it is built upon.
	 * @throws IOException
	 */
	public abstract void close() throws IOException;

}
