package de.unikoblenz.west.ldim.io;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Abstraction layer class which allows for managing PrintStream ontop of
 * different types of files. The abstraction allows for operating on different
 * file formats, e.g. compressed files, while maintaining the same method
 * interface.
 * 
 * @author Thomas Gottron
 * 
 */
public abstract class PrintStreamHandler {

	/**
	 * Returns the actual PrintStream
	 * @return
	 */
	public abstract PrintStream getPrintStream();

	/**
	 * Closes the PrintStream and any dependent output stream object
	 * @throws IOException
	 */
	public abstract void close() throws IOException;

}
