package de.unikoblenz.west.ldim.index.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;

public class ObjectHashIndex {

	public void run(File objectSortedInput, File indexOutput) {
		try {
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			NQuadParser parserIn = new NQuadParser(objectSortedInput);
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
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
