package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Merge the contents of various nquad files into one big file.
 * 
 * @author Thomas Gottron
 * 
 */
public class NQuadMerger {

	/**
	 * Takes a set of file references from which to read (in arbitrary order)
	 * and copy the contents of these files to the given output file.
	 * 
	 * @param snippets Files from which to read
	 * @param outFile File to which to write the nquads
	 * @throws IOException
	 */
	public void mergeFiles(Set<File> snippets, File outFile) throws IOException {
		NQuadWriter nqOut = new NQuadWriter(outFile);
		for (File snipFile : snippets) {
			NQuadParser nqIn = new NQuadParser(snipFile);
			while (nqIn.hasNext()) {
				nqOut.write(nqIn.next());
			}
		}
		nqOut.close();
	}
}
