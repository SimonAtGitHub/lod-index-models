package de.unikoblenz.west.ldim.index.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;

/**
 * An index to lookup triples by the context in the nquads 
 * @author gottron
 *
 */
public class ContextHashIndex {

	public void run(File contextSortedInput, File indexOutput) {
		try {
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			NQuadParser parserIn = new NQuadParser(contextSortedInput);
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
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
