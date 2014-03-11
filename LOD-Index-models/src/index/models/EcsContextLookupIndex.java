package index.models;

import index.IndexWriter;
import io.NQuad;
import io.NQuadParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import tools.Constants;

public class EcsContextLookupIndex {

	public void run(File subjectSortedInput, File indexOutput) {
		try {
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			NQuadParser parserIn = new NQuadParser(subjectSortedInput);
			String currentSubjectURI = null;
			String context = null;
			TreeSet<String> signature = new TreeSet<String>();
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
				if (currentSubjectURI == null) {
					currentSubjectURI = nq.subject;
					context = nq.context;
				}
				if (! nq.subject.equals(currentSubjectURI)) {
					indexWriter.addToIndex(currentSubjectURI, signature);
					signature = new TreeSet<String>();
					currentSubjectURI = nq.subject;
					context = nq.context;
				}
			 	if (nq.predicate.equals(Constants.RDF_TYPE)) {
			 		signature.add("T-"+nq.object);
			 	}
			 	if (! nq.predicate.equals(Constants.RDF_TYPE)) {
			 		signature.add("P-"+nq.predicate);
			 	}
			}
			indexWriter.addToIndex(context, signature);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
