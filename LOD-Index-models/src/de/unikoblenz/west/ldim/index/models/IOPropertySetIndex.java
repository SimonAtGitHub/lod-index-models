package de.unikoblenz.west.ldim.index.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.index.join.Join;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;
import de.unikoblenz.west.ldim.io.sorting.NQuadSorting;
import de.unikoblenz.west.ldim.tools.Constants;

public class IOPropertySetIndex {

	public void run(File propertySetIndex, File incomingPropertySetIndex, File indexOutput) {
		try {
			IndexWriter indexWriter = new IndexWriter(indexOutput);
/*
			Join join = new Join();
			// First step: replace all objects of triples with their types
			File tmpFile = new File(objectSortedInput.getName()+"_OBJ_TS_LOOKUP.txt");
			join.sortedJoin(objectSortedInput, typeSetIndex, tmpFile, Join.OBJECT_JOIN);
			File tmp2File = new File(objectSortedInput.getName()+"_OBJ_TS_LOOKUP-s-sort.txt");
			NQuadSorting nqSort = new NQuadSorting(NQuadSorting.SUBJECT_SORT);
			nqSort.run(tmpFile, tmp2File);
			
			// Now index over property-TS combinations in triples
			NQuadParser parserModIn = new NQuadParser(tmp2File);
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
			tmp2File.delete();
			*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
}
