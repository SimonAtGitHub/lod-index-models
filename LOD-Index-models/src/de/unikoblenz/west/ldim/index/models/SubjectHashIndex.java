package de.unikoblenz.west.ldim.index.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;

/**
 * Creates an index over the subject values in NQuads. Each subject is
 * associated with the objects and predicates seen in the triples (effectively
 * this corresponds to the full triple as the subject is known as well).
 * 
 * The input data is expected to be sorted by subject ... even though it is not
 * necessarily required, this produces key sorted index files.
 * 
 * @author Thomas Gottron
 * 
 */
public class SubjectHashIndex {

	public void run(File subjectSortedInput, File indexOutput) {
		try {
			// Write to an indexWriter
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			// Read from an NQuad Parser
			NQuadParser parserIn = new NQuadParser(subjectSortedInput);
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
				// each nquad produces exactly one index entry, namely assigning the predicate and object to their subject key
				TreeSet<String> s = new TreeSet<String>();
				s.add(nq.subject);
				String po = nq.predicate + IndexWriter.KEY_SEPARATOR
						+ nq.object;
				indexWriter.addToIndex(po, s);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
