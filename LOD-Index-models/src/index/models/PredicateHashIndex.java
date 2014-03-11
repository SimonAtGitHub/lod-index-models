package index.models;

import index.IndexWriter;
import io.NQuad;
import io.NQuadParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

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
