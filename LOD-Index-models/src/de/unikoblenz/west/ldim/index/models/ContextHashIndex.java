package de.unikoblenz.west.ldim.index.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;

/**
 * Creates an index over the context values in NQuads. Each context is
 * associated with the triples.
 * 
 * The input data is expected to be sorted by context ... even though it is not
 * necessarily required, this produces key sorted index files.
 * 
 * @author Thomas Gottron
 * 
 */
public class ContextHashIndex {

	public void run(File contextSortedInput, File indexOutput) {
		try {
			// Write to an indexWriter
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			// Read from an NQuad Parser
			NQuadParser parserIn = new NQuadParser(contextSortedInput);
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
				// each nquad produces exactly one index entry, namely assigning the triple to their context key
				TreeSet<String> pld = new TreeSet<String>();
				pld.add(nq.context);
				String spo = nq.subject+IndexWriter.KEY_SEPARATOR+nq.predicate+IndexWriter.KEY_SEPARATOR+nq.object;
				indexWriter.addToIndex(spo, pld);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
