package de.unikoblenz.west.ldim.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import de.unikoblenz.west.ldim.tools.Utilities;

/**
 * Takes an NQuad input file and splits it into smaller files based on the
 * provided context. The obtained result is directory in which multiple gzip
 * compressed nquad files are located, one file per identified context. The
 * filenames are based on md5 hashes of the context URIs.
 * 
 * @author Thomas Gottron
 * 
 */
public class NQuadFileSplitter {

	/**
	 * Maximal number of (output) files which may be open at the same time.
	 */
	public final static int MAX_OPEN_FILES = 32;

	/**
	 * File from which to read the nquads
	 */
	private File inFile = null;
	/**
	 * Directory from which to write the split nquad files.
	 */
	private File outDir = null;

	/**
	 * Initialize the splitter by giving an input file and an output directory
	 * to which to write the split files.
	 * 
	 * @param in
	 *            NQuad input file from which to read the data
	 * @param out
	 *            directory in which the output files will be written.
	 */
	public NQuadFileSplitter(File in, File out) {
		this.inFile = in;
		this.outDir = out;
	}

	public void run() throws IOException {
		TreeMap<String, NQuadWriter> nqWriterRefs = new TreeMap<String, NQuadWriter>();
		ArrayList<String> cache = new ArrayList<String>();
		NQuadParser nqIn = new NQuadParser(this.inFile);
		while (nqIn.hasNext()) {
			NQuad quad = nqIn.next();
			String hash = this.getBucketHash(quad);
			NQuadWriter nqOut = null;
			if (cache.contains(hash)) {
				nqOut = nqWriterRefs.get(hash);
				cache.remove(hash);
				cache.add(hash);
			} else {
				if (cache.size() >= MAX_OPEN_FILES) {
					String oldHash = cache.remove(0);
					nqWriterRefs.get(oldHash).close();
					nqWriterRefs.remove(oldHash);
				}
				File output = new File(this.outDir, hash + ".gz");
				nqOut = new NQuadWriter(output, true);
				nqWriterRefs.put(hash, nqOut);
				cache.add(hash);
			}
			nqOut.write(quad);
		}
		for (NQuadWriter nqOut : nqWriterRefs.values()) {
			nqOut.close();
		}
	}

	/**
	 * Capsulated method for getting the md5 hash for a context. 
	 * @param nquad
	 * @return
	 */
	public String getBucketHash(NQuad nquad) {
		String result = Utilities.md5Hash(nquad.context);
		return result;
	}

}
