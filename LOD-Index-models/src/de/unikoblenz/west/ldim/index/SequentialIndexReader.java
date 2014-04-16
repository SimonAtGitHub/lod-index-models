package de.unikoblenz.west.ldim.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * A class for reading sequentially from an Index file. The entries are
 * sequentially read line by line (given the linebased format of the
 * IndexWriter).
 * 
 * @author Thomas Gottron
 * 
 */
public class SequentialIndexReader {

	/**
	 * BufferedReader to read the input from
	 */
	private BufferedReader in = null;
	/**
	 * Internal flag whether or not the end of the file has been reached
	 */
	private boolean EOF = false;

	/**
	 * Initializes the SequentialIndexReader to read from a given File.
	 * 
	 * @param indexFile
	 *            File to read the index from.
	 * @throws FileNotFoundException
	 */
	public SequentialIndexReader(File indexFile) throws FileNotFoundException {
		this.in = new BufferedReader(new FileReader(indexFile));
	}

	/**
	 * Reads the next entry from the index file and returns it.
	 * 
	 * @return Entry composed of the key and its value. Note that the value is
	 *         given in its concatenated and hashed form as stored in the index.
	 */
	public KeyValueEntry nextEntry() {
		KeyValueEntry result = null;
		if (!EOF) {
			try {
				String line = in.readLine();
				if (line == null) {
					EOF = true;
				} else {
					result = KeyValueEntry.fromString(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Closes the IndexReader and all underlying IO mechanisms.
	 */
	public void close() {
		try {
			this.in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
