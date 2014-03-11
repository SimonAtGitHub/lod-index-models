package index.join;

import index.KeyValueEntry;
import index.RandomAccessIndexReader;
import index.IndexWriter;
import index.SequentialIndexReader;
import io.NQuad;
import io.NQuadParser;
import io.NQuadWriter;

import java.io.File;
import java.io.IOException;

public class Join {

	public static final byte SUBJECT_JOIN = 1;
	public static final byte PREDICATE_JOIN = 2;
	public static final byte OBJECT_JOIN = 3;
	
	public void sortedJoin(File nquadIN, File indexFile, File nquadOutFile, byte mode) {
		try {
			SequentialIndexReader indexIn = new SequentialIndexReader(indexFile);
			NQuadParser parserIn = new NQuadParser(nquadIN);
			NQuadWriter nquadOut = new NQuadWriter(nquadOutFile);
			
			KeyValueEntry indexEntry = indexIn.nextEntry();
			// First step: replace all objects of triples with their types
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
				String match = null;
				switch (mode) {
				case Join.SUBJECT_JOIN:
					match = nq.subject;
					break;
				case Join.PREDICATE_JOIN:
					match = nq.predicate;
					break;
				case Join.OBJECT_JOIN:
					match = nq.object;
					break;
				}
				while ( (indexEntry!=null)  && (indexEntry.key.compareTo(match) < 0)) {
					indexEntry = indexIn.nextEntry();
				}
				String resolve = RandomAccessIndexReader.MISSING;
				if ( (indexEntry != null) && (indexEntry.key.equals(match)) ) {
					resolve = indexEntry.value;
				}
				resolve = '<'+resolve+'>';
				switch (mode) {
				case Join.SUBJECT_JOIN:
					nq.subject = resolve;
					break;
				case Join.PREDICATE_JOIN:
					nq.predicate = resolve;
					break;
				case Join.OBJECT_JOIN:
					nq.object = resolve;
					break;
				}
				nquadOut.write(nq);
			}
			indexIn.close();
			nquadOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
