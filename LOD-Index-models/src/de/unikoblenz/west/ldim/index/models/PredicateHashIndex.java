package de.unikoblenz.west.ldim.index.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;

/**
 * Creates an index over the predicate values in NQuads. Each predicate is
 * associated with the subjects and objects seen in the triples (effectively
 * this corresponds to the full triple as the predicate is known as well).
 * 
 * The input data is expected to be sorted by predicate ... even though it is not
 * necessarily required, this produces key sorted index files.
 * 
 * @author Thomas Gottron
 * 
 */
public class PredicateHashIndex {
	public void run(File predicateSortedInput, File indexOutput) {
		try {
			// Write to an indexWriter
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			// Read from an NQuad Parser
			NQuadParser parserIn = new NQuadParser(predicateSortedInput);
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
				// each nquad produces exactly one index entry, namely assigning the subject and object to their predicate key
				TreeSet<String> p = new TreeSet<String>();
				p.add(nq.predicate);
				String so = nq.subject+IndexWriter.KEY_SEPARATOR+nq.object;
				indexWriter.addToIndex(so, p);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
