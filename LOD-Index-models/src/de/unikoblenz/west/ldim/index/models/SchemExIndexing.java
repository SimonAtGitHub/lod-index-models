package de.unikoblenz.west.ldim.index.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.index.RandomAccessIndexReader;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;
import de.unikoblenz.west.ldim.io.NQuadWriter;
import de.unikoblenz.west.ldim.tools.Constants;

public class SchemExIndexing {

	public static final String LITERAL_TYPE = "LITERAL";
	
	public void run(File subjectSortedInput, File typeSetIndex, File indexOutput) {
		try {
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			File dbDir = new File(typeSetIndex.getName()+"_JE_DIR");
			dbDir.mkdir();
			File tmpFile = new File(subjectSortedInput.getName()+"_OBJ_TS_LOOKUP.txt");
			RandomAccessIndexReader tsIndex = new RandomAccessIndexReader(typeSetIndex, dbDir);
			NQuadParser parserIn = new NQuadParser(subjectSortedInput);
			NQuadWriter nquadOut = new NQuadWriter(tmpFile);
			
			// First step: replace all objects of triples with their types
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
				StringBuffer builder = new StringBuffer();
				builder.append('<');
				String typeSetId = tsIndex.get(nq.object);
				builder.append(typeSetId);
				builder.append('>');
				nq.object = builder.toString();
				nquadOut.write(nq);
			}
			nquadOut.close();
			
			// Now index over property-TS combinations in triples
			NQuadParser parserModIn = new NQuadParser(tmpFile);
			String currentSubjectURI = null;
			TreeSet<String> propertyToTS = new TreeSet<String>();
			PrintStream out = new PrintStream(indexOutput);
			while (parserModIn.hasNext()) {
				NQuad nq = parserModIn.next();
				if (currentSubjectURI == null) {
					currentSubjectURI = nq.subject;
				}
				if (! nq.subject.equals(currentSubjectURI)) {
					indexWriter.addToIndex(currentSubjectURI, propertyToTS);
					propertyToTS = new TreeSet<String>();
					currentSubjectURI = nq.subject;
				}
			 	if (! nq.predicate.equals(Constants.RDF_TYPE)) {
			 		propertyToTS.add("P-"+nq.predicate+"_TS-"+nq.object);
			 	}
			}
			indexWriter.addToIndex(currentSubjectURI, propertyToTS);
			out.close();
			tmpFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
