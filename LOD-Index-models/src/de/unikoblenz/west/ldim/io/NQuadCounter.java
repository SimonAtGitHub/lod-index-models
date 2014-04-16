package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.IOException;

/**
 * Very simple class which provides one method to count the number of nquads in
 * a file. Simply opens the file with an NQuadParser, reads all quads and
 * increases a counter for each observed quad.
 * 
 * @author Thomas Gottron
 * 
 */
public class NQuadCounter {
	
	/**
	 * Reads the file entirely and counts the number of contained nquads.
	 *  
	 * @param nqFile File from which to read the nquads (may be gzip or zip compressed)
	 * @return number of encountered nquads
	 * @throws IOException
	 */
	public int countNQuads(File nqFile) throws IOException {
		int result = 0;
		NQuadParser parser = new NQuadParser(nqFile);
		while (parser.hasNext()) {
			parser.next();
			result++;
		}
		return result;
	}
}
