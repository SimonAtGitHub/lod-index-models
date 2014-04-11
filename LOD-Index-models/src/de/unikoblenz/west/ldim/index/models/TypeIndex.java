package de.unikoblenz.west.ldim.index.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;
import de.unikoblenz.west.ldim.tools.Constants;

public class TypeIndex {
	
	public void run(File subjectSortedInput, File indexOutput) {
		try {
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			NQuadParser parserIn = new NQuadParser(subjectSortedInput);
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
			 	if (nq.predicate.equals(Constants.RDF_TYPE)) {
					TreeSet<String> rdfType = new TreeSet<String>();
			 		rdfType.add(nq.object);
					indexWriter.addToIndex(nq.subject, rdfType);
			 	}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
