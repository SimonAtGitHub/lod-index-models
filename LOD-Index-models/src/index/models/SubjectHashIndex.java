package index.models;

import index.IndexWriter;
import io.NQuad;
import io.NQuadParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

public class SubjectHashIndex {

	public void run(File subjectSortedInput, File indexOutput) {
		try {
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			NQuadParser parserIn = new NQuadParser(subjectSortedInput);
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
				TreeSet<String> s = new TreeSet<String>();
				s.add(nq.subject);
				String po = nq.predicate+IndexWriter.KEY_SEPARATOR+nq.object;
				indexWriter.addToIndex(po, s);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	
}
