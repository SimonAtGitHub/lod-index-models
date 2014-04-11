package de.unikoblenz.west.ldim.index.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Comparator;
import java.util.TreeSet;

import de.unikoblenz.west.ldim.index.IndexWriter;
import de.unikoblenz.west.ldim.io.NQuad;
import de.unikoblenz.west.ldim.io.NQuadParser;

public class PldHashIndex {
	
	private TreeSet<String> publicSuffixes = new TreeSet<String>(new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			int result = 0;
			result = o1.length() - o2.length();
			if (result == 0) {
				result = o1.compareTo(o2);
			}
			return result;
		}
		
	});
	
	public PldHashIndex(File publicSuffixListFile) {
		try {
		BufferedReader in = new BufferedReader(new FileReader(publicSuffixListFile));
		String line = null;
		while ( (line = in.readLine()) != null) {
			if (line.trim().length()>0) {
				if (! line.startsWith("//")) {
					publicSuffixes.add(line.trim());
				}
			}
		}
		in.close();
		} catch (IOException ioe) {
			// ignore
			System.err.println("PldHashIndex: Couldn't load Public Suffix list...");
		}
	}
	
	public void run(File contextSortedInput, File indexOutput) {
		try {
			IndexWriter indexWriter = new IndexWriter(indexOutput);
			NQuadParser parserIn = new NQuadParser(contextSortedInput);
			PrintStream out = new PrintStream(indexOutput);
			while (parserIn.hasNext()) {
				NQuad nq = parserIn.next();
				TreeSet<String> pld = new TreeSet<String>();
				// TODO : Convert context to PLD
				URL pldUri = new URL(nq.context.substring(1, nq.context.length()-2));
				String host = pldUri.getHost();
				String pldString = host;
				for (String suffix : this.publicSuffixes) {
					if (host.endsWith(suffix)) {
						pldString = host.substring(0, host.length() - suffix.length()-1);
						break;
					}
				}
				
				pld.add(pldString);
				String spo = nq.subject+IndexWriter.KEY_SEPARATOR+nq.predicate+IndexWriter.KEY_SEPARATOR+nq.object;
				indexWriter.addToIndex(spo, pld);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
