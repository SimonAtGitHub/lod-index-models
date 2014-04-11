package de.unikoblenz.west.ldim.index.models;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.BreakIterator;
import java.util.Locale;
import java.util.TreeSet;

import org.tartarus.snowball.ext.porterStemmer;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;

public class KeyWordIndex {

	/**
	 * The actual stemmer to be used. Defaults to the porter stemmer
	 */
	private porterStemmer stemmer = new porterStemmer();

	
	public void run(File subjectSortedInput, File indexOutput) {
		try {
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			NQuadParser parserIn = new NQuadParser(subjectSortedInput);
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
				if ((nq.object.startsWith("\"")) && (nq.object.length()>2)) {
					String in = nq.object.substring(1,nq.object.length()-2);
					if (in.length()>0) {
						BreakIterator wordIterator = BreakIterator.getWordInstance(Locale.ENGLISH);
						wordIterator.setText(in);
						int wordBoundary = wordIterator.first();
						int wordPrev = wordBoundary;
						while (wordBoundary != BreakIterator.DONE) {
							wordPrev = wordBoundary;
							wordBoundary = wordIterator.next();
							if (wordBoundary != BreakIterator.DONE) {
								String word = this.prepWord(in.substring(wordPrev, wordBoundary));
								if (word.length() > 0) {
									TreeSet<String> terms = new TreeSet<String>();
									terms.add(word);
									indexWriter.addToIndex(nq.subject, terms);
								}
							}
						}
					}						
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String prepWord(String word) {
		this.stemmer.setCurrent(word.trim().toLowerCase());
		this.stemmer.stem();
		return stemmer.getCurrent();
	}

	
}
