package de.unikoblenz.west.ldim.index.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;

public class PredicateHashIndex {
	public void run(File predicateSortedInput, File indexOutput) {
		try {
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			NQuadParser parserIn = new NQuadParser(predicateSortedInput);
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
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
