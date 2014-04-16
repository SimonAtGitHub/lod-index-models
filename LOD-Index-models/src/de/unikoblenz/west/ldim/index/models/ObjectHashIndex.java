package de.unikoblenz.west.ldim.index.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;


/**
 * Creates an index over the object values in NQuads. Each object is
 * associated with the subjects and predicates seen in the triples (effectively
 * this corresponds to the full triple as the object is known as well).
 * 
 * The input data is expected to be sorted by object ... even though it is not
 * necessarily required, this produces key sorted index files.
 * 
 * @author Thomas Gottron
 */
public class ObjectHashIndex {

	public void run(File objectSortedInput, File indexOutput) {
		try {
			// Write to an indexWriter
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			// Read from an NQuad Parser
			NQuadParser parserIn = new NQuadParser(objectSortedInput);
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
				// each nquad produces exactly one index entry, namely assigning the predicate and subject to their object key
				TreeSet<String> o = new TreeSet<String>();
				o.add(nq.object);
				String sp = nq.subject+IndexWriter.KEY_SEPARATOR+nq.predicate;
				indexWriter.addToIndex(sp, o);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
